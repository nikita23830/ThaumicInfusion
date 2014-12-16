package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

/**
 * Created by DrunkMafia on 08/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("aqua"), cost = 1, hasCustomBlock = true)
public class Aqua extends AspectEffect {

    @Override
    public InfusedBlock getBlock() {
        return new InfusedBlock(Material.water);
    }

    public boolean isReplaceable(IBlockAccess access, int x, int y, int z) {
        return false;
    }
}