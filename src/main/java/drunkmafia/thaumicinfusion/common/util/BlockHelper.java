package drunkmafia.thaumicinfusion.common.util;

import com.esotericsoftware.reflectasm.FieldAccess;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.client.RequestBlockPacketS;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.WorldCoordinates;

import java.lang.reflect.Field;

import static drunkmafia.thaumicinfusion.common.util.InfusionHelper.*;

public class BlockHelper {

    public static BlockData getDataFromStack(ItemStack stack, int x, int y, int z) {
        Class[] classes = getEffectsFromStack(stack);
        if(classes != null) {
            BlockData data = new BlockData(new WorldCoord(x, y, z), classes, getInfusedID(stack), getBlockID(classes));
            return data;
        }else return null;
    }

    public static <T>T getData(Class<T> type, World world, WorldCoord coords) {
        T data = getWorldData(world).getBlock(type, coords);
        if (data == null && world.isRemote)
            ChannelHandler.network.sendToServer(new RequestBlockPacketS((Class<? extends BlockSavable>) type, coords));
        return data;
    }

    public static <T>T getData(Class<T> type, IBlockAccess access, WorldCoord coords) {
        World world = getWorld(access);
        if(world == null)
            return null;
        return getData(type, world, coords);
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

    public static void destroyBlock(World world, WorldCoord coords){
        TIWorldData data = getWorldData(world);
        data.removeBlock(coords);

        world.setBlock(coords.x, coords.y, coords.z, Blocks.air);
        world.removeTileEntity(coords.x, coords.y, coords.z);
    }

    static FieldAccess worldObj;
    static int worldObjIndex;

    public static World getWorld(IBlockAccess blockAccess) {
        if(ThaumicInfusion.instance.isServer) {
            if (blockAccess instanceof ChunkCache) {
                if(worldObj == null) {
                    worldObj = FieldAccess.get(ChunkCache.class);
                    worldObjIndex = worldObj.getIndex("worldObj");
                }

                if(worldObj != null)
                    return (World) worldObj.get(blockAccess, worldObjIndex);
            } else if (blockAccess instanceof World)
                return (World) blockAccess;
        }
        return Minecraft.getMinecraft().theWorld;
    }
}
