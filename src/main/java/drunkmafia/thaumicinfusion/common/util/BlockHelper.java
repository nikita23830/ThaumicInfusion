package drunkmafia.thaumicinfusion.common.util;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.client.RequestBlockPacketS;
import drunkmafia.thaumicinfusion.net.packet.server.BlockDestroyedPacketC;
import javassist.*;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import static drunkmafia.thaumicinfusion.common.util.InfusionHelper.*;

public final class BlockHelper {

    public static BlockData getDataFromStack(ItemStack stack, int x, int y, int z) {
        Class[] classes = getEffectsFromStack(stack);
        if(classes != null) {
            BlockData data = new BlockData(new WorldCoord(x, y, z), classes, getInfusedID(stack), getBlockID(classes));
            return data;
        }else return null;
    }

    public static <T>T getData(Class<T> type, World world, WorldCoord coords) {
        T data = getWorldData(world).getBlock(type, coords);
        if (data == null && world.isRemote) {
            coords.dim = Minecraft.getMinecraft().theWorld.provider.dimensionId;
            ChannelHandler.network.sendToServer(new RequestBlockPacketS((Class<? extends BlockSavable>) type, coords));
        }
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
            world.perWorldStorage.setData(worldName, worldData);
            return (TIWorldData) world.perWorldStorage.loadData(TIWorldData.class, worldName);
        }
    }

    public static void destroyBlock(World world, WorldCoord coords){
        TIWorldData data = getWorldData(world);
        data.removeBlock(coords);

        world.setBlock(coords.x, coords.y, coords.z, Blocks.air);
        world.removeTileEntity(coords.x, coords.y, coords.z);

        if(!world.isRemote)
            ChannelHandler.network.sendToDimension(new BlockDestroyedPacketC(coords), world.provider.dimensionId);
    }

    static Field worldObj;

    static HashMap<IBlockAccess, World> worlds = new HashMap<IBlockAccess, World>();

    public static World getWorld(IBlockAccess blockAccess) {
        if(ThaumicInfusion.instance.isServer) {
            if (blockAccess instanceof ChunkCache) {
                if(worldObj == null) {
                    try {
                        worldObj = ChunkCache.class.getDeclaredField("worldObj");
                        worldObj.setAccessible(true);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }

                if(worlds.containsKey(blockAccess))
                    return worlds.get(blockAccess);
                else{
                    try{
                        World world = (World) worldObj.get(blockAccess);
                        worlds.put(blockAccess, world);
                        return world;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } else if (blockAccess instanceof World)
                return (World) blockAccess;
        }
        return ChannelHandler.getClientWorld();
    }

    public static ForgeDirection dirFromSide(int side){
        switch (side){
            case 0: return ForgeDirection.DOWN;
            case 1: return ForgeDirection.UP;
            case 2: return ForgeDirection.NORTH;
            case 3: return ForgeDirection.SOUTH;
            case 4: return ForgeDirection.WEST;
            case 5: return ForgeDirection.EAST;
            default: return ForgeDirection.UNKNOWN;
        }
    }
}
