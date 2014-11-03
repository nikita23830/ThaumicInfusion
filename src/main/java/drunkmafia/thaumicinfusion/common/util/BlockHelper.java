package drunkmafia.thaumicinfusion.common.util;

import com.esotericsoftware.reflectasm.FieldAccess;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.client.RequestBlockPacketS;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Field;

import static drunkmafia.thaumicinfusion.common.util.InfusionHelper.*;

public class BlockHelper {

    public static BlockData getDataFromStack(ItemStack stack, int x, int y, int z) {
        Class[] classes = getEffectsFromStack(stack);
        if(classes != null) {
            BlockData data = new BlockData(new ChunkCoordinates(x, y, z), classes, getInfusedID(stack), getBlockID(classes));
            return data;
        }else return null;
    }

    public static BlockSavable getData(World world, ChunkCoordinates coords) {
        BlockSavable data = getWorldData(world).getBlock(coords);
        if (data == null && world.isRemote)
            ChannelHandler.network.sendToServer(new RequestBlockPacketS(coords));
        return data;
    }

    public static BlockSavable getData(IBlockAccess access, ChunkCoordinates coords) {
        World world = getWorld(access);
        if(world == null)
            return null;
        return getData(world, coords);
    }

    public static TIWorldData getWorldData(World world) {
        String worldName = world.getWorldInfo().getWorldName() + "_" + world.provider.dimensionId + "_TIWorldData";
        WorldSavedData worldData = world.perWorldStorage.loadData(TIWorldData.class, worldName);
        if (worldData != null) return (TIWorldData) worldData;
        else {
            worldData = new TIWorldData(worldName);
            ((TIWorldData)worldData).world = world;
            world.perWorldStorage.setData(worldName, worldData);
            return (TIWorldData) worldData;
        }
    }

    static Field worldObj;

    public static World getWorld(IBlockAccess blockAccess) {
        if(blockAccess instanceof ChunkCache) {
            try {
                if (worldObj == null) {
                    worldObj = ChunkCache.class.getDeclaredField("worldObj");
                    worldObj.setAccessible(true);
                }
                Object obj = worldObj.get(blockAccess);
                if (obj != null)
                    return (World) obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(blockAccess instanceof World){
            return (World) blockAccess;
        }
        return null;
    }

    /**
     * Calculates which side is <i>side</i> given the block's orientation
     * @author Two - Edited By DrunkMafia
     * @param side the side that is searched for
     * @param blockDir the facing of the block as CW 0 (south) to 3 (east)
     * @return the side that corresponds to <i>side</i> according to the block's rotation.
     */
    public static ForgeDirection getRotatedSide(final int side, final int blockDir) {
        switch (side) {
            case 0:
                return ForgeDirection.DOWN;
            case 1:
                return ForgeDirection.UP;
            case 2: // north side
                switch (blockDir & 3) {
                    case 0:
                        return ForgeDirection.EAST;
                    case 1:
                        return ForgeDirection.NORTH;
                    case 2:
                        return ForgeDirection.WEST;
                    case 3:
                        return ForgeDirection.SOUTH;
                }
            case 3: // south side
                switch (blockDir & 3) {
                    case 0:
                        return ForgeDirection.WEST;
                    case 1:
                        return ForgeDirection.SOUTH;
                    case 2:
                        return ForgeDirection.EAST;
                    case 3:
                        return ForgeDirection.NORTH;
                }
            case 4: // west side
                switch (blockDir & 3) {
                    case 0:
                        return ForgeDirection.NORTH;
                    case 1:
                        return ForgeDirection.WEST;
                    case 2:
                        return ForgeDirection.SOUTH;
                    case 3:
                        return ForgeDirection.EAST;
                }
            case 5: // east side
                switch (blockDir & 3) {
                    case 0:
                        return ForgeDirection.SOUTH;
                    case 1:
                        return ForgeDirection.EAST;
                    case 2:
                        return ForgeDirection.NORTH;
                    case 3:
                        return ForgeDirection.WEST;
                }
        }
        throw new IllegalArgumentException("Illegal side " + side);
    }
}
