package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.world.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "mortuus", cost = 4)
public class Mortuus extends AspectEffect {

    private static int[] mobs = {
            50,
            51,
            52,
            54,
            55,
            58
    };

    static final long maxCooldown = 2000L;
    long cooldown;

    @Override
    public void updateBlock(World world) {
        WorldCoord pos = getPos();
        if(world.isRemote || world.getBlockLightValue(pos.x, pos.y, pos.z) > 8 || !world.isAirBlock(pos.x, pos.y + 1, pos.z) || !world.isAirBlock(pos.x, pos.y + 2, pos.z))
            return;

        Random rand = world.rand;
        if(System.currentTimeMillis() > cooldown + maxCooldown && rand.nextInt(1000) == 1){
            Entity entity = EntityList.createEntityByID(mobs[rand.nextInt(mobs.length)], world);
            entity.setPosition(pos.x, pos.y + 1, pos.z);
            world.spawnEntityInWorld(entity);
            cooldown = System.currentTimeMillis();
        }
    }
}
