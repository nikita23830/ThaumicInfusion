package drunkmafia.thaumicinfusion.common.util;

import drunkmafia.thaumicinfusion.common.CommonProxy;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.ItemEssence;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class InfusionHelper {

    public static int getBlockID(Class[] aspects){
        int defBlock = Block.getIdFromBlock(BlockHandler.getBlock("default"));
        for(Class aspect : aspects){
            if(aspect.isAnnotationPresent(Effect.class)) {
                Effect annotation = (Effect) aspect.getAnnotation(Effect.class);
                int effectBlock = Block.getIdFromBlock(BlockHandler.getBlock(annotation.infusedBlock()));
                if(defBlock != effectBlock) return effectBlock;
            }
        }
        return defBlock;
    }

    public static int getInfusedID(ItemStack stack){
        NBTTagCompound tag = stack.getTagCompound();
        if(tag != null) {
            return tag.getInteger("infusedID");
        }
        return -1;
    }

    public static Class[] getEffectsFromStack(ItemStack stack){
        NBTTagCompound tag = stack.stackTagCompound;
        if(stack.stackTagCompound != null && tag.hasKey("infusedAspect_Size")) {
            Class[] effects = new Class[tag.getInteger("infusedAspect_Size")];
            for (int i = 0; i < effects.length; i++) {
                try {
                    Class c = Class.forName(tag.getString("infusedAspect_" + i));
                    if (c != null) effects[i] = c;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return effects;
        }else return null;
    }

    private static Class[] getEffectsFromList(ArrayList<Aspect> list) {
        Class[] effects = new Class[list.size()];
        for(int i = 0; i < effects.length; i++) {
            effects[i] = AspectHandler.getEffectFromAspect(list.get(i));
            System.out.println(effects[i]);
        }
        return effects;
    }

    public static ArrayList<Aspect> phialsToAspects(ArrayList<ItemStack> stacks){
        ArrayList<Aspect> aspects = new ArrayList<Aspect>();
        for(ItemStack stack : stacks) {
            if (stack.getItem() instanceof ItemEssence) {
                AspectList list = new AspectList();
                list.readFromNBT(stack.getTagCompound());
                aspects.add(list.getAspects()[0]);
            }
        }
        return aspects;
    }

    public static ItemStack getInfusedItemStack(ArrayList<Aspect> list, int infusedID, int size, int meta){
        if(list == null) return null;
        Class[] effects = getEffectsFromList(list);
        if(effects.length == 0){
            System.out.println("Failed to get an effect list");
            return null;
        }
        int blockID = getBlockID(effects);
        if(blockID == -1){
            System.out.println("Failed to get a block");
            return null;
        }
        ItemStack stack = new ItemStack(Block.getBlockById(blockID), size, meta);
        NBTTagCompound tag = new NBTTagCompound();
        for(int i = 0; i < effects.length; i++)
            tag.setString("infusedAspect_" + i, effects[i].getName());

        tag.setInteger("infusedAspect_Size", effects.length);
        tag.setInteger("infusedID", infusedID);
        stack.setTagCompound(tag);

        stack.setStackDisplayName("Infused " + new ItemStack(Block.getBlockById(infusedID), 1, meta).getDisplayName());
        System.out.println("Infused stack obtained");
        return stack;
    }
}
