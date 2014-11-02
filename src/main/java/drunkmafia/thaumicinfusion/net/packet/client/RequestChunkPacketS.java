package drunkmafia.thaumicinfusion.net.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.BlockSyncPacketC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;

/**
 * Created by DrunkMafia on 28/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class RequestChunkPacketS implements IMessage {

    public RequestChunkPacketS(){}

    private ChunkCoordinates coordinates;

    public RequestChunkPacketS(ChunkCoordinates coordinates){
        this.coordinates = coordinates;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        coordinates = new ChunkCoordinates(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(coordinates.posX);
        buf.writeInt(coordinates.posY);
        buf.writeInt(coordinates.posZ);
    }

    public static class Handler implements IMessageHandler<RequestChunkPacketS, IMessage> {

        @Override
        public IMessage onMessage(RequestChunkPacketS message, MessageContext ctx) {
            if(message.coordinates == null || ctx.side.isClient()) return null;
            EntityPlayer player = ChannelHandler.getPlayer(ctx);
            Chunk chunk = player.worldObj.getChunkFromBlockCoords(message.coordinates.posX, message.coordinates.posZ);
            if(chunk != null){
                ArrayList<BlockSavable> blocks = BlockHelper.getWorldData(player.worldObj).getBlocksInChunk(chunk);
                for(BlockSavable data : blocks) ChannelHandler.network.sendTo(new BlockSyncPacketC(data), (EntityPlayerMP)player);
            }
            return null;
        }
    }
}


