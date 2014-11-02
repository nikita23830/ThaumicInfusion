package drunkmafia.thaumicinfusion.common;

import drunkmafia.thaumicinfusion.common.tab.TITab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileInfusionPillar;

/**
 * Created by DrunkMafia on 20/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class DebugItem extends Item {

    public DebugItem(){
        setUnlocalizedName("Debug");
        setCreativeTab(TITab.tab);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        System.out.println("Block: " + world.getBlock(x, y, z).getUnlocalizedName() + " Meta: " + world.getBlockMetadata(x, y, z));
        if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileInfusionPillar){
            TileInfusionPillar pillar = (TileInfusionPillar) world.getTileEntity(x, y, z);
            System.out.println(pillar.orientation);
        }
        return true;
    }
}
