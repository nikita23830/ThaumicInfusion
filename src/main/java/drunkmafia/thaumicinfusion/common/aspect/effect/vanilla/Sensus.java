package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.EffectSyncPacketC;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.items.armor.ItemGoggles;

import java.util.Random;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("sensus"), cost = 4, hasCustomBlock = true)
public class Sensus extends AspectEffect {

    @Override
    public InfusedBlock getBlock() {
        return new InfusedBlock(Material.rock).setPass(0);
    }

    @Override
    public void aspectInit(World world,ChunkCoordinates pos) {
        super.aspectInit(world, pos);
        if(!world.isRemote)
            updateTick(world, pos.posX, pos.posY, pos.posZ, new Random());
    }

    public boolean goggles, oldGooggles;

    public void updateTick(World world, int x, int y, int z, Random rand) {
        ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
        goggles = stack != null && stack.getItem() instanceof ItemGoggles;

        if (goggles != oldGooggles) {
            ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
            Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(x, y, z);
            oldGooggles = goggles;
        }else if(rand.nextInt(100) >= rand.nextInt(100))
            ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 50);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        IIcon icon = BlockHelper.getData(BlockData.class, access, new ChunkCoordinates(x, y, z)).getContainingBlock().getIcon(access, x, y, z, side);
        return goggles ? icon : access.getBlock(x, y, z).getIcon(0, 0);
    }

    @Override
    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);
        tagCompound.setByte("goggles", goggles ? (byte) 1 : (byte) 0);
    }

    @Override
    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);
        if(tagCompound.hasKey("goggles"))
            goggles = tagCompound.getByte("goggles") == 1;
    }
}
