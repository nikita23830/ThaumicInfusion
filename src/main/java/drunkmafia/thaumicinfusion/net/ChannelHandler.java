package drunkmafia.thaumicinfusion.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.net.packet.client.*;
import drunkmafia.thaumicinfusion.net.packet.server.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by DrunkMafia on 20/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ChannelHandler{

    public static SimpleNetworkWrapper network;

    public static void init() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);

        Side S = Side.SERVER, C = Side.CLIENT;

        //Server Handled Packets
        network.registerMessage(RequestChunkPacketS.Handler.class, RequestChunkPacketS.class, 0, S);
        network.registerMessage(RequestBlockPacketS.Handler.class, RequestBlockPacketS.class, 1, S);
        network.registerMessage(RequestTilePacketS.Handler.class, RequestTilePacketS.class, 2, S);

        //Client Handled Packets
        network.registerMessage(BlockDestroyedPacketC.Handler.class, BlockDestroyedPacketC.class, 3, C);
        network.registerMessage(BlockSyncPacketC.Handler.class, BlockSyncPacketC.class, 4, C);
        network.registerMessage(TileSyncPacketC.Handler.class, TileSyncPacketC.class, 5, C);
        network.registerMessage(SensusPacketC.Handler.class, SensusPacketC.class, 6, C);
    }

    public static EntityPlayer getPlayer(MessageContext ctx){
        if(ctx.side.isClient()) return Minecraft.getMinecraft().thePlayer;
        if(ctx.side.isServer()) return ctx.getServerHandler().playerEntity;
        return null;
    }
}
