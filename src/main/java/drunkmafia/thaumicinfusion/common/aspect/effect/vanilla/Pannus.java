package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("pannus"), cost = 1)
public class Pannus extends AspectEffect {

    public void onFallenUpon(World world, int x, int y, int z, Entity ent, float fall) {
        ent.fallDistance = 0;
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {
        ent.fallDistance = 0;
    }
}
