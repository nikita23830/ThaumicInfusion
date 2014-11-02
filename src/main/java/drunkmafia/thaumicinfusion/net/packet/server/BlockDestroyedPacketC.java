package drunkmafia.thaumicinfusion.net.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

/**
 * Created by DrunkMafia on 28/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockDestroyedPacketC implements IMessage {

    public BlockDestroyedPacketC(){}

    private ChunkCoordinates coordinates;

    public BlockDestroyedPacketC(ChunkCoordinates coordinates){
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

    public static class Handler implements IMessageHandler<BlockDestroyedPacketC, IMessage> {

        @Override
        public IMessage onMessage(BlockDestroyedPacketC message, MessageContext ctx) {
            if(message.coordinates == null || ctx.side.isServer()) return null;
            EntityPlayer player = ChannelHandler.getPlayer(ctx);
            BlockHelper.getWorldData(player.worldObj).removeBlock(message.coordinates);
            return null;
        }
    }
}
