package drunkmafia.thaumicinfusion.common.world;

import com.sun.istack.internal.NotNull;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.SafeClassGenerator;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;

public class BlockData extends BlockSavable {

    public static long runTime = 0;

    private int containingID, blockID;
    private TileEntity tile;
    public World world;

    private ArrayList<AspectEffect> dataEffects = new ArrayList<AspectEffect>();

    public BlockData() {}

    public BlockData(WorldCoord coords, Class[] list, int containingID, int blockID) {
        super(coords);
        this.blockID = blockID;
        this.containingID = containingID;


        for (AspectEffect effect : classesToEffects(list)) {
            if(!(getContainingBlock() instanceof ITileEntityProvider) && tile == null && effect.getClass().getAnnotation(Effect.class).hasTileEntity())
                tile = effect.getTile();
            dataEffects.add(effect);
        }
    }

    @Override
    public void dataLoad(World world) {
        this.world = world;

        WorldCoord pos = getCoords();
        for(int a = 0; a < dataEffects.size(); a++) {
            AspectEffect effect = dataEffects.get(a);
            effect.aspectInit(world, getCoords());
        }

        if(tile == null && getContainingBlock() instanceof ITileEntityProvider)
            tile = getContainingBlock().createTileEntity(world, world.getBlockMetadata(pos.x, pos.y, pos.z));

        if(tile != null) {
            if(!(tile instanceof SafeClassGenerator.SafeClass)){
                TileEntity safeTile = BlockHandler.getSafeTile(tile.getClass());
                if(safeTile != null){
                    NBTTagCompound tag = new NBTTagCompound();
                    tile.writeToNBT(tag);
                    safeTile.readFromNBT(tag);
                    tile = safeTile;
                }
            }
            if(tile instanceof SafeClassGenerator.SafeClass)
                world.setTileEntity(pos.x, pos.y, pos.z, tile);
            else if(!world.isRemote)
                BlockHelper.destroyBlock(world, pos);
        }

        init = true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockData && ((BlockData)obj).blockID == this.blockID && ((BlockData)obj).containingID == containingID;
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

    @NotNull
    /** Can only be run within a method from the block class **/
    public Block runBlockMethod(){
        long time = System.currentTimeMillis();
        StackTraceElement lastMethod = Thread.currentThread().getStackTrace()[2];
        String methName = lastMethod.getMethodName();
        if(!BlockHandler.isBlockMethod(methName))
            throw new IllegalArgumentException("Attempted to run a block method outside of one, culprit class: " + lastMethod.getClassName() + " from: " + lastMethod.getMethodName());
        Block block = null;
        for (AspectEffect dataEffect : dataEffects)
            if (dataEffect.hasMethod(methName) && dataEffect.isEnabled)
                block = dataEffect;
        runTime = time - System.currentTimeMillis();
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

    Boolean openGUI = null;

    public boolean canOpenGUI(){
        if(openGUI != null)
            return openGUI;


        for(AspectEffect effect : getEffects()){
            Effect annot = effect.getClass().getAnnotation(Effect.class);
            if(annot.hasGUI()) {
                openGUI = true;
                break;
            }else
                openGUI = false;
        }
        return openGUI;
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
