package drunkmafia.thaumicinfusion.net.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.CooldownPacket;
import drunkmafia.thaumicinfusion.net.packet.server.TileSyncPacketC;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

/**
 * Created by DrunkMafia on 01/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class RequestTilePacketS extends CooldownPacket {

    protected WorldCoord coordinates;

    public RequestTilePacketS(WorldCoord coordinates){
        if(canSend(coordinates))
            this.coordinates = coordinates;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.readByte() == 1){
            coordinates = new WorldCoord();
            coordinates.fromBytes(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(coordinates != null) {
            buf.writeByte(1);
            coordinates.toBytes(buf);
        }else buf.writeByte(0);
    }

    public static class Handler implements IMessageHandler<RequestTilePacketS, IMessage> {
        @Override
        public IMessage onMessage(RequestTilePacketS message, MessageContext ctx) {
            WorldCoord pos = message.coordinates;
            if (pos == null || ctx.side.isClient()) return null;
            EntityPlayer player = ChannelHandler.getPlayer(ctx);
            Block block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
            TileEntity tileEntity = player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if(block == null ||tileEntity  == null) return null;
            return new TileSyncPacketC(tileEntity);
        }
    }
}
