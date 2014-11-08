package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.lib.BlockInfo;
import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.BlockSyncPacketC;
import drunkmafia.thaumicinfusion.net.packet.server.SensusPacketC;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.items.armor.ItemGoggles;

import java.util.Random;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.infusedBlock_RegistryName;
import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.infusedBlock_UnlocalizedName;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("sensus"), cost = 4, infusedBlock = "tile." + infusedBlock_UnlocalizedName + "_Sensus")
public class Sensus extends AspectEffect {

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
            ChannelHandler.network.sendToAllAround(new SensusPacketC(new ChunkCoordinates(x, y, z), goggles), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
            Minecraft.getMinecraft().renderGlobal.markBlockForUpdate(x, y, z);
            oldGooggles = goggles;
        }else if(rand.nextInt(100) >= rand.nextInt(100))
            ChannelHandler.network.sendToAllAround(new SensusPacketC(new ChunkCoordinates(x, y, z), goggles), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 50);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        return goggles ? null : TIBlocks.infusedBlock.getIcon(0, 0);
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
