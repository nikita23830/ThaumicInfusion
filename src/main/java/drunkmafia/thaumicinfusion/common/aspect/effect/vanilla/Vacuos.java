package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.aspect.tileentity.VacuosTile;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.utils.InventoryUtils;

/**
 * Created by DrunkMafia on 06/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("vacuos"), cost = 4)
public class Vacuos extends AspectEffect {

    VacuosTile tile;

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new VacuosTile();
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if(world.isRemote)
            return;

        getTile(world, x, y, z);
        if(tile == null)
            return;

        ItemStack inv = null;
        int index = -1;
        for(int i = 0; i < tile.getSizeInventory(); i++)
            if(tile.getStackInSlot(i) != null) {
                inv = tile.getStackInSlot(i);
                index = i;
                break;
            }

        if (inv != null && index != -1) {
            if(inv.stackSize > 64){
                ItemStack copy = inv.copy();
                copy.stackSize = 64;
                inv.stackSize -= 64;
                spawnItemStack(player, copy);
                tile.setInventorySlotContents(index, inv);
            }else{
                spawnItemStack(player, inv);
                tile.setInventorySlotContents(index, null);
            }

            player.inventory.markDirty();
            world.playSoundEffect(x, y, z, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.5F);
            return;
        }
    }

    void spawnItemStack(EntityPlayer player, ItemStack stack){
        if ((stack == null) || !(stack.stackSize > 0))
            return;

        EntityItem entityItem = new EntityItem(player.worldObj, player.posX, player.posY + player.getEyeHeight() / 2.0F, player.posZ, stack.copy());
        player.worldObj.spawnEntityInWorld(entityItem);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote)
            return true;

        getTile(world, x, y, z);
        if(tile == null)
            return false;
        ItemStack inv = tile.getStackInSlot(0);
        ItemStack equipped = player.getCurrentEquippedItem();
        if (inv != null) {
            if (equipped != null) {
                player.setCurrentItemOrArmor(0, InventoryUtils.insertStack(tile, equipped, 0, true));

                player.inventory.markDirty();
                world.playSoundEffect(x, y, z, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.5F);
                return true;
            }
        }

        if (equipped != null && inv == null) {

            tile.setInventorySlotContents(0, equipped);
            player.setCurrentItemOrArmor(0, null);

            player.inventory.markDirty();

            world.playSoundEffect(x, y, z, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.6F);

            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if(world.isRemote)
            return;
        InventoryUtils.dropItems(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void blockHighlight(World world, int x, int y, int z, EntityPlayer player, MovingObjectPosition pos, float partialTicks){
        getTile(world, x, y, z);
        if(tile == null)
            return;

        ItemStack stack = tile.getFirstStack();

        if(stack == null)
            return;

        ForgeDirection direction = BlockHelper.getRotatedSide(pos.sideHit);
        int size = tile.getAmount();
        String str = size / 64 + " of 64";
        if(size < 64)
            str = size + "";

        Thaumcraft.instance.renderEventHandler.drawTextInAir(x + direction.offsetX, y + direction.offsetY + 0.5F, z + direction.offsetZ, partialTicks, str);
        renderInventory(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, player, stack, partialTicks);
    }

    void renderInventory(int x, int y, int z, EntityPlayer player, ItemStack stack, float deltaTime){
        if(stack == null && tile.getAmount() == 0) return;

        double iPX = player.prevPosX + (player.posX - player.prevPosX) * deltaTime;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * deltaTime;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * deltaTime;

        GL11.glPushMatrix();
        float ticks = Minecraft.getMinecraft().renderViewEntity.ticksExisted + deltaTime;
        float hover = net.minecraft.util.MathHelper.sin(ticks % 32767.0F / 16.0F) * 0.05F;

        GL11.glTranslated(-iPX + x + 0.5D, -iPY + y + 0.5D + hover, -iPZ + z + 0.5D);
        GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.25F, 0.25F, 0.25F);

        RenderManager.instance.itemRenderer.renderItem(player, stack, 0);
        GL11.glPopMatrix();
    }

    public void getTile(World world, int x, int y, int z){
        if(tile == null) {
            TileEntity worldTile = world.getTileEntity(x, y, z);
            if(worldTile != null && worldTile instanceof VacuosTile)
                tile = (VacuosTile) worldTile;
        }
    }
}
