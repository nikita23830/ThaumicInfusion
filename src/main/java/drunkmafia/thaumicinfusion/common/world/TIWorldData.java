package drunkmafia.thaumicinfusion.common.world;

import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.common.util.Savable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DrunkMafia on 18/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TIWorldData extends WorldSavedData {

    public World world;
    private HashMap<ChunkCoordinates, BlockSavable> blocksData;

    public TIWorldData(String mapname) {
        super(mapname);
        blocksData = new HashMap<ChunkCoordinates, BlockSavable>();
        setDirty(true);
    }

    public boolean addBlock(World world, BlockSavable block) {
        if (block != null && block.getCoords() != null) {
            if(block instanceof BlockData){
                BlockData data = (BlockData)block;
                if(data.isInit()) {
                    ChunkCoordinates pos = data.getCoords();
                    data.initAspects(world, pos.posX, pos.posY, pos.posZ);
                }
            }
            blocksData.put(block.getCoords(), block);
            setDirty(true);
            return true;
        }
        return false;
    }

    public void removeBlock(ChunkCoordinates coords) {
        blocksData.remove(coords);
        setDirty(true);
    }

    public BlockSavable getBlock(ChunkCoordinates coords) {
        return blocksData.get(coords);
    }

    public ArrayList<BlockSavable> getBlocksInChunk(Chunk chunk) {
        World world = chunk.worldObj;
        Object[] blocks = blocksData.entrySet().toArray();
        ArrayList<BlockSavable> data = new ArrayList<BlockSavable>();
        for (Object obj : blocks) {
            BlockSavable block = (BlockSavable) ((Map.Entry) obj).getValue();
            if (world.getChunkFromBlockCoords(block.getCoords().posX, block.getCoords().posZ).isAtLocation(chunk.xPosition, chunk.zPosition))
                data.add(block);
        }
        return data;
    }

    public int getNoOfBlocks() {
        return blocksData.size();
    }

    public Map.Entry[] getAllBocks(){
        return blocksData.entrySet().toArray(new Map.Entry[blocksData.size()]);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        for (int i = 0; i < tag.getInteger("Size"); i++) {
            BlockSavable data = (BlockSavable) Savable.loadDataFromNBT(tag.getCompoundTag("Tag: " + i));
            if (data != null)
                blocksData.put(data.getCoords(), data);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        Object[] objs = blocksData.entrySet().toArray();
        tag.setInteger("Size", objs.length);
        for (int i = 0; i < objs.length; i++) {
            NBTTagCompound dataTag = new NBTTagCompound();
            blocksData.get(((Map.Entry) objs[i]).getKey()).writeNBT(dataTag);
            tag.setTag("Tag: " + i, dataTag);
        }
    }
}
