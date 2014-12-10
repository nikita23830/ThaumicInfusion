package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.EffectSyncPacketC;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.items.armor.ItemGoggles;

import java.util.Random;

/**
 * Created by DrunkMafia on 06/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("tenebrae"), cost = 4, hasCustomBlock = true)
public class Tenebrae extends AspectEffect {

    @Override
    public InfusedBlock getBlock() {
        return new InfusedBlock(Material.rock).setPass(0);
    }

    @Override
    public void aspectInit(World world,WorldCoord pos) {
        super.aspectInit(world, pos);
        if(!world.isRemote)
            updateTick(world, pos.x, pos.y, pos.z, new Random());
    }

    public boolean isLit, oldIsLit;

    public void updateTick(World world, int x, int y, int z, Random rand) {
        isLit = world.getBlockLightValue(x, y, z) > 8;

        if (isLit != oldIsLit) {
            ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
            oldIsLit = isLit;
        }else if(rand.nextInt(100) >= rand.nextInt(100))
            ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 50);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        if(isLit)  access.getBlock(x, y, z).setBlockBounds(0, 0, 0, 0, 0, 0);
        else access.getBlock(x, y, z).setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if(isLit)  return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
        else return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRender(World world, int x, int y, int z, RenderBlocks render) {
        return !isLit;
    }

    @Override
    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);
        tagCompound.setByte("isLit", isLit ? (byte) 1 : (byte) 0);
    }

    @Override
    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);
        if(tagCompound.hasKey("isLit"))
            isLit = tagCompound.getByte("isLit") == 1;
    }
}
