package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@SideOnly(Side.CLIENT)
public class FakeBlockRender extends Block {

    public Block containing;

    public FakeBlockRender(Block containing) {
        super(containing.getMaterial());
        this.containing = containing;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return containing.getIcon(side, meta);
    }

    @Override
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int meta) {
        return containing.getIcon(access, x, y, z, meta);
    }

    @Override
    public int getBlockColor() {
        return containing.getBlockColor();
    }

    @Override
    public int getRenderColor(int colour) {
        return containing.getRenderColor(colour);
    }

    @Override
    public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
        return containing.colorMultiplier(access, x, y, z);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
        return containing.shouldSideBeRendered(access, x, y, z, side);
    }

    @Override
    public MapColor getMapColor(int colour) {
        return containing.getMapColor(colour);
    }
}
