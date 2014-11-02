package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("potentia"))
public class Potentia  extends Savable {

    public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side){
        return 15;
    }

    public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int side){
        return 15;
    }

    public boolean shouldCheckWeakPower(IBlockAccess access, int x, int y, int z, int side) {
        return true;
    }

    public boolean canConnectRedstone(IBlockAccess access, int x, int y, int z, int side) {
        return true;
    }
}
