package drunkmafia.thaumicinfusion.common.block.tile;

import com.sun.javafx.geom.Vec3f;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TilePedestal;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by DrunkMafia on 19/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class InfusionCoreTile extends TilePedestal implements IWandable {

    public static ArrayList<ItemStack> infuseStacksTemp = new ArrayList<ItemStack>();

    public TileInfusionMatrix matrix;

    @SideOnly(Side.CLIENT)
    public float yLevel = 0, angle;
    @SideOnly(Side.CLIENT)
    public Vec3f coreAxies = new Vec3f();

    @Override
    public void updateEntity() {
        if(matrix == null)
           getMatrixTile();
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    void getMatrixTile(){
        if(matrix != null)
            return;
        TileEntity matrixTile = worldObj.getTileEntity(xCoord, yCoord + 2, zCoord);
        if(matrixTile != null && matrixTile instanceof TileInfusionMatrix)
            matrix = (TileInfusionMatrix)matrixTile;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md){
        if ((!world.isRemote) && (matrix.active) && (!matrix.crafting)){
            ItemStack stack = getStackInSlot(0);
            if(stack != null) {
                infuseStacksTemp.add(stack);
                matrix.craftingStart(player);
                infuseStacksTemp.remove(stack);
                return 0;
            }
        }
        return -1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack itemStack, EntityPlayer player) {return null;}
    @Override
    public void onUsingWandTick(ItemStack itemStack, EntityPlayer player, int i) {}
    @Override
    public void onWandStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int i) {}
}
