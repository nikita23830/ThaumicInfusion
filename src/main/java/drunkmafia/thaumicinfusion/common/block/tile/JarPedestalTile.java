package drunkmafia.thaumicinfusion.common.block.tile;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.SyncTilePacketC;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.tiles.TileJar;
import thaumcraft.common.tiles.TileJarFillable;

/**
 * Created by DrunkMafia on 17/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class JarPedestalTile extends TileEntity {

    private ItemStack jar;

    public void placeJar(ItemStack stack){
        if(jar != null || stack.getItem() instanceof ItemJarFilled) {
            jar = stack;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            if(worldObj.isRemote) worldObj.playSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "thaumcraft:jar", 0.4F, 1.0F, false);
        }
    }

    public ItemStack takeJar(){
        ItemStack copyJar = jar;
        jar = null;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        if(worldObj.isRemote) worldObj.playSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "thaumcraft:jar", 0.4F, 1.0F, false);
        return copyJar;
    }

    public AspectList getAspectsInJar(){
        if(jar == null) return null;
        AspectList jarList = new AspectList();
        jarList.readFromNBT(jar.getTagCompound());
        return jarList;
    }

    public void setAspectsInJar(AspectList list){
        if(jar == null) return;
        ((ItemJarFilled)jar.getItem()).setAspects(jar, list);
        if(worldObj.isRemote) worldObj.playSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "game.neutral.swim", 0.5F, 1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.3F, false);
    }

    public ItemStack getJar(){
        return jar.copy();
    }

    public boolean hasJar(){
        return jar != null;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(jar == null) return;
        NBTTagCompound jarTag = new NBTTagCompound();
        jar.writeToNBT(jarTag);
        tag.setTag("jar", jarTag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey("jar")) jar = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("jar"));
        else jar = null;
    }
}
