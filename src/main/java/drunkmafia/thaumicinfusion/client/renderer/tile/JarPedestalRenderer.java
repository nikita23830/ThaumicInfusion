package drunkmafia.thaumicinfusion.client.renderer.tile;

import drunkmafia.thaumicinfusion.common.block.tile.JarPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.*;

/**
 * Created by DrunkMafia on 17/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class JarPedestalRenderer extends TileEntitySpecialRenderer {

    private IModelCustom model = AdvancedModelLoader.loadModel(jarPedestalBlock_Model);

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float deltaTime) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);

        Minecraft.getMinecraft().renderEngine.bindTexture(jarPedestalBlock_Texture);
        model.renderAll();

        GL11.glPopMatrix();

        JarPedestalTile pedestalTile = (JarPedestalTile) tile;

        if(!pedestalTile.hasJar()) return;

        ItemStack jar = pedestalTile.getJar();
        jar.stackSize = 1;
        EntityItem entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, jar);
        entityitem.hoverStart = 0.0F;

        GL11.glPushMatrix();

        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.9F, (float)z + 0.5F);
        GL11.glScalef(1.6F, 1.6F, 1.6F);

        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

        GL11.glPopMatrix();
    }
}
