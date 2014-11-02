package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;

/**
 * Created by DrunkMafia on 08/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("aqua"), tileentity = AquaTile.class, hasTileEntity = true)
public class Aqua extends Savable {

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        System.out.println(world.getTileEntity(x, y, z) != null);
        ItemStack playerItem = player.inventory.getCurrentItem();
        if (playerItem != null) {
            AquaTile tank = (AquaTile) world.getTileEntity(x, y, z);
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
            if (liquid != null) {
                int amount = tank.fill( liquid, false);
                if (amount == liquid.amount) {
                    tank.fill( liquid, true);
                    if (!player.capabilities.isCreativeMode) {
                        playerItem.stackSize--;
                        if(playerItem.stackSize <= 0) playerItem = null;
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, playerItem);
                        return true;
                    }
                    return true;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private ResourceLocation gui = new ResourceLocation(ModInfo.MODID, "textures/gui/InfusedGUI.png");

    public void blockHighlight(World world, int x, int y, int z, EntityPlayer worldP, MovingObjectPosition pos, float partialTicks){
        /**
        EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().renderViewEntity;
        double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        GL11.glPushMatrix();

        float xd = (float)(iPX - (x + 0.5D));
        float zd = (float)(iPZ - (z + 0.5D));
        float rotYaw = (float)(Math.atan2(xd, zd) * 180.0D / 3.141592653589793D);

        //GL11.glRotatef(rotYaw + 180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslated(-iPX + x + 1.9D, -iPY + y + 1.5D, -iPZ + z + 0.5D);

        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(0.02F, 0.02F, 0.02F);

        Minecraft.getMinecraft().renderEngine.bindTexture(gui);
        GL11.glEnable(3042);
        drawTexturedQuad(0, 0, 0, 0, 107, 118, 12);

        GL11.glPopMatrix();


        TileEntity tank = world.getTileEntity(x, y, z);
        if(tank != null) System.out.println(tank.getClass().getSimpleName());
        if(tank == null || !(tank instanceof AquaTile)){
            System.out.println("Tile Failed");
            return;
        }

        FluidStack liquid = ((AquaTile)tank).tank.getFluid();

        if (liquid == null || liquid.amount <= 0) {
            System.out.println("Tank is empty");
            return;
        }

        GL11.glPushMatrix();

        GL11.glTranslated(-iPX + x + 1.9D, -iPY + y + 1.5D, -iPZ + z + 0.5D);

        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(0.02F, 0.02F, 0.02F);

        RenderManager.instance.renderEntityWithPosYaw(new EntityItem(world, 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.brick_block)), 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

        System.out.println("Rendering block");

        GL11.glPopAttrib();
        GL11.glPopMatrix();
         **/
    }

    public static void drawTexturedQuad(double x, double y, double z, int u, int v, int sizeX, int sizeY){
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV(x + 0, y + sizeY, z, (u + 0) * var7, (v + sizeY) * var8);
        var9.addVertexWithUV(x + sizeX, y + sizeY, z, (u + sizeX) * var7, (v + sizeY) * var8);
        var9.addVertexWithUV(x + sizeX, y + 0, z, (u + sizeX) * var7, (v + 0) * var8);
        var9.addVertexWithUV(x + 0, y + 0, z, (u + 0) * var7, (v + 0) * var8);
        var9.draw();
    }
}
