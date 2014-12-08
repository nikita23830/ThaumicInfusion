package drunkmafia.thaumicinfusion.common.aspect;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.lib.BlockInfo;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.EffectGUI;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.aspects.Aspect;

import java.util.*;

public class AspectHandler {

    private static ArrayList<Class> classesToRegister = new ArrayList<Class>();
    private static ArrayList<Class<? extends AspectEffect>> effectsToRegister = new ArrayList<Class<? extends AspectEffect>>();

    private static HashMap<Aspect, Class> registeredEffects = new HashMap<Aspect, Class>();

    /**
     * Can only be called during the {@link cpw.mods.fml.common.event.FMLConstructionEvent} state, once registered the effect will be handled.
     */
    public static void addEffect(Class<? extends AspectEffect>... classes){
        Loader loader = Loader.instance();
        if(!loader.isInState(LoaderState.CONSTRUCTING)) {
            ThaumicInfusion.instance.logger.fatal(loader.activeModContainer().getName() + " Attempted to register a package to the aspect handler outside the COSTRUCTION state.");
            return;
        }
        for(Class<? extends AspectEffect> c : classes)
            if(!classesToRegister.contains(c))
                classesToRegister.add(c);
    }

    public static void preInit(){
        Logger logger = ThaumicInfusion.instance.logger;
        if(isInCorretState(LoaderState.PREINITIALIZATION)) {
            logger.warn("Pre Init cannot be called outside it's state");
            return;
        }

        for(Class<?> c : classesToRegister) {
            if (isClassAEffect(c)) {
                try {
                    Class<? extends AspectEffect> effect = (Class<? extends AspectEffect>) c;
                    Effect annotation = effect.getAnnotation(Effect.class);
                    if(annotation.hasCustomBlock() || annotation.aspect().equals("default")) {
                        AspectEffect effectInstace = effect.newInstance();

                        InfusedBlock block = effectInstace.getBlock();
                        block.setBlockName(BlockInfo.infusedBlock_UnlocalizedName + "." + annotation.aspect());

                        if (!BlockHandler.hasBlock(block.getUnlocalizedName())) {
                            GameRegistry.registerBlock(block.addBlockToHandler(), "reg_InfusedBlock" + annotation.aspect());
                            TileEntity tileEntity = effectInstace.createTileEntity(null, 0);
                            if (tileEntity != null)
                                GameRegistry.registerTileEntity(tileEntity.getClass(), "tile_InfusedBlock" + annotation.aspect());
                        }
                    }
                    if(annotation.aspect() != "default")
                        effectsToRegister.add(effect);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void postInit(){
        Logger logger = ThaumicInfusion.instance.logger;
        if(isInCorretState(LoaderState.POSTINITIALIZATION)) {
            logger.warn("Post Init cannot be called outside it's state");
            return;
        }

        for(Class<? extends AspectEffect> effect : effectsToRegister){
            Effect annotation = effect.getAnnotation(Effect.class);
            Aspect aspect = Aspect.getAspect(annotation.aspect().toLowerCase());
            if(aspect != null) {
                if (!registeredEffects.containsKey(aspect) && !registeredEffects.containsValue(effect))
                    registeredEffects.put(aspect, effect);
            }else
                logger.log(Level.ERROR, "Aspect: " + annotation.aspect() + " does not exist in the instance");
        }
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
