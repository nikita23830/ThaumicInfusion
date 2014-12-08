package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import cpw.mods.fml.common.network.NetworkRegistry;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.EffectSyncPacketC;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("superbia"), cost = 4)
public class Superbia extends AspectEffect {

    private Block disguisedBlock;
    private int disguisedMeta;

    @Override
    public void aspectInit(World world, ChunkCoordinates pos) {
        super.aspectInit(world, pos);
        ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), pos.posX, pos.posY, pos.posZ, 50));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float floatX, float floatY, float floatZ) {
        if(!world.isRemote)
            return true;

        if(player.isSneaking()){
            disguisedBlock = null;
            ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if(stack == null || (stack != null && !(stack.getItem() instanceof ItemBlock)))
            return false;

        Block block = Block.getBlockFromItem(stack.getItem());
        if(block == null || (block != null && block.getRenderType() != 0))
            return false;

        disguisedBlock = block;
        disguisedMeta = stack.getItemDamage();

        ChannelHandler.network.sendToAllAround(new EffectSyncPacketC(this), new NetworkRegistry.TargetPoint(world.getWorldInfo().getVanillaDimension(), x, y, z, 50));

        return true;
    }

    @Override
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        if(disguisedBlock == null)
            return null;
        return disguisedBlock.getIcon(side, disguisedMeta);
    }

    @Override
    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);
        if(disguisedBlock == null)
            return;
        tagCompound.setString("disguisedBlock", disguisedBlock.getUnlocalizedName());
        tagCompound.setInteger("disguisedMeta", disguisedMeta);
    }

    @Override
    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);
        if(!tagCompound.hasKey("disguisedBlock"))
            disguisedBlock = null;
        disguisedBlock = Block.getBlockFromName(tagCompound.getString("disguisedBlock"));
        disguisedMeta = tagCompound.getInteger("disguisedMeta");
    }
}
