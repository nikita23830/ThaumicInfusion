package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("iter"), cost = 4)
public class Iter extends AspectEffect {

    public static long maxCooldown = 100L;
    long startCooldown;

    WorldCoord destination;

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {
        teleportEntity(ent);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity ent) {
        teleportEntity(ent);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote)
            return true;

        if(destination != null & !player.isSneaking())
            teleportEntity(player);

        ItemStack wand = player.getCurrentEquippedItem();
        if(wand == null)
            return false;

        if(player.isSneaking()){
            BlockData data = BlockHelper.getData(BlockData.class, world, destination);
            destination = null;
            if(data != null && data.hasEffect(Iter.class))
                data.getEffect(Iter.class).destination = null;
            return true;
        }

        NBTTagCompound compound = wand.stackTagCompound == null ? new NBTTagCompound() : wand.stackTagCompound;
        if(compound.hasKey("id") && compound.getString("id").equals("Iter")){
            destination = new WorldCoord();
            destination.readNBT(compound);
            destination.removeFromNBT(compound);
            destination.id = "Dest";
            wand.stackTagCompound = compound;

            BlockData data = BlockHelper.getData(BlockData.class, world, destination);
            if(data == null){
                destination = null;
                return false;
            }

            Iter destIter = data.getEffect(Iter.class);
            if(destIter == null) {
                destination = null;
            }else {
                WorldCoord pos = getPos();
                pos.id = "Dest";
                destIter.destination = pos;
            }

            return true;
        }else {
            WorldCoord pos = new WorldCoord("Iter", x, y, z);
            pos.writeNBT(compound);
            wand.stackTagCompound = compound;
            return true;
        }
    }

    void teleportEntity(Entity entity){
        if(entity.worldObj.isRemote || destination == null)
                return;

        if(entity instanceof EntityLivingBase && startCooldown < System.currentTimeMillis() + maxCooldown && safeToTeleport(entity.worldObj)) {
            startCooldown = System.currentTimeMillis();
            ((EntityLivingBase) entity).setPositionAndUpdate(destination.x + 0.5F, destination.y + 1F, destination.z + 0.5F);
        }
    }

    boolean safeToTeleport(World world){
        BlockData data = BlockHelper.getData(BlockData.class, world, destination);

        if(data == null || !data.hasEffect(Iter.class)) {
            destination = null;
            return false;
        }

        return world.isAirBlock(destination.x, destination.y + 1, destination.z) && world.isAirBlock(destination.x, destination.y + 2, destination.z);
    }

    @Override
    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);

        if(destination == null)
            return;

        destination.writeNBT(tagCompound);
    }

    @Override
    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);

        if(!tagCompound.hasKey("id"))
            return;

        destination = new WorldCoord();
        destination.readNBT(tagCompound);
    }
}
