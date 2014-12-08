package drunkmafia.thaumicinfusion.common.intergration;

import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.aspect.effect.vanilla.*;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.block.tile.InfusionCoreTile;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.ClassLoading;
import drunkmafia.thaumicinfusion.common.util.InfusionHelper;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by DrunkMafia on 08/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ThaumcraftIntergration {

    public static void init() {
        InfusionRecipe coreRecipe = ThaumcraftApi.addInfusionCraftingRecipe("BLOCKINFUSION", new ItemStack(TIBlocks.infusionCoreBlock), 4, new AspectList().add(Aspect.ORDER, 80).add(Aspect.MAGIC, 40), new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), new ItemStack[]{new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6), new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 7), new ItemStack(ConfigBlocks.blockCosmeticSolid, 9, 6), new ItemStack(ConfigBlocks.blockCosmeticSolid, 4, 7)});
        ShapedArcaneRecipe essentiaRecipe = null;
        ItemStack essentiaBlock = null;

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
                if(essentiaBlock == null)
                    essentiaBlock = stack;
            }
        }

        ResearchCategories.registerCategory("THAUMICINFUSION", new ResourceLocation(ModInfo.MODID, "textures/research/r_ti.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));

        ItemStack empty = new ItemStack(ConfigBlocks.blockHole, 1, 15);
        List core = Arrays.asList(new Object[]{new AspectList().add(Aspect.FIRE, 25).add(Aspect.EARTH, 25).add(Aspect.ORDER, 25).add(Aspect.AIR, 25).add(Aspect.ENTROPY, 25).add(Aspect.WATER, 25), Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3), Arrays.asList(new ItemStack[]{empty, null, empty, null, new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), null, empty, null, empty, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), null, null, null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6), new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), null, new ItemStack(TIBlocks.infusionCoreBlock), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7), null, new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7)})});

        new ResearchItem("BLOCKINFUSION", "THAUMICINFUSION", new AspectList().add(Aspect.ORDER, 3).add(Aspect.MAGIC, 3), -2, 0, 2, new ItemStack(TIBlocks.infusionCoreBlock)).setPages(new ResearchPage("tc.research_page.BLOCKINFUSION.1"), new ResearchPage(coreRecipe), new ResearchPage("tc.research_page.BLOCKINFUSION.2"), new ResearchPage(core)).registerResearchItem();
        new ResearchItem("ESSENTIABLOCKS", "THAUMICINFUSION", new AspectList().add(Aspect.ORDER, 3).add(Aspect.MAGIC, 3), 2, 0, 2, essentiaBlock).setPages(new ResearchPage("tc.research_page.ESSENTIABLOCKS.1"), new ResearchPage(essentiaRecipe)).registerResearchItem();

        new ResearchItem("ASPECTEFFECTS", "THAUMICINFUSION", new AspectList(), 0, 2, 2, new ResourceLocation("thaumcraft", "textures/misc/r_aspects.png")).setPages(getPages()).setAutoUnlock().registerResearchItem();

        ThaumcraftApi.getCraftingRecipes().add(new BlockInfusionRecipe("", 10));
    }

    private static AspectEffectPage[] getPages(){
        Aspect[] aspects = AspectHandler.getAspects();
        Aspect[] current = new Aspect[3];
        ArrayList<AspectEffectPage> pages = new ArrayList<AspectEffectPage>();
        int index = 0;
        for(Aspect aspect : aspects){
            if(aspect != null) {
                current[index] = aspect;
                if (index == 1) {
                    pages.add(new AspectEffectPage(current));
                    current = new Aspect[2];
                    index = 0;
                } else
                    index++;
            }
        }
        AspectEffectPage[] researchPages = new AspectEffectPage[pages.size()];
        for(int p = 0; p < researchPages.length; p++)
            researchPages[p] = pages.get(p);

        return researchPages;
    }

    public static void registerEffects(){
        AspectHandler.addEffect(
                AspectEffect.class,
                Aqua.class,
                Gelum.class,
                Ignis.class,
                Limus.class,
                Lux.class,
                Messis.class,
                Pannus.class,
                Potentia.class,
                Sensus.class,
                Spiritus.class,
                Tempestas.class,
                Tenebrae.class,
                Vacuos.class,
                Alienis.class,
                Vitium.class,
                Bestia.class,
                Fames.class,
                Fabrico.class,
                Volatus.class,
                Cognitio.class,
                Sano.class,
                Infernus.class,
                Superbia.class,
                Tutamen.class);
    }
}

class AspectEffectPage extends ResearchPage {

    Aspect[] aspects;

    public AspectEffectPage(Aspect[] aspects) {
        super("");
        this.aspects = aspects;
    }

    @Override
    public String getTranslatedText() {
        String str = "";
        for(Aspect aspect : aspects){
            if(aspect != null) {
                ResourceLocation location = aspect.getImage();
                str += "<IMG>" + location.getResourceDomain() + ":" + location.getResourcePath() + ":0:0:255:255:0.125</IMG>" + aspect.getName() + " Cost: " + AspectHandler.getCostOfEffect(aspect) + " " + ThaumicInfusion.translate("ti.effect_info." + aspect.getName().toUpperCase()) + "\n";
            }
        }
        return str;
    }
}

class BlockInfusionRecipe extends InfusionRecipe {

    public BlockInfusionRecipe(String research, int inst) {
        super(research, null, inst, null, null, null);
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        recipeOutput = null;

        boolean isStackSetToInfuse = false;
        for(ItemStack check : InfusionCoreTile.infuseStacksTemp) {
            isStackSetToInfuse = check.getItem() == central.getItem() && check.getItemDamage() == central.getItemDamage() && check.stackSize == central.stackSize;
            if (isStackSetToInfuse)
                break;
        }

        if (!(central.getItem() instanceof ItemBlock) || !isStackSetToInfuse || !BlockHandler.isBlockWhitelisted(Block.getBlockFromItem(central.getItem())))
            return false;

        ArrayList<ItemStack> ii = new ArrayList<ItemStack>();
        for (ItemStack is:input)
            if(is.getItem() instanceof ItemEssence) {
                ii.add(is.copy());
            }else return false;

        AspectList infuseAspects = new AspectList();

        for(ItemStack phial : ii){
            AspectList phialList = ((ItemEssence)phial.getItem()).getAspects(phial);
            if(phialList == null)
                return false;
            Aspect aspect = phialList.getAspects()[0];
            if(aspect == null)
                return false;
            if(infuseAspects.getAmount(aspect) > 0)
                return false;

            int cost = AspectHandler.getCostOfEffect(aspect);
            if(cost != -1)
                infuseAspects.add(aspect,  cost * central.stackSize);
            else return false;
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
        return recipeOutput != null && ii.size() > 0;
    }
}