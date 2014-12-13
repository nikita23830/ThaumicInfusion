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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;

import java.util.*;

import static drunkmafia.thaumicinfusion.common.lib.ModInfo.LOGGER_ID;

public class AspectHandler {
    private static ArrayList<Class<? extends AspectEffect>> effectsToRegister = new ArrayList<Class<? extends AspectEffect>>();
    private static HashMap<Aspect, Class> registeredEffects = new HashMap<Aspect, Class>();

    public static void preInit(){
        Logger logger = ThaumicInfusion.instance.logger;
        if(isInCorretState(LoaderState.PREINITIALIZATION)) {
            logger.warn(LOGGER_ID + "Pre Init cannot be called outside it's state");
            return;
        }
        Loader loader = Loader.instance();

        ClassFinder classFinder = new ClassFinder(AspectEffect.class);
        for(ModContainer mod : loader.getActiveModList())
            if (mod != null && !mod.getModId().matches("mcp") && !mod.getModId().matches("FML") && !mod.getModId().matches("Forge"))
                classFinder.processFile(mod.getSource().getAbsolutePath(), "");

        Set<Class<? extends AspectEffect>> classes = classFinder.getClasses();

        if(classes == null || classes.size() == 0){
            logger.warn(LOGGER_ID + "Failed to register any effects, mod is being disabled");
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

        logger.info(LOGGER_ID + effectsToRegister.size() + "effects have been detected and have been loaded into memory");
    }

    public static void postInit(){
        Logger logger = ThaumicInfusion.instance.logger;
        if(isInCorretState(LoaderState.POSTINITIALIZATION)) {
            logger.warn(LOGGER_ID + "Post Init cannot be called outside it's state");
            return;
        }

        for(Class<? extends AspectEffect> effect : effectsToRegister){
            Effect annotation = effect.getAnnotation(Effect.class);
            Aspect aspect = Aspect.getAspect(annotation.aspect().toLowerCase());
            if(aspect != null) {
                if (!registeredEffects.containsKey(aspect) && !registeredEffects.containsValue(effect))
                    registeredEffects.put(aspect, effect);
            }else
                logger.log(Level.ERROR,LOGGER_ID +  "Aspect: " + annotation.aspect() + " does not exist in the instance");
        }

        logger.info(LOGGER_ID + registeredEffects.size() + " effects have been binded to their aspect, Failed to find: " + (effectsToRegister.size() - registeredEffects.size()) + " effects aspects.");
        effectsToRegister = null;
    }

    private static boolean isInCorretState(LoaderState state){
        Loader loader = Loader.instance();
        return !loader.isInState(state) && loader.activeModContainer().getModId().matches(ModInfo.MODID);
    }

    private static boolean isClassAEffect(Class<?> c){
        return c != null && c.isAnnotationPresent(Effect.class) && AspectEffect.class.isAssignableFrom(c);
    }

    public static Class getEffectFromAspect(Aspect aspects) {
        return registeredEffects.get(aspects);
    }

    public static int getCostOfEffect(Aspect aspect){
        Class c = getEffectFromAspect(aspect);
        if(c == null || (c != null && c.getAnnotation(Effect.class) == null))
            return -1;
        Effect annot = (Effect) c.getAnnotation(Effect.class);
        return annot.cost();
    }

    public static Aspect[] getAspects(){
        Map.Entry<Aspect, Class>[] entries = registeredEffects.entrySet().toArray(new Map.Entry[registeredEffects.size()]);
        Aspect[] aspects = new Aspect[entries.length];
        for(int i = 0; i < aspects.length; i++)
            aspects[i] = entries[i].getKey();
        return aspects;
    }

    public static Aspect getAspectsFromEffect(Class effect) {
        if(effect.isAnnotationPresent(Effect.class)){
            Effect annotation = (Effect) effect.getAnnotation(Effect.class);
            return Aspect.getAspect(annotation.aspect());
        }
        return null;
    }

    public static EffectGUI getEffectGUI(Class effect){
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
