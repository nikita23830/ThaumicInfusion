package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 01/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("limus"))
public class Limus extends Savable {

    public void onEntityCollidedWithBlock (World world, int x, int y, int z, Entity entity) {
        if (entity.motionY < 0)
            entity.motionY *= -1.2F;
        entity.fallDistance = 0;
    }
}
