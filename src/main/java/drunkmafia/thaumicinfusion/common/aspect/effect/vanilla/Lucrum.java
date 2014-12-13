package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.MathHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "lucrum", cost = 4)
public class Lucrum extends AspectEffect {

    @Override
    public void aspectInit(World world, WorldCoord pos) {
        super.aspectInit(world, pos);
        if(!world.isRemote)
            updateTick(world, pos.x, pos.y, pos.z, new Random());
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(10, 10, 10);
        ArrayList<EntityItem> list = (ArrayList<EntityItem>) world.getEntitiesWithinAABB(EntityItem.class, axisalignedbb);

        double speed = 0.05D;

        for(EntityItem item : list){
            if(!isItemNearBlock(item)){
                item.motionX = item.posX > x ? -speed : speed;
                item.motionZ = item.posZ > z ? -speed : speed;
            }
        }

        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 1);
    }

    boolean isItemNearBlock(EntityItem item){
        return getPos().getDistanceSquared((int)item.posX, (int)item.posY, (int)item.posZ) < 1;
    }
}
