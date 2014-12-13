package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import cpw.mods.fml.common.Loader;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "machina", cost = 8, hasTileEntity = true)
public class Machina extends AspectEffect {
    /**
    @Override
    public TileEntity getTile() {
        return new TileComputer();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote)
            return true;

        TileEntity tile = world.getTileEntity(x, y, z);

        if(tile != null && tile instanceof TileComputer) {
            ((TileComputer) tile).openGUI(player);
            return true;
        }
        return false;
    }
    **/

    @Override
    public boolean shouldRegister() {
        Loader loader = Loader.instance();
        return loader.isModLoaded("computercraft");
    }
}
