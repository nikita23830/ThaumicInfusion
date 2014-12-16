package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.EffectSyncPacketC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Iterator;
import java.util.List;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "motus", cost = 4)
public class Motus extends AspectEffect {

    ForgeDirection direction = ForgeDirection.UNKNOWN;

    @Override
    public void updateBlock(World world) {
        WorldCoord coord = getPos();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(coord.x, coord.y, coord.z, coord.x + 1, coord.y + 1.05F, coord.z + 1);
        List list =  world.getEntitiesWithinAABB(Entity.class, axisalignedbb);

        Iterator iter = list.iterator();
        while (iter.hasNext()){
            Entity entity = (Entity) iter.next();
            double mult = 0.15;

            entity.motionX = direction.offsetX * mult;
            entity.motionZ = direction.offsetZ * mult;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack wand = player.getCurrentEquippedItem();
        if(wand == null)
            return false;

        if(world.isRemote)
            return true;

        int face = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;

        ForgeDirection dir = BlockHelper.dirFromSide(player.isSneaking() ? side : face);

        if(dir != ForgeDirection.UP && dir != ForgeDirection.DOWN) {
            direction = dir;
            ChannelHandler.network.sendToDimension(new EffectSyncPacketC(this), world.provider.dimensionId);
            return true;
        }
        return false;
    }

    @Override
    public void worldBlockInteracted(EntityPlayer player, World world, int x, int y, int z, int face) {

    }

    @Override
    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);
        if(direction != null)
            tagCompound.setInteger("dir", direction.ordinal());
    }

    @Override
    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);
        if(tagCompound.hasKey("dir"))
            direction = ForgeDirection.getOrientation(tagCompound.getInteger("dir"));
        else
            direction = ForgeDirection.UNKNOWN;
    }
}
