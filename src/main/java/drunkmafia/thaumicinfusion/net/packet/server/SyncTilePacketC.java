package drunkmafia.thaumicinfusion.net.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.block.tile.JarPedestalTile;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 18/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class SyncTilePacketC implements IMessage {

    public SyncTilePacketC() {}

    private TileEntity tile;
    private NBTTagCompound tagCompound;

    public SyncTilePacketC(TileEntity tile) {
        System.out.println("Tile Packet");
        this.tile = tile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        System.out.println("Reading");
        try {
            tagCompound = new PacketBuffer(buf).readNBTTagCompoundFromBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        System.out.println("Writing");
        try {
            if (tile != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tile.writeToNBT(tag);
                new PacketBuffer(buf).writeNBTTagCompoundToBuffer(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Handler implements IMessageHandler<SyncTilePacketC, IMessage> {
        @Override
        public IMessage onMessage(SyncTilePacketC message, MessageContext ctx) {
            System.out.println("Syncing Tile");
            NBTTagCompound tag = message.tagCompound;
            int x = tag.getInteger("x"), y = tag.getInteger("z"), z = tag.getInteger("z");
            World world = ChannelHandler.getPlayer(ctx).worldObj;
            TileEntity tile = world.getTileEntity(x, y, z);
            if(tile == null)
                tile = TileEntity.createAndLoadEntity(tag);
            tile.readFromNBT(message.tagCompound);
            world.setTileEntity(x, y, z, tile);
            return null;
        }
    }
}
