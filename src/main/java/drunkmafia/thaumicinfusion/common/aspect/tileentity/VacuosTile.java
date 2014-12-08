package drunkmafia.thaumicinfusion.common.aspect.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

/**
 * Created by DrunkMafia on 06/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class VacuosTile extends EffectTile implements IInventory {

    private ItemStack[] inv;

    public VacuosTile(){
        inv = new ItemStack[getSizeInventory()];
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv[slot];
    }

    public ItemStack getFirstStack(){
        for(ItemStack stack : inv)
            if(stack != null)
                return stack;
        return null;
    }

    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (inv[slot] != null){
            ItemStack itemstack = inv[slot];
            inv[slot] = null;
            return itemstack;
        }
        return null;
    }

    public int getAmount(){
        int size = 0;
        for(ItemStack item : inv)
            if(item != null)
                size += item.stackSize;
        return size;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (worldObj != null && !worldObj.isRemote)
            worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void setInventorySlotContents(int slot, ItemStack stack){
        inv[slot] = stack;
        if ((stack != null) && (stack.stackSize > getInventoryStackLimit()))
            stack.stackSize = getInventoryStackLimit();
        markDirty();
    }

    public ItemStack decrStackSize(int slot, int amount){
        if (inv[slot] != null){
            if (inv[slot].stackSize <= amount){
                ItemStack itemstack = inv[slot];
                inv[slot] = null;
                markDirty();
                return itemstack;
            }

            ItemStack itemstack = inv[slot].splitStack(amount);
            if (inv[slot].stackSize == 0)
                inv[slot] = null;

            markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public String getInventoryName() {
        return "vacuosTile";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistance(xCoord, yCoord, zCoord) < 32;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() { }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        for(int i = 0; i < getSizeInventory(); i++){
            ItemStack item = inv[i];
            if(item != null){
                NBTTagCompound itemTag = new NBTTagCompound();
                item.writeToNBT(itemTag);
                tag.setTag("S" + i, itemTag);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        for(int i = 0; i < getSizeInventory(); i++) {
            if (tag.hasKey("S" + i))
                setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(tag.getCompoundTag("S" + i)));
            else
                setInventorySlotContents(i, null);
        }
    }

    public Packet getDescriptionPacket(){
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 64537, tag);
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }
}
