package drunkmafia.thaumicinfusion.common.block;

import drunkmafia.thaumicinfusion.common.world.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public interface IWorldData {
    public boolean shouldUsePlaceEvent();
    public BlockSavable getData(World world, ItemStack stack, WorldCoord coord);
    public void breakBlock(World world, BlockSavable data, int meta);
}
