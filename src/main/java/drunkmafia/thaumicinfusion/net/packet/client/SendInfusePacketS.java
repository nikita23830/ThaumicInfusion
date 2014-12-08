package drunkmafia.thaumicinfusion.net.packet.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drunkmafia.thaumicinfusion.common.container.CreativeInfusionContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

/**
 * Created by DrunkMafia on 07/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class SendInfusePacketS implements IMessage {

    public SendInfusePacketS() {
    }

    ItemStack phial, block;

    public SendInfusePacketS(CreativeInfusionContainer.Inventory infuseInv){
        phial = infuseInv.getStackInSlot(0);
        block = infuseInv.getStackInSlot(1);

        System.out.println("Packet Maded");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        System.out.println("Reading");
        try {
            NBTTagCompound phialTag = new PacketBuffer(buf).readNBTTagCompoundFromBuffer();
            NBTTagCompound blockTag = new PacketBuffer(buf).readNBTTagCompoundFromBuffer();
            if(phialTag != null && blockTag != null){
                phial = ItemStack.loadItemStackFromNBT(phialTag);
                block = ItemStack.loadItemStackFromNBT(blockTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if(phial != null && block != null){
            System.out.println("Writing");
            try {
                NBTTagCompound phialTag = new NBTTagCompound();
                NBTTagCompound blockTag = new NBTTagCompound();
                phial.writeToNBT(phialTag);
                block.writeToNBT(blockTag);
                new PacketBuffer(buf).writeNBTTagCompoundToBuffer(phialTag);
                new PacketBuffer(buf).writeNBTTagCompoundToBuffer(blockTag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Handler implements IMessageHandler<SendInfusePacketS, IMessage> {
        @Override
        public IMessage onMessage(SendInfusePacketS message, MessageContext ctx) {
            if(ctx.side.isServer() && message.phial != null && message.block != null){
                System.out.println("Creative Infuse Packet, recived");
            }
            return null;
        }
    }
}
