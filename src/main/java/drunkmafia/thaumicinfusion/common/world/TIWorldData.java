package drunkmafia.thaumicinfusion.common.world;

import gnu.trove.map.hash.THashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by DrunkMafia on 18/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TIWorldData extends WorldSavedData {

    public World world;
    private THashMap<WorldCoord, ArrayList<BlockSavable>> blocksData;

    public TIWorldData(String mapname) {
        super(mapname);
        blocksData = new THashMap<WorldCoord, ArrayList<BlockSavable>>();
        setDirty(true);
    }

    public void addBlock(BlockSavable block, boolean init){
        if(block == null)
            return;

        if(world == null)
            world = DimensionManager.getWorld(block.getCoords().dim);

        if(init && !block.isInit())
            block.dataLoad(world);

        if(blocksData.containsKey(block.getCoords())) {
            blocksData.get(block.getCoords()).add(block);
        }else {
            ArrayList<BlockSavable> datas = new ArrayList<BlockSavable>();
            datas.add(block);
            blocksData.put(block.getCoords(), datas);
        }
        setDirty(true);
    }

    public void addBlock(BlockSavable block){
        addBlock(block, false);
    }

    public void postLoad(){
        for(BlockSavable savable : getAllBocks()) {
            if(world == null)
                world = DimensionManager.getWorld(savable.getCoords().dim);
            if (!savable.isInit())
                savable.dataLoad(world);
        }
    }

    public void removeBlock(WorldCoord coords) {
        if(coords == null || !blocksData.containsKey(coords))
            return;

        blocksData.remove(coords);
        setDirty(true);
    }

    public BlockSavable[] getAllDatasAt(WorldCoord coords){
        ArrayList<BlockSavable> savables = blocksData.get(coords);
        if(savables != null)
            return blocksData.get(coords).toArray(new BlockSavable[savables.size()]);
        return new BlockSavable[0];
    }

    public <T>T getBlock(Class<T> type, WorldCoord coords) {
        ArrayList<BlockSavable> datas = blocksData.get(coords);
        if(datas == null)
            return null;
        for(BlockSavable block : datas)
            if (type.isAssignableFrom(block.getClass()))
                return (T) block;

        return null;
    }

    public BlockSavable[][] getAllStoredData(){
        Map.Entry<WorldCoord, ArrayList<BlockSavable>>[] entries = blocksData.entrySet().toArray(new Map.Entry[blocksData.size()]);
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
        for(Map.Entry<WorldCoord, ArrayList<BlockSavable>> data : entries){
            blocks.addAll(data.getValue());
        }
        BlockSavable[] array = new BlockSavable[blocks.size()];
        array = blocks.toArray(array);
        return array;
    }

    @SuppressWarnings("unchecked")
    public <T extends BlockSavable>T[] getAllBlocks(Class<T> type){
        BlockSavable[] blocks = getAllBocks();
        ArrayList<T> blocksOfType = new ArrayList<T>();
        for(BlockSavable block : blocks){
            if(type.isInstance(block))
                blocksOfType.add(type.cast(block));
        }
        return blocksOfType.toArray((T[]) Array.newInstance(type, blocksOfType.size()));
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