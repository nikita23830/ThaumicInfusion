package drunkmafia.thaumicinfusion.common.util;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.client.RequestBlockPacketS;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

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

    public static BlockSavable getData(IBlockAccess world, ChunkCoordinates coords) {
        return getData(getWorld(world, coords), coords);
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

    public static World getWorld(IBlockAccess blockAccess, ChunkCoordinates coordinates) {
        return getWorld(blockAccess, coordinates.posX, coordinates.posY, coordinates.posZ);
    }

    public static World getWorld(IBlockAccess blockAccess, int x, int y, int z) {
        if (ThaumicInfusion.isServer) {
            Block block = blockAccess.getBlock(x, y, z);
            int meta = blockAccess.getBlockMetadata(x, y, z);
            for (WorldServer world : MinecraftServer.getServer().worldServers) {
                if (world.getBlock(x, y, z) == block && world.getBlockMetadata(x, y, z) == meta)
                    return world;
            }
        } else {
            return getClientWorld();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
