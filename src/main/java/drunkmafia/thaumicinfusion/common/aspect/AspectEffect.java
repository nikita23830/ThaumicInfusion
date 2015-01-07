package drunkmafia.thaumicinfusion.common.aspect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.client.gui.EffectGUI;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.helper.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "default", cost = 0)
public class AspectEffect extends Block {

    private static HashMap<Class, ArrayList<String>> phasedMethods = new HashMap<Class, ArrayList<String>>();
    private List<String> methods;

    protected WorldCoord pos;

    public boolean isEnabled = true;

    public AspectEffect() {
        super(Material.air);
        if(!phasedMethods.containsKey(getClass()))
            phaseForMethods();
        methods = phasedMethods.get(getClass());
    }

    public void aspectInit(World world, WorldCoord pos){
        this.pos = pos;
    }

    public WorldCoord getPos(){
        return pos;
    }

    public boolean shouldRegister(){
        return true;
    }

    public InfusedBlock getBlock(){
        return new InfusedBlock(Material.rock);
    }

    @SideOnly(Side.CLIENT)
    public EffectGUI getGUI(){return null;}

    public TileEntity getTile(){return null;}

    public BlockData getData(World world){
        return BlockHelper.getData(BlockData.class, world, getPos());
    }

    public boolean hasMethod(String meth){
        return methods.contains(meth);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRender(World world, int x, int y, int z, RenderBlocks renderBlocks){
        return true;
    }

    public void blockHighlight(World world, int x, int y, int z, EntityPlayer player, MovingObjectPosition pos, float partialTicks){}

    public void worldBlockInteracted(EntityPlayer player, World world, int x, int y, int z, int face) {}

    public void updateBlock(World world){}

    public static AspectEffect loadDataFromNBT(NBTTagCompound tag) {
        if (!tag.hasKey("class")) return null;
        try {
            Class<?> c = Class.forName(tag.getString("class"));
            if (AspectEffect.class.isAssignableFrom(c)) {
                AspectEffect data = (AspectEffect) c.newInstance();
                data.readNBT(tag);
                return data;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void phaseForMethods(){
        Class c = this.getClass();
        List<Method> blockMethods = Arrays.asList(Block.class.getMethods());
        Method[] effectMethods = c.getDeclaredMethods();
        ArrayList<String> meths = new ArrayList<String>();
        for(Method meth : effectMethods)
            for (Method blockMeth : blockMethods)
                if (meth.getName().matches(blockMeth.getName()))
                    meths.add(meth.getName());
        phasedMethods.put(getClass(), meths);
    }

    @SideOnly(Side.CLIENT)
    public void renderBlocksInWorld(IBlockAccess access, int x, int y, int z, Block block, int meta, RenderBlocks renderBlocks){}

    public void writeNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("class", this.getClass().getCanonicalName());
        if(pos != null)
            pos.writeNBT(tagCompound);
    }

    public void readNBT(NBTTagCompound tagCompound) {
        pos = new WorldCoord();
        pos.readNBT(tagCompound);
    }
}
