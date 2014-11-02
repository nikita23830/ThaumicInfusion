package drunkmafia.thaumicinfusion.net.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 28/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockSyncPacketC implements IMessage {

    public BlockSyncPacketC() {
    }

    private BlockSavable data;

    public BlockSyncPacketC(BlockSavable data) {
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            NBTTagCompound tag = new PacketBuffer(buf).readNBTTagCompoundFromBuffer();
            if (tag != null)
                data = (BlockSavable) BlockSavable.loadDataFromNBT(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            if (data != null) {
                NBTTagCompound tag = new NBTTagCompound();
                data.writeNBT(tag);
                new PacketBuffer(buf).writeNBTTagCompoundToBuffer(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<BlockSyncPacketC, IMessage> {
        @Override
        public IMessage onMessage(BlockSyncPacketC message, MessageContext ctx) {
            BlockSavable data = message.data;
            if (data == null || ctx.side.isServer()) return null;
            World world = ChannelHandler.getPlayer(ctx).worldObj;
            ChunkCoordinates pos = data.getCoords();
            BlockHelper.getWorldData(world).addBlock(world, message.data);
            Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
            world.markAndNotifyBlock(pos.posX, pos.posY, pos.posZ, world.getChunkFromBlockCoords(pos.posX, pos.posZ), block, block, 2);
            return null;
        }
    }
}
