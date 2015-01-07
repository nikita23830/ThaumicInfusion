package drunkmafia.thaumicinfusion.common.item;

import drunkmafia.thaumicinfusion.common.block.IWorldData;
import drunkmafia.thaumicinfusion.common.util.helper.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.helper.InfusionHelper;
import drunkmafia.thaumicinfusion.common.world.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ItemInfused extends ItemBlock {

    IWorldData blockData;

    public ItemInfused(Block block) {
        super(block);
        blockData = (IWorldData) block;
        setUnlocalizedName("item." + block.getUnlocalizedName());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int id = InfusionHelper.getInfusedID(stack);

        try {
            return "Infused " + (id != -1 ? (new ItemStack(Block.getBlockById(id), 1, stack.getItemDamage())).getDisplayName() : "");
        }catch (Exception e){
            return "NULL STACK, PLEASE DESTROY IT";
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        if(block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
            side = 1;
        else if(block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)) {
            if(side == 0)
                --y;

            if(side == 1)
                ++y;

            if(side == 2)
                --z;

            if(side == 3)
                ++z;

            if(side == 4)
                --x;

            if(side == 5)
                ++x;
        }

        if(stack.stackSize == 0)
            return false;
         else if(!player.canPlayerEdit(x, y, z, side, stack))
            return false;
         else if(y == 255 && this.field_150939_a.getMaterial().isSolid())
            return false;
         else if(world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack)) {
            BlockData data = (BlockData) blockData.getData(world, stack, new WorldCoord(x, y, z, world.provider.dimensionId));

            if(data == null)
                return false;

            WorldCoord pos = data.getCoords();
            TIWorldData worldData = BlockHelper.getWorldData(world);
            worldData.addBlock(data);

            int meta = data.getContainingBlock().onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, stack.getItemDamage());
            if(this.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, meta)) {
                data = BlockHelper.getData(BlockData.class, world, pos);
                if(data == null){
                    BlockHelper.destroyBlock(world, pos);
                    return false;
                }
                world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), data.getContainingBlock().stepSound.func_150496_b(), (data.getContainingBlock().stepSound.getVolume() + 1.0F) / 2.0F, data.getContainingBlock().stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        WorldCoord coord = WorldCoord.get(x, y, z);
        if(!world.setBlock(x, y, z, this.field_150939_a, metadata, 3)) {
            BlockHelper.getWorldData(world).removeBlock(coord);
            return false;
        } else {
            if(world.getBlock(x, y, z) == this.field_150939_a) {
                BlockHelper.getData(BlockData.class, world, coord).dataLoad(world);
                this.field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
                this.field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
            }

            return true;
        }
    }
}
