package drunkmafia.thaumicinfusion.common.world;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.BlockDestroyedPacketC;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.WorldCoordinates;

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
    private HashMap<WorldCoordinates, ArrayList<BlockSavable>> blocksData;

    public TIWorldData(String mapname) {
        super(mapname);
        blocksData = new HashMap<WorldCoordinates, ArrayList<BlockSavable>>();
        setDirty(true);
    }

    public boolean addBlock(World world, BlockSavable block) {
        this.world = world;

        if (block != null && block.getCoords() != null) {
            if(block instanceof BlockData){
                BlockData data = (BlockData)block;
                if(!data.isInit()) {
                    WorldCoordinates pos = data.getCoords();
                    data.initAspects(world, pos.x, pos.y, pos.z);
                }
            }

            if(blocksData.containsKey(block.getCoords())) {
                blocksData.get(block.getCoords()).add(block);
            }else {
                ArrayList<BlockSavable> datas = new ArrayList<BlockSavable>();
                datas.add(block);
                blocksData.put(block.getCoords(), datas);
            }
            setDirty(true);
            return true;
        }
        return false;
    }

    public void postLoad(){
        if(world == null)
            return;

        for(BlockSavable savable : getAllBocks()){
            WorldCoordinates coords = savable.getCoords();
            if(savable instanceof BlockData && !((BlockData)savable).isInit())
                ((BlockData)savable).initAspects(world, coords.x, coords.y, coords.z);
        }
    }

    public void removeBlock(WorldCoordinates coords) {
        if(!blocksData.containsKey(coords))
            return;

        blocksData.remove(coords);
        setDirty(true);
        ChannelHandler.network.sendToAll(new BlockDestroyedPacketC(coords));
    }

    public <T>T getBlock(Class<T> type, WorldCoordinates coords) {
        ArrayList<BlockSavable> datas = blocksData.get(coords);
        if(datas == null)
            return null;
        for(BlockSavable block : datas)
            if(type.isAssignableFrom(block.getClass()))
                return (T) block;
        return null;
    }

    public BlockSavable[][] getAllStoredData(){
        Map.Entry<WorldCoordinates, ArrayList<BlockSavable>>[] entries = blocksData.entrySet().toArray(new Map.Entry[blocksData.size()]);
        BlockSavable[][] savables = new BlockSavable[entries.length][0];
        for(int i = 0; i < savables.length; i++){
            ArrayList<BlockSavable> stored = entries[i].getValue();
            savables[i] = new BlockSavable[stored.size()];
            for(int s = 0; s < stored.size(); s++)
                savables[i][s] = stored.get(s);
        }
        return savables;
    }

    public int getNoOfBlocks() {
        return blocksData.size();
    }

    public BlockSavable[] getAllBocks(){
        Map.Entry[] entries = blocksData.entrySet().toArray(new Map.Entry[blocksData.size()]);
        ArrayList<BlockSavable> blocks = new ArrayList<BlockSavable>();
        for(Map.Entry<WorldCoordinates, ArrayList<BlockSavable>> data : entries){
            blocks.addAll(data.getValue());
        }
        BlockSavable[] array = new BlockSavable[blocks.size()];
        array = blocks.toArray(array);
        return array;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        for(int i = 0; i < tag.getInteger("Positions"); i++){
            for(int p = 0; p < tag.getInteger("Pos: " + i); p++){
                NBTTagCompound dataTag = tag.getCompoundTag("Pos: " + i + " Tag: " + p);
                BlockSavable data = (BlockSavable) BlockSavable.loadDataFromNBT(dataTag);

                if(blocksData.containsKey(data.getCoords())) {
                    blocksData.get(data.getCoords()).add(data);
                }else {
                    ArrayList<BlockSavable> datas = new ArrayList<BlockSavable>();
                    datas.add(data);
                    blocksData.put(data.getCoords(), datas);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        BlockSavable[][] storedData = getAllStoredData();
        tag.setInteger("Positions", storedData.length);
        for(int i = 0; i < storedData.length; i++){
            tag.setInteger("Pos: " + i, storedData[i].length);
            for(int p = 0; p < storedData[i].length; p++){
                NBTTagCompound dataTag = new NBTTagCompound();
                storedData[i][p].writeNBT(dataTag);
                tag.setTag("Pos: " + i + " Tag: " + p, dataTag);
            }
        }
    }
}
