package drunkmafia.thaumicinfusion.net.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by DrunkMafia on 01/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TileSyncPacketC implements IMessage {

    private TileEntity tileEntity;

    public TileSyncPacketC(TileEntity tileEntity){
        this.tileEntity = tileEntity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            NBTTagCompound tag = new PacketBuffer(buf).readNBTTagCompoundFromBuffer();
            if (tag != null)
                tileEntity = TileEntity.createAndLoadEntity(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            if (tileEntity != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tileEntity.writeToNBT(tag);
                new PacketBuffer(buf).writeNBTTagCompoundToBuffer(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<TileSyncPacketC, IMessage> {
        @Override
        public IMessage onMessage(TileSyncPacketC message, MessageContext ctx) {
            if (message.tileEntity == null || ctx.side.isServer()) return null;
            EntityPlayer player = ChannelHandler.getPlayer(ctx);
            TileEntity tile = message.tileEntity;
            player.worldObj.setTileEntity(tile.xCoord, tile.yCoord, tile.zCoord, tile);
            return null;
        }
    }
}
