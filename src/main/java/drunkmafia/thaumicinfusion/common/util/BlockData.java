package drunkmafia.thaumicinfusion.common.util;

import com.esotericsoftware.reflectasm.MethodAccess;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;

public class BlockData extends BlockSavable {

    private int containingID, blockID;
    private Class<? extends TileEntity> tileClass;
    private TileEntity tile;
    private boolean init;

    private ArrayList<MethodAccess> dataAccess = new ArrayList<MethodAccess>();
    private ArrayList<Savable> dataEffects = new ArrayList<Savable>();

    private MethodAccess blockAccess;

    protected BlockData() {}

    public BlockData(ChunkCoordinates coords, Class[] list, int containingID, int blockID) {
        super(coords);
        this.blockID = blockID;
        this.containingID = containingID;

        for (Savable effect : classesToEffects(list)){
            Effect annot = effect.getClass().getAnnotation(Effect.class);

            if(annot.hasTileEntity())
                tileClass = (Class<? extends TileEntity>)annot.tileentity();
            dataEffects.add(effect);
            dataAccess.add(MethodAccess.get(effect.getClass()));
        }
    }

    public void initAspects(World world, int x, int y, int z){
        for(int a = 0; a < dataEffects.size(); a++) {
            Savable effect = dataEffects.get(a);
            runAspectMethod(effect.getClass().getSimpleName().toLowerCase() + "Init", effect, world, x, y, z);
        }

        if(tile == null)
            tile = getTileEntity();
        if(tile != null)
            world.setTileEntity(x, y, z, tile);
        init = true;
    }

    private Savable[] classesToEffects(Class[] list) {
        Savable[] effects = new Savable[list.length];
        for (int i = 0; i < effects.length; i++) {
            try {
                effects[i] = (Savable) list[i].newInstance();
            }catch (Exception e){}
        }
        return effects;
    }

    public <T>T runMethod(boolean shouldBlockRun, Class<T> type, T defRet, Object... pars) {
        if(blockAccess == null) blockAccess = MethodAccess.get(Block.class);
        T ret = null;
        String methName = Thread.currentThread().getStackTrace()[2].getMethodName();

        for (int s = 0; s < dataEffects.size(); s++) {
            Savable effect = dataEffects.get(s);
            int effectIndex = AspectHandler.getMethod(effect.getClass(), methName);
            if (effectIndex != -1)
                try { ret = type.cast(dataAccess.get(s).invoke(effect, effectIndex, pars)); } catch (Exception e) {}
        }
        if(shouldBlockRun)
            try {ret = type.cast(blockAccess.invoke(getContainingBlock(),BlockHandler.getMethod(methName), pars));}catch (Exception e){}
        if(ret == null)
            return defRet;
        else
            return ret;
    }

    public Object runAspectMethod(String meth, Savable effect, Object... pars) {
        int effectIndex = AspectHandler.getMethod(effect.getClass(), meth);
        if (effectIndex != -1)
             try { return dataAccess.get(dataEffects.indexOf(effect)).invoke(effect, effectIndex, pars); } catch (Exception e) {}
        return null;
    }

    public boolean isInit(){
        return init;
    }

    public boolean canOpenGUI(){
        for(Savable effect : dataEffects) return AspectHandler.getEffectGUI(effect.getClass()) != null;
        return false;
    }

    public TileEntity getTileEntity(){
        if(tileClass == null)
            return null;

        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("id", "tile_" + tileClass.getSimpleName());
        return TileEntity.createAndLoadEntity(tagCompound);
    }

    public Block getContainingBlock() {
        return Block.getBlockById(containingID);
    }

    public Block getBlock() {
        return Block.getBlockById(blockID);
    }

    public Class[] getEffects() {
        Class[] classes = new Class[dataEffects.size()];
        for (int i = 0; i < classes.length; i++) classes[i] = dataEffects.get(i).getClass();
        return classes;
    }

    public ArrayList<Aspect> getAspects(){
        ArrayList<Aspect> aspects = new ArrayList<Aspect>();
        for(Savable effect : dataEffects)
             aspects.add(AspectHandler.getAspectsFromEffect(effect.getClass()));
        return aspects;
    }

    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);
        tagCompound.setInteger("BlockID", blockID);

        tagCompound.setInteger("length", dataEffects.size());
        for (int i = 0; i < dataEffects.size(); i++) {
            NBTTagCompound effectTag = new NBTTagCompound();
            dataEffects.get(i).writeNBT(effectTag);
            tagCompound.setTag("effect: " + i, effectTag);
        }

        tagCompound.setInteger("ContainingID", containingID);

        if(tile != null){
            NBTTagCompound tileTag = new NBTTagCompound();
            tile.writeToNBT(tileTag);
            tagCompound.setTag("Tile", tileTag);
        }
    }

    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);
        blockID = tagCompound.getInteger("BlockID");

        for (int i = 0; i < tagCompound.getInteger("length"); i++) {
            dataEffects.add(Savable.loadDataFromNBT(tagCompound.getCompoundTag("effect: " + i)));
            dataAccess.add(MethodAccess.get(dataEffects.get(i).getClass()));
        }

        containingID = tagCompound.getInteger("ContainingID");

        if(tagCompound.hasKey("Tile"))
            tile = TileEntity.createAndLoadEntity(tagCompound.getCompoundTag("Tile"));
    }
}
