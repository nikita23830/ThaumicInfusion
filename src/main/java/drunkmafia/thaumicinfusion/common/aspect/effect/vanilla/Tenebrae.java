package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by DrunkMafia on 06/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("tenebrae"), cost = 1)
public class Tenebrae extends AspectEffect {

    @Override
    public void aspectInit(World world,ChunkCoordinates pos) {
        super.aspectInit(world, pos);
        if(!world.isRemote)
            updateTick(world, pos.posX, pos.posY, pos.posZ, new Random());
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.setLightValue(EnumSkyBlock.Block, x, y, z, -10);
        world.setLightValue(EnumSkyBlock.Block, x + 1, y, z, -10);
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 10);
    }
}
