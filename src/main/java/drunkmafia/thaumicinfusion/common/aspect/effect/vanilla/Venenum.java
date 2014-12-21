package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "venenum", cost = 4)
public class Venenum extends AspectEffect {

    static long maxCooldown = 4000L;
    long cooldown;

    @Override
    public void updateBlock(World world) {
        if(world.isRemote)
            return;

        if(cooldown + maxCooldown < System.currentTimeMillis()) {
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1).expand(1, 1, 1);
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

            for (EntityPlayer player : players)
                player.addPotionEffect(new PotionEffect(Potion.poison.getId(), 100));

            cooldown = System.currentTimeMillis();
        }
    }
}
