package drunkmafia.thaumicinfusion.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.mixerBlock_Model;
import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.mixerBlock_Texture;

/**
 * Created by DrunkMafia on 28/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class MixerRenderer extends TileEntitySpecialRenderer {

    private IModelCustom model = AdvancedModelLoader.loadModel(mixerBlock_Model);

    @java.lang.Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tpf) {
        GL11.glPushMatrix();

        GL11.glEnable (GL11.GL_BLEND);
        GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);

        Minecraft.getMinecraft().renderEngine.bindTexture(mixerBlock_Texture);
        model.renderAll();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        ItemStack jar = new ItemStack(Blocks.coal_block);
        jar.stackSize = 1;
        EntityItem entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, jar);
        entityitem.hoverStart = 0.0F;

        GL11.glPushMatrix();

        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GL11.glScalef(1.6F, 1.6F, 1.6F);

        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

        GL11.glPopMatrix();
    }
}
