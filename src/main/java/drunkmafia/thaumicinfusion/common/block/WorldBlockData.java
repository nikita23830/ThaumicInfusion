package drunkmafia.thaumicinfusion.common.block;

import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public abstract class WorldBlockData extends Block implements IWorldData {
    protected WorldBlockData(Material mat) {
        super(mat);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        BlockSavable block = getData(world, stack, WorldCoord.get(x, y, z));

        if(block != null)
            BlockHelper.getWorldData(world).addBlock(block);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if(world.isRemote)
            return;
        WorldCoord pos = WorldCoord.get(x, y, z);
        BlockSavable[] blocks = BlockHelper.getWorldData(world).getAllDatasAt(pos);
        for(BlockSavable savable : blocks)
            if(savable != null)
                breakBlock(world, savable);

        BlockHelper.destroyBlock(world, pos);
    }

    @Override
    public abstract boolean shouldUsePlaceEvent();

    @Override
    public abstract BlockSavable getData(World world, ItemStack stack, WorldCoord coord);

    @Override
    public abstract void breakBlock(World world, BlockSavable data);
}
