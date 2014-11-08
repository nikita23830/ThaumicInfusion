package drunkmafia.thaumicinfusion.common;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemEssence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by DrunkMafia on 08/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ThaumcraftIntergration {

    public static void init() {
        InfusionRecipe coreRecipe = ThaumcraftApi.addInfusionCraftingRecipe("BLOCKINFUSION", new ItemStack(TIBlocks.infusionCoreBlock), 4, new AspectList().add(Aspect.ORDER, 80).add(Aspect.MAGIC, 40), new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), new ItemStack[]{new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6), new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 7), new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6), new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 7)});
        ShapedArcaneRecipe essentiaRecipe = null;

        for (Aspect aspect : Aspect.aspects.values()) {
            for (int i = 0; i <= 2; i++) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("aspectTag", aspect.getTag());
                ItemStack stack = new ItemStack(TIBlocks.essentiaBlock);
                stack.setItemDamage(i);
                stack.setTagCompound(tag);
                stack.setStackDisplayName(aspect.getName() + (i != 0 ? (i == 1 ? ThaumicInfusion.translate("key.essentiaBlock.brick") : ThaumicInfusion.translate("key.essentiaBlock.chiseled")) : ""));

                ItemStack phial = new ItemStack(ConfigItems.itemEssence, 1, 1);
                ((ItemEssence) phial.getItem()).setAspects(phial, new AspectList().add(aspect, 8));

                ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("ESSENTIABLOCKS", stack, new AspectList().add(Aspect.ENTROPY, 10), "PP", "PP", Character.valueOf('P'), phial);
                if (essentiaRecipe == null)
                    essentiaRecipe = recipe;
            }
        }

        ResearchCategories.registerCategory("THAUMICINFUSION", new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));

        new ResearchItem("BLOCKINFUSION", "THAUMICINFUSION", new AspectList().add(Aspect.ORDER, 3).add(Aspect.MAGIC, 3), -2, 0, 2, new ItemStack(TIBlocks.infusionCoreBlock)).setPages(new ResearchPage("tc.research_page.BLOCKINFUSION.1"), new ResearchPage("tc.research_page.BLOCKINFUSION.2"), new ResearchPage(coreRecipe)).registerResearchItem();
        new ResearchItem("ESSENTIABLOCKS", "THAUMICINFUSION", new AspectList().add(Aspect.ORDER, 3).add(Aspect.MAGIC, 3), 2, 0, 2, new ItemStack(TIBlocks.essentiaBlock)).setPages(new ResearchPage("tc.research_page.ESSENTIABLOCKS.1"), new ResearchPage(essentiaRecipe)).registerResearchItem();

        ThaumcraftApi.getCraftingRecipes().add(new BlockInfusionRecipe("", 10));
    }
}

class BlockInfusionRecipe extends InfusionRecipe {

    public BlockInfusionRecipe(String research, int inst) {
        super(research, null, inst, null, null, null);
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        recipeOutput = null;

        if (research.length()>0 && !ThaumcraftApiHelper.isResearchComplete(player.getCommandSenderName(), research)) {
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
            if(aspect == null)
                return false;


            Class effect = AspectHandler.getEffectFromAspect(aspect);
            if(effect == null)
                return false;
            Annotation annot = effect.getAnnotation(Effect.class);
            if(annot instanceof Effect) {
                Effect effe = (Effect) annot;
                int amount = effe.cost() * central.stackSize;
                infuseAspects.add(aspect, amount);
            }else return false;
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

            recipeOutput = InfusionHelper.getInfusedItemStack(InfusionHelper.phialsToAspects(input), Block.getIdFromBlock(((ItemBlock) central.getItem()).field_150939_a), central.stackSize, central.getItemDamage());
        }
        System.out.println((recipeOutput != null && ii.size() > 0));

        return recipeOutput != null && ii.size() > 0;
    }
}
