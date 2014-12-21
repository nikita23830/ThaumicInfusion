package drunkmafia.thaumicinfusion.common.world;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;

public class BlockData extends BlockSavable {

    private int containingID, blockID;
    private TileEntity tile;
    private boolean init;

    private ArrayList<AspectEffect> dataEffects = new ArrayList<AspectEffect>();

    private World worldObj;

    public BlockData() {}

    public BlockData(WorldCoord coords, Class[] list, int containingID, int blockID) {
        super(coords);
        this.blockID = blockID;
        this.containingID = containingID;

        for (AspectEffect effect : classesToEffects(list)){
            TileEntity tempTile = effect.getTile();
            if(tempTile != null)
                tile = tempTile;
            dataEffects.add(effect);
        }
    }

    public void initAspects(World world, int x, int y, int z){
        worldObj = world;

        for(int a = 0; a < dataEffects.size(); a++) {
            AspectEffect effect = dataEffects.get(a);
            effect.aspectInit(world, getCoords());
        }
        if(tile != null)
            world.setTileEntity(x, y, z, tile);
        init = true;
    }

    public <T extends AspectEffect>T getEffect(Class<T> effect){
        for(AspectEffect obj : dataEffects)
            if(obj.getClass() == effect)
                return effect.cast(obj);
        return null;
    }

    public boolean hasEffect(Class<? extends AspectEffect> effect){
        return getEffect(effect) != null;
    }

    private AspectEffect[] classesToEffects(Class[] list) {
        AspectEffect[] effects = new AspectEffect[list.length];
        for (int i = 0; i < effects.length; i++) {
            try {
                effects[i] = (AspectEffect) list[i].newInstance();
            }catch (Exception e){}
        }
        return effects;
    }

    public Block runBlockMethod(){
        String methName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Block block = null;
        for (AspectEffect dataEffect : dataEffects)
            if (dataEffect.hasMethod(methName))
                block = dataEffect;

        return block == null ? getContainingBlock() : block;
    }

    public AspectEffect[] runAllAspectMethod(){
        String methName = Thread.currentThread().getStackTrace()[2].getMethodName();
        ArrayList<AspectEffect> effects = new ArrayList<AspectEffect>();
        for (AspectEffect dataEffect : dataEffects)
            if (dataEffect.hasMethod(methName))
                effects.add(dataEffect);
        return effects.toArray(new AspectEffect[effects.size()]);
    }

    public AspectEffect runAspectMethod(){
        String methName = Thread.currentThread().getStackTrace()[2].getMethodName();
        for (AspectEffect dataEffect : dataEffects)
            if (dataEffect.hasMethod(methName))
                return dataEffect;
        return null;
    }

    public boolean isInit(){
        return init;
    }

    public boolean canOpenGUI(){
        for(AspectEffect effect : dataEffects) return AspectHandler.getInstance().getEffectGUI(effect.getClass()) != null;
        return false;
    }

    public Block getContainingBlock() {
        return Block.getBlockById(containingID);
    }

    public Block getBlock() {
        return Block.getBlockById(blockID);
    }

    public AspectEffect[] getEffects() {
        AspectEffect[] classes = new AspectEffect[dataEffects.size()];
        return dataEffects.toArray(classes);
    }

    public Aspect[] getAspects(){
        AspectEffect[] effects = getEffects();
        Aspect[] aspects = new Aspect[effects.length];
        for(int i = 0; i < effects.length; i++)
            aspects[i] = AspectHandler.getInstance().getAspectsFromEffect(effects[i].getClass());

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

        for (int i = 0; i < tagCompound.getInteger("length"); i++)
            dataEffects.add(AspectEffect.loadDataFromNBT(tagCompound.getCompoundTag("effect: " + i)));
        containingID = tagCompound.getInteger("ContainingID");

        if(tagCompound.hasKey("Tile"))
            tile = TileEntity.createAndLoadEntity(tagCompound.getCompoundTag("Tile"));
    }
}
