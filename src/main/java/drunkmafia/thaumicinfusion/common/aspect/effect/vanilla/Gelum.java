package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.material.Material;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("gelum"), cost = 1, hasCustomBlock = true)
public class Gelum extends AspectEffect {
    @Override
    public InfusedBlock getBlock() {
        return new InfusedBlock(Material.rock).setSlipperiness(0.98F);
    }
}
