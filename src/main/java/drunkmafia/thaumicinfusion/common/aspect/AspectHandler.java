package drunkmafia.thaumicinfusion.common.aspect;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.lib.BlockInfo;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.ClassFinder;
import drunkmafia.thaumicinfusion.common.util.EffectGUI;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AspectHandler {

    private static AspectHandler instance;

    private ArrayList<ModContainer> modsForEffects = new ArrayList<ModContainer>();

    private ArrayList<Class<? extends AspectEffect>> effectsToRegister = new ArrayList<Class<? extends AspectEffect>>();
    private HashMap<Aspect, Class> registeredEffects = new HashMap<Aspect, Class>();
    private HashMap<Aspect, Aspect[]> opposites = new HashMap<Aspect, Aspect[]>();

    public static AspectHandler getInstance(){
        return instance != null ? instance : (instance = new AspectHandler());
    }

    private AspectHandler(){}

    public void addMod(){
        if(isInCorretState(LoaderState.CONSTRUCTING)) {
            ThaumicInfusion.instance.logger.warn("Cannot Add an effect path outside the constructing stage");
            return;
        }

        modsForEffects.add(Loader.instance().activeModContainer());
    }

    public void preInit(){
        Logger logger = ThaumicInfusion.instance.logger;
        if(isInCorretState(LoaderState.PREINITIALIZATION)) {
            logger.warn("Pre Init cannot be called outside it's state");
            return;
        }

        ClassFinder classFinder = new ClassFinder(AspectEffect.class);
        for(ModContainer mod : modsForEffects)
            classFinder.processFile(mod.getSource().getAbsolutePath(), "");


        Set<Class<? extends AspectEffect>> classes = classFinder.getClasses();

        if(classes == null || classes.size() == 0){
            logger.warn("Failed to register any effects");
            return;
        }

        for(Class<?> c : classes) {
            if (isClassAEffect(c)) {
                try {
                    Class<? extends AspectEffect> effect = (Class<? extends AspectEffect>) c;
                    Effect annotation = effect.getAnnotation(Effect.class);
                    AspectEffect effectInstace = effect.newInstance();

                    if(effectInstace.shouldRegister()) {
                        if (annotation.hasCustomBlock() || annotation.aspect().equals("default")) {
                            InfusedBlock block = effectInstace.getBlock();
                            block.setBlockName(BlockInfo.infusedBlock_UnlocalizedName + "." + annotation.aspect());

                            if (!BlockHandler.hasBlock(block.getUnlocalizedName()))
                                GameRegistry.registerBlock(block.addBlockToHandler(), "reg_InfusedBlock" + annotation.aspect());
                        }

                        if (annotation.hasTileEntity()) {
                            TileEntity tileEntity = effectInstace.getTile();
                            if (tileEntity != null)
                                GameRegistry.registerTileEntity(tileEntity.getClass(), "tile_InfusedBlock" + annotation.aspect());
                        }

                        if (annotation.aspect() != "default")
                            effectsToRegister.add(effect);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        logger.info(effectsToRegister.size() + "effects have been detected and have been loaded into memory");
    }

    public void postInit(){
        Logger logger = ThaumicInfusion.instance.logger;
        if(isInCorretState(LoaderState.POSTINITIALIZATION)) {
            logger.warn("Post Init cannot be called outside it's state");
            return;
        }

        Configuration config = ThaumicInfusion.instance.config;
        config.load();
        config.addCustomCategoryComment("Aspects", "The enabling and disabling of effects - NOTE: These MUST be the same as the server config or MAJOR de-syncs will be caused.");

        for(Class<? extends AspectEffect> effect : effectsToRegister){
            Effect annotation = effect.getAnnotation(Effect.class);
            Aspect aspect = Aspect.getAspect(annotation.aspect().toLowerCase());
            if(aspect != null) {
                if (!registeredEffects.containsKey(aspect) && !registeredEffects.containsValue(effect) && config.get("Aspects", aspect.getName(), true).getBoolean())
                    registeredEffects.put(aspect, effect);
            }else
                logger.log(Level.ERROR, "Aspect: " + annotation.aspect() + " does not exist in the instance");
        }

        for(Aspect aspect : getAspects())
            opposites.put(aspect, calculateEffectOpposites(aspect));


        config.save();
        logger.info(registeredEffects.size() + " effects have been binded to their aspect, Failed to find: " + (effectsToRegister.size() - registeredEffects.size()) + " effects aspects.");
        effectsToRegister = null;
    }

    private Aspect[] calculateEffectOpposites(Aspect aspect){
        try {
            ArrayList<Aspect> aspects = new ArrayList<Aspect>();
            AspectEffect effect = (AspectEffect) getEffectFromAspect(aspect).newInstance();
            for(Aspect checking : getAspects()){
                for(Method method : getEffectFromAspect(checking).getDeclaredMethods()) {
                    if (effect.hasMethod(method.getName())) {
                        aspects.add(checking);
                        break;
                    }
                }
            }
            return aspects.toArray(new Aspect[aspects.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Aspect[0];
    }

    private boolean isInCorretState(LoaderState state){
        Loader loader = Loader.instance();
        return !loader.isInState(state) && loader.activeModContainer().getModId().matches(ModInfo.MODID);
    }

    private boolean isClassAEffect(Class<?> c){
        return c != null && c.isAnnotationPresent(Effect.class) && AspectEffect.class.isAssignableFrom(c);
    }

    public Class getEffectFromAspect(Aspect aspects) {
        return registeredEffects.get(aspects);
    }

    public boolean canInfuse(Aspect[] aspects){
        for(Aspect aspect : aspects){
            Aspect[] opposite = opposites.get(aspect);
            if(opposite.length > 0) {
                for (Aspect checking : aspects) {
                    if (checking != aspect) {
                        for (Aspect checkingOpposite : opposite) {
                            if (checkingOpposite == checking)
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public int getCostOfEffect(Aspect aspect){
        Class c = getEffectFromAspect(aspect);
        if(c == null || (c != null && c.getAnnotation(Effect.class) == null))
            return -1;
        Effect annot = (Effect) c.getAnnotation(Effect.class);
        return annot.cost();
    }

    public Aspect[] getAspects(){
        Map.Entry<Aspect, Class>[] entries = registeredEffects.entrySet().toArray(new Map.Entry[registeredEffects.size()]);
        Aspect[] aspects = new Aspect[entries.length];
        for(int i = 0; i < aspects.length; i++)
            aspects[i] = entries[i].getKey();
        return aspects;
    }

    public Aspect getAspectsFromEffect(Class effect) {
        if(effect.isAnnotationPresent(Effect.class)){
            Effect annotation = (Effect) effect.getAnnotation(Effect.class);
            return Aspect.getAspect(annotation.aspect());
        }
        return null;
    }

    public EffectGUI getEffectGUI(Class effect){
        if(effect.isAnnotationPresent(Effect.class)){
            Effect annotation = (Effect) effect.getAnnotation(Effect.class);
            if(annotation.gui() != Object.class && EffectGUI.class.isAssignableFrom(annotation.gui())){
                try {
                    return (EffectGUI) annotation.gui().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}