package drunkmafia.thaumicinfusion.common.container;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * Created by DrunkMafia on 07/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class CreativeInfusionContainer extends Container {

    private Inventory tempInv;

    public CreativeInfusionContainer(InventoryPlayer inv){
        tempInv = new Inventory();



        addSlotToContainer(new Slot(tempInv, 0, 0, 0));
        addSlotToContainer(new Slot(tempInv, 1, 30, 0));

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(inv, x, 8 + 18 * x, 108));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(inv, x + y * 9 + 9, 8 + 18 * x, 50 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public Inventory getTempInv(){
        return tempInv;
    }

    public static class Inventory implements IInventory {

        private ItemStack[] inv = new ItemStack[getSizeInventory()];

        @Override
        public int getSizeInventory() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inv[slot];
        }

        @Override
        public ItemStack decrStackSize(int i, int count) {
            ItemStack itemstack = getStackInSlot(i);
            if (itemstack != null) {
                if (itemstack.stackSize <= count)
                    setInventorySlotContents(i, null);
                else
                    itemstack = itemstack.splitStack(count);
            }
            return itemstack;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            return inv[slot];
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            inv[slot] = stack;
        }

        @Override
        public String getInventoryName() {
            return "CreativeInfusion";
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
        public void markDirty() {}

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory() {}

        @Override
        public void closeInventory() {}

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return true;
        }
    }
}
