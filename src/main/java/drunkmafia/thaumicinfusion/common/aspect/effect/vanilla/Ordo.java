package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */

@Effect(aspect = ("ordo"), cost = 4)
public class Ordo extends AspectEffect implements IInfusionStabiliser {
    @Override
    public boolean canStabaliseInfusion(World world, int x, int y, int z) {
        return true;
    }
}
