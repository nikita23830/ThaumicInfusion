package drunkmafia.thaumicinfusion.net.packet.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.BlockSyncPacketC;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by DrunkMafia on 28/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class RequestBlockPacketS implements IMessage {

    public RequestBlockPacketS() {
    }

    private ChunkCoordinates coordinates;

    public static HashMap<ChunkCoordinates, Long> syncTimeouts = new HashMap<ChunkCoordinates, Long>();
    private static long maxTimeout = 10000;

    public RequestBlockPacketS(ChunkCoordinates coordinates) {
        if (syncTimeouts.containsKey(coordinates)) {
            long timeout = syncTimeouts.get(coordinates);
            if ((timeout + maxTimeout) < System.currentTimeMillis()) {
                syncTimeouts.put(coordinates, System.currentTimeMillis());
                this.coordinates = coordinates;
            }
        } else {
            syncTimeouts.put(coordinates, System.currentTimeMillis());
            this.coordinates = coordinates;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.readByte() == 1) coordinates = new ChunkCoordinates(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(coordinates != null) {
            buf.writeByte(1);
            buf.writeInt(coordinates.posX);
            buf.writeInt(coordinates.posY);
            buf.writeInt(coordinates.posZ);
        }else buf.writeByte(0);
    }

    public static class Handler implements IMessageHandler<RequestBlockPacketS, IMessage> {
        @Override
        public IMessage onMessage(RequestBlockPacketS message, MessageContext ctx) {
            if (message.coordinates == null || ctx.side.isClient()) return null;
            BlockSavable data = BlockHelper.getData(ChannelHandler.getPlayer(ctx).worldObj, message.coordinates);
            if (data != null)
                return new BlockSyncPacketC(data);
            return null;
        }
    }
}
