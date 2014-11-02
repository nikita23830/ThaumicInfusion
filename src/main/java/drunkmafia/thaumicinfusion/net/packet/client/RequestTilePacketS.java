package drunkmafia.thaumicinfusion.net.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.server.BlockSyncPacketC;
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
public class RequestTilePacketS implements IMessage {

    protected ChunkCoordinates coordinates;

    public RequestTilePacketS(ChunkCoordinates coordinates){
        this.coordinates = coordinates;
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

    public static class Handler implements IMessageHandler<RequestTilePacketS, IMessage> {
        @Override
        public IMessage onMessage(RequestTilePacketS message, MessageContext ctx) {
            ChunkCoordinates pos = message.coordinates;
            if (pos == null || ctx.side.isClient()) return null;
            EntityPlayer player = ChannelHandler.getPlayer(ctx);
            Block block = player.worldObj.getBlock(pos.posX, pos.posY, pos.posZ);
            TileEntity tileEntity = player.worldObj.getTileEntity(pos.posX, pos.posY, pos.posZ);
            if(block == null ||tileEntity  == null) return null;
            return new TileSyncPacketC(tileEntity);
        }
    }
}
