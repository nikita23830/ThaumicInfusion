package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by DrunkMafia on 01/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("limus"), cost = 4, hasCustomBlock = true)
public class Limus extends AspectEffect {

    public static final MaterialLiquid limusMat = new LimusMat();

    @Override
    public InfusedBlock getBlock() {
        return new InfusedBlock(Material.water);
    }

    public boolean isReplaceable(IBlockAccess access, int x, int y, int z) {
        return false;
    }

    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {}

    public static class LimusMat extends MaterialLiquid {
        public LimusMat() {
            super(MapColor.greenColor);
        }
    }
}
