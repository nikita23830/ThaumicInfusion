package drunkmafia.thaumicinfusion.common.aspect;

import com.esotericsoftware.reflectasm.MethodAccess;
import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.aspect.effect.vanilla.*;
import drunkmafia.thaumicinfusion.common.util.EffectGUI;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;

import java.lang.reflect.Method;
import java.util.*;

public class AspectHandler {

    public static IIconRegister iconRegister;

    private static HashMap<Aspect, Class> registeredEffects = new HashMap<Aspect, Class>();
    private static HashMap<Class, HashMap<String, Integer>> effectMethods = new HashMap<Class,HashMap<String, Integer>>();

    public static void initEffects(){
        try {
            registerEffect(Lux.class);
            registerEffect(Ignis.class);
            registerEffect(Motus.class);
            registerEffect(Potentia.class);
            registerEffect(Sensus.class);
            registerEffect(Permutatio.class);
            registerEffect(Aqua.class);
            registerEffect(Limus.class);
            registerEffect(Messis.class);
            registerEffect(Pannus.class);
            registerEffect(Iter.class);
            registerEffect(Gelum.class);
            registerEffect(Tempestas.class);
            registerEffect(Tenebrae.class);
            registerEffect(Vacuos.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void registerEffect(Class effect) throws Exception {
        if (effect.isAnnotationPresent(Effect.class) && AspectEffect.class.isAssignableFrom(effect)) {
            Effect annotation = (Effect) effect.getAnnotation(Effect.class);
            Aspect aspect = Aspect.getAspect(annotation.aspect().toLowerCase());
            if (!registeredEffects.containsKey(aspect) && !registeredEffects.containsValue(effect)) {
                registerTile(annotation);
                registeredEffects.put(aspect, effect);
                phaseEffect(effect);
            }
        }
    }

    public static void registerIcons(){
        if(iconRegister == null)
            return;
        for(Map.Entry ent : registeredEffects.entrySet()){
            try {
                Class effect = (Class) ent.getValue();
                Method meth = effect.getDeclaredMethod("registerIcons", IIconRegister.class);
                meth.invoke(effect.newInstance(), iconRegister);
            }catch (Exception e){}
        }
    }

    private static void registerTile(Effect annotation){
        if(annotation.hasTileEntity() && TileEntity.class.isAssignableFrom(annotation.tileentity())){
            Class<? extends TileEntity> tile = (Class<? extends TileEntity>) annotation.tileentity();
            GameRegistry.registerTileEntity(tile,"tile_" + tile.getSimpleName());
        }
    }

    private static void phaseEffect(Class effect){
        MethodAccess methodAccess = MethodAccess.get(effect);
        String[] methods = methodAccess.getMethodNames();

        HashMap<String, Integer> effectsMeth = new HashMap<String, Integer>();
        for(String name : methods) {
            effectsMeth.put(name, methodAccess.getIndex(name));
        }
        effectMethods.put(effect, effectsMeth);
    }

    public static int getMethod(Class effect, String name){
        if(effectMethods.get(effect).containsKey(name))
            return effectMethods.get(effect).get(name);
        return -1;
    }

    public static boolean isAspectAnEffect(Aspect aspects){
        return registeredEffects.containsKey(aspects);
    }

    public static Class getEffectFromAspect(Aspect aspects) {
        return registeredEffects.get(aspects);
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
