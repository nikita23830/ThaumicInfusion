package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.tab.TITab;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.EssentiaData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.BlockDestroyedPacketC;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.List;
import java.util.Map;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.*;

/**
 * Created by DrunkMafia on 29/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class EssentiaBlock extends Block {

    public EssentiaBlock() {
        super(Material.rock);
        setCreativeTab(TITab.tab);
        setBlockName(essentiaBlock_UnlocalizedName);
        setHardness(1.5F);
        setLightLevel(1F);
        setResistance(10.0F);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        Object[] objs = Aspect.aspects.entrySet().toArray();
        for(Object obj : objs){
            for(int i = 0; i <= 2; i++) {
                NBTTagCompound tag = new NBTTagCompound();
                Aspect aspect = (Aspect) ((Map.Entry) obj).getValue();
                tag.setString("aspectTag", aspect.getTag());
                ItemStack stack = new ItemStack(this);
                stack.setItemDamage(i);
                stack.setTagCompound(tag);
                stack.setStackDisplayName(aspect.getName() + (i != 0 ? (i == 1 ? " Brick" : " chiseled") : ""));
                list.add(stack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon brick;
    @SideOnly(Side.CLIENT)
    private IIcon squarebrick;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon(essentiaBlock_BlockTexture);
        brick = icon.registerIcon(essentiaBlock_BrickTexture);
        squarebrick = icon.registerIcon(essentiaBlock_SquareTexture);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (meta){
            case 1: return brick;
            case 2: return squarebrick;
            default: return blockIcon;
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
         BlockHelper.getData(BlockData.class, world, new ChunkCoordinates(x, y, z));
        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase ent, ItemStack stack) {
        if(!world.isRemote) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null) {
                EssentiaData data = new EssentiaData(new ChunkCoordinates(x, y, z), Aspect.getAspect(tagCompound.getString("aspectTag")));
                BlockHelper.getWorldData(world).addBlock(world, data);
                world.setBlockMetadataWithNotify(x, y, z, stack.getItemDamage(), 3);
            } else world.setBlock(x, y, z, Blocks.air);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        BlockSavable data = BlockHelper.getData(BlockData.class, world, new ChunkCoordinates(x, y, z));
        if(data != null) {
            int meta = world.getBlockMetadata(x, y, z);
            ItemStack stack = new ItemStack(this, 1, meta);
            NBTTagCompound tagCompound = new NBTTagCompound();

            Aspect aspect = ((EssentiaData) data).getAspect();
            tagCompound.setString("aspectTag", aspect.getTag());
            stack.setTagCompound(tagCompound);
            stack.setStackDisplayName(aspect.getName() + (meta != 0 ? (meta == 1 ? " Brick" : " chiseled") : ""));

            return stack;
        }
        return null;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if(!world.isRemote){
            BlockSavable data = BlockHelper.getData(BlockData.class, world, new ChunkCoordinates(x, y, z));
            if(data != null) {
                BlockHelper.getWorldData(world).removeBlock(data.getCoords());

                ItemStack stack = new ItemStack(this, 1, meta);
                NBTTagCompound tagCompound = new NBTTagCompound();
                Aspect aspect = ((EssentiaData) data).getAspect();
                tagCompound.setString("aspectTag", aspect.getTag());
                stack.setTagCompound(tagCompound);
                stack.setStackDisplayName(aspect.getName() + (meta != 0 ? (meta == 1 ? " Brick" : " chiseled") : ""));
                super.dropBlockAsItem(world, x, y, z, stack);
            }
        }
    }

    @Override
    protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack nul) {}

    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess access, int x, int y, int z){
        EssentiaData data = BlockHelper.getData(EssentiaData.class, access, new ChunkCoordinates(x, y, z));
        if(data == null || data.getAspect() == null)
            return 0;
        return data.getAspect().getColor();
    }
}
