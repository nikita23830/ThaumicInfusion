package drunkmafia.thaumicinfusion.client.renderer.tile;

import com.sun.javafx.geom.Vec3f;
import drunkmafia.thaumicinfusion.common.block.tile.InfusionCoreTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import drunkmafia.thaumicinfusion.common.util.MathHelper;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.*;

/**
 * Created by DrunkMafia on 19/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class InfusionCoreRenderer extends TileEntitySpecialRenderer {

    IModelCustom model = AdvancedModelLoader.loadModel(infusionCore_Model);
    float transSpeed = 0.025F, rotSpeed = 2F;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float deltaTime) {
        InfusionCoreTile core = (InfusionCoreTile) tile;

        if(core == null)
            return;

        updateRot(core, deltaTime);

        Vec3f pos = new Vec3f((float)x + 0.5F, (float)y + 0.5F + core.yLevel, (float)z + 0.5F);

        renderCore(pos, new Vec3f(1.4F, 1.3F, 1.4F), core.coreAxies, core.angle);
        renderCore(pos, new Vec3f(1F, 1.3F, 1F), core.coreAxies, -core.angle);
        renderInventory(pos, tile, deltaTime);
    }

    void renderCore(Vec3f pos, Vec3f scale, Vec3f axies, float angle){
        GL11.glPushMatrix();

        GL11.glTranslated(pos.x, pos.y, pos.z);
        GL11.glScalef(scale.x, scale.y, scale.z);
        GL11.glRotatef(angle, axies.x, axies.y, axies.z);

        Minecraft.getMinecraft().renderEngine.bindTexture(infusionCore_Texture);
        model.renderAll();

        GL11.glPopMatrix();
    }

    void renderInventory(Vec3f pos, TileEntity tile, float deltaTime){
        InfusionCoreTile coreTile = (InfusionCoreTile) tile;
        ItemStack inv = coreTile.getStackInSlot(0);
        if(inv == null) return;

        inv.stackSize = 1;
        EntityItem entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, inv);
        entityitem.hoverStart = 0.0F;

        GL11.glPushMatrix();

        pos.y -= 0.1F;
        float ticks = Minecraft.getMinecraft().renderViewEntity.ticksExisted + deltaTime;
        float hover = net.minecraft.util.MathHelper.sin(ticks % 32767.0F / 16.0F) * 0.05F;

        GL11.glTranslated(pos.x, pos.y + hover, pos.z);
        GL11.glScalef(1.2F, 1.2F, 1.2F);
        GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);

        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        GL11.glPopMatrix();
    }

    void updateRot(InfusionCoreTile core, float deltaTime){
        if(core.matrix != null ){
            if(core.matrix.crafting) {
                if(core.yLevel != 0.95F)
                    core.yLevel = MathHelper.lerp(core.yLevel, 0.95F, transSpeed * deltaTime);
                core.coreAxies = new Vec3f(1, 1, 1);
            }else {
                if(core.yLevel != 0)
                    core.yLevel = MathHelper.lerp(core.yLevel, 0, transSpeed * deltaTime);
                core.coreAxies = new Vec3f(0, 1, 0);
            }
            core.angle = MathHelper.lerp(core.angle, 360, rotSpeed * deltaTime);
            if(core.angle == 360)
                core.angle = 0;
        }
    }
}
