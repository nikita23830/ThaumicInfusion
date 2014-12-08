package drunkmafia.thaumicinfusion.common.util;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.ItemEssence;

import java.util.ArrayList;

public class InfusionHelper {

    public static AspectList getInfusedAspects(ItemStack stack){
        if (!isInfusedStack(stack))
            return null;

        AspectList list = new AspectList();
        Class[] effects = getEffectsFromStack(stack);
        for(Class c : effects){
            Effect effect = (Effect) c.getAnnotation(Effect.class);
            list.add(Aspect.getAspect(effect.aspect()), effect.cost());
        }

        return list;
    }

    public static AspectList addBlockAspects(ItemStack stack){
        if(!isInfusedStack(stack))
            return null;

        AspectList list = new AspectList();
        NBTTagCompound tagCompound = stack.getTagCompound();

        AspectList blockList = new AspectList(new ItemStack(Block.getBlockById(tagCompound.getInteger("infusedID")), 1, stack.getItemDamage()));
        for(int i = 0; i < blockList.size(); i++){
            Aspect aspect = blockList.getAspects()[i];
            list.add(aspect, blockList.getAmount(aspect));
        }

        return list;
    }

    public static boolean isInfusedStack(ItemStack stack){
        if(stack == null || (stack != null && !(Block.getBlockFromItem(stack.getItem()) instanceof InfusedBlock)))
            return false;
        return stack.hasTagCompound();
    }

    public static int getBlockID(Class[] aspects){
        int defBlock = Block.getIdFromBlock(BlockHandler.getBlock("default"));
        for(Class aspect : aspects){
            if(aspect.isAnnotationPresent(Effect.class)) {
                Effect annotation = (Effect) aspect.getAnnotation(Effect.class);
                if(annotation.hasCustomBlock())
                    return Block.getIdFromBlock(BlockHandler.getBlock(annotation.aspect()));
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
        for(int i = 0; i < effects.length; i++)
            effects[i] = AspectHandler.getEffectFromAspect(list.get(i));

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
        if(effects.length == 0)
            return null;

        int blockID = getBlockID(effects);
        if(blockID == -1)
            return null;

        ItemStack stack = new ItemStack(Block.getBlockById(blockID), size, meta);
        NBTTagCompound tag = new NBTTagCompound();
        for(int i = 0; i < effects.length; i++)
            tag.setString("infusedAspect_" + i, effects[i].getName());

        tag.setInteger("infusedAspect_Size", effects.length);
        tag.setInteger("infusedID", infusedID);
        stack.setTagCompound(tag);

        stack.setStackDisplayName(ThaumicInfusion.translate("key.infusedBlock.infused") + " " + new ItemStack(Block.getBlockById(infusedID), 1, meta).getDisplayName());
        return stack;
    }
}
