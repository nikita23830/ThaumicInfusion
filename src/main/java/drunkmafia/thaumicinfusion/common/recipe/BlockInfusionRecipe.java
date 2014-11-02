package drunkmafia.thaumicinfusion.common.recipe;

import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.block.tile.InfusionCoreTile;
import drunkmafia.thaumicinfusion.common.util.InfusionHelper;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.items.ItemEssence;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by DrunkMafia on 29/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockInfusionRecipe extends InfusionRecipe {

    public BlockInfusionRecipe(String research, int inst) {
        super(research, null, inst, null, null, null);
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        recipeOutput = null;

        if (research.length()>0 && !ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), research)) {
            System.out.println("Failed first check - research?!");
            return false;
        }

        boolean isStackSetToInfuse = false;
        for(ItemStack check : InfusionCoreTile.infuseStacksTemp) {
            isStackSetToInfuse = check.getItem() == central.getItem() && check.getItemDamage() == central.getItemDamage() && check.stackSize == central.stackSize;
            if (isStackSetToInfuse)
                break;
        }

        //Replace with block check
        if (!(central.getItem() instanceof ItemBlock) || !isStackSetToInfuse)
            return false;

        ArrayList<ItemStack> ii = new ArrayList<ItemStack>();
        for (ItemStack is:input)
            if(is.getItem() instanceof ItemEssence) {
                ii.add(is.copy());
            }else return false;

        AspectList infuseAspects = new AspectList();

        for(ItemStack phial : ii){
            AspectList phialList = ((ItemEssence)phial.getItem()).getAspects(phial);
            Aspect aspect = phialList.getAspects()[0];
            if(aspect == null) {
                System.out.println("Aspect is null");
                return false;
            }
            Class effect = AspectHandler.getEffectFromAspect(aspect);
            Effect annot = (Effect) effect.getAnnotation(Effect.class);
            int amount = annot.cost() * central.stackSize;
            System.out.println(aspect.getName() + " need: " + amount);
            infuseAspects.add(aspect, amount);
        }

        aspects = infuseAspects;

        if(ii.size() > 0) {
            try {
                Field recipeInput = InfusionRecipe.class.getDeclaredField("recipeInput");
                Field components = InfusionRecipe.class.getDeclaredField("components");

                recipeInput.setAccessible(true);
                components.setAccessible(true);

                recipeInput.set(this, central);

                ItemStack[] comps = new ItemStack[ii.size()];
                for(int i = 0; i < comps.length; i++)
                    comps[i] = ii.get(i);

                components.set(this, comps);
            }catch (Exception e){
                e.printStackTrace();
            }

            recipeOutput = InfusionHelper.getInfusedItemStack(InfusionHelper.phialsToAspects(input), Block.getIdFromBlock(((ItemBlock)central.getItem()).field_150939_a), central.stackSize, central.getItemDamage());
        }
        System.out.println("Mathes finshed: " + (recipeOutput != null && ii.size() > 0));

        return recipeOutput != null && ii.size() > 0;
    }
}
