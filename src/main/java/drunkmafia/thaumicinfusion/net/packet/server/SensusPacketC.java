package drunkmafia.thaumicinfusion.net.packet.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.aspect.effect.vanilla.Sensus;
import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class SensusPacketC implements IMessage{

    public SensusPacketC(){

    }

    public ChunkCoordinates pos;
    public boolean goggles;

    public SensusPacketC(ChunkCoordinates pos, boolean goggles){
        this.pos = pos;
        this.goggles = goggles;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        byte packet = buf.readByte();
        if(packet == -1)
            return;
        pos = new ChunkCoordinates(buf.readInt(), buf.readInt(), buf.readInt());
        goggles = buf.readByte() == 1;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(pos != null){
            buf.writeByte(1);
            buf.writeInt(pos.posX);
            buf.writeInt(pos.posY);
            buf.writeInt(pos.posZ);
            buf.writeByte(goggles ? 1 : 0);
        }else buf.writeByte(-1);
    }

    public static class Handler implements IMessageHandler<SensusPacketC, IMessage> {
        @Override
        public IMessage onMessage(SensusPacketC message, MessageContext ctx) {
            if (message.pos == null || ctx.side.isServer()) return null;

            World world = ChannelHandler.getPlayer(ctx).worldObj;
            BlockSavable savable = BlockHelper.getData(world, message.pos);
            if(savable == null || (savable != null && !(savable instanceof BlockData)))
                return null;

            BlockData data = (BlockData)savable;
            Sensus effect = data.getEffect(Sensus.class);
            if(effect != null)
                effect.goggles = message.goggles;

            return null;
        }
    }
}
