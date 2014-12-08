package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.HashMap;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("iter"), cost = 4)
public class Iter extends AspectEffect {

    static HashMap<String, ChunkCoordinates> itersPositions = itersPositions = new HashMap<String, ChunkCoordinates>();
    ChunkCoordinates destination;

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(handleShifting(player) || world.isRemote)
            return true;
        if(destination == null)
            return false;

        System.out.println("Teleporting player to pos");
        player.setPositionAndUpdate(destination.posX, destination.posY, destination.posZ);
        return true;
    }

    public boolean handleShifting(EntityPlayer player){
        if(!player.isSneaking())
            return false;

        System.out.println("Handing shift");
        String name = player.getCommandSenderName();
        ChunkCoordinates pos = itersPositions.get(name);

        if(pos == null){
            itersPositions.put(name, getPos());
        }else{
            BlockSavable data = BlockHelper.getData(BlockData.class, worldObj, getPos());
            if(data != null){
                Iter iter = ((BlockData)data).getEffect(Iter.class);
                if(iter != null){
                    iter.destination = getPos();
                    destination = iter.getPos();
                    itersPositions.remove(name);
                    System.out.println("Desitnations set");
                }
            }
        }

        return true;
    }
}
