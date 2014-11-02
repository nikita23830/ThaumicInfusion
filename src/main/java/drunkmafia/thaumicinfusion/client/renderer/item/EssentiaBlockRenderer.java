package drunkmafia.thaumicinfusion.client.renderer.item;

import com.sun.javafx.geom.Vec3f;
import drunkmafia.thaumicinfusion.common.block.EssentiaBlock;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.block.tile.InfusionCoreTile;
import drunkmafia.thaumicinfusion.common.util.RGB;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;

import java.lang.reflect.Method;

/**
 * Created by DrunkMafia on 01/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class EssentiaBlockRenderer implements IItemRenderer {
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        NBTTagCompound tag = stack.getTagCompound();
        if(tag == null || stack == null) return;

        Block essentiaBlock = TIBlocks.essentiaBlock;
        RenderBlocks renderBlocks = new RenderBlocks();
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        essentiaBlock.setBlockBoundsForItemRender();
        renderBlocks.setRenderBoundsFromBlock(essentiaBlock);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);


        if(type == ItemRenderType.EQUIPPED)
            GL11.glTranslatef(-1.1225F, 0.05F, 0F);
        else if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            GL11.glTranslatef(-1.F, 0F, 0.05F);
        else if(type == ItemRenderType.INVENTORY)
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        RGB rgb = new RGB(Aspect.getAspect(tag.getString("aspectTag")).getColor());
        GL11.glColor4f(rgb.getR(), rgb.getG(), rgb.getB(), 1.0F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderBlocks.renderFaceYNeg(essentiaBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(essentiaBlock, 0, stack.getItemDamage()));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderBlocks.renderFaceYPos(essentiaBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(essentiaBlock, 1, stack.getItemDamage()));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderBlocks.renderFaceZNeg(essentiaBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(essentiaBlock, 2, stack.getItemDamage()));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderBlocks.renderFaceZPos(essentiaBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(essentiaBlock, 3, stack.getItemDamage()));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXNeg(essentiaBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(essentiaBlock, 4, stack.getItemDamage()));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderBlocks.renderFaceXPos(essentiaBlock, 0.0D, 0.0D, 0.0D, renderBlocks.getBlockIconFromSideAndMetadata(essentiaBlock, 5, stack.getItemDamage()));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glPopMatrix();
    }
}
