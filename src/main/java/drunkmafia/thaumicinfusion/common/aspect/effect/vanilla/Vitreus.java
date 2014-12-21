package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.world.IBlockAccess;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "vitreus", cost = 2)
public class Vitreus extends AspectEffect {
    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return 0;
    }
}
