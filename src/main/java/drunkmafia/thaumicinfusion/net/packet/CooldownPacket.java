package drunkmafia.thaumicinfusion.net.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.world.WorldCoord;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;

/**
 * Created by DrunkMafia on 17/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public abstract class CooldownPacket implements IMessage {

    public static HashMap<WorldCoord, Long> syncTimeouts = new HashMap<WorldCoord, Long>();
    private static long maxTimeout = -1;

    public CooldownPacket() {}

    public boolean canSend(WorldCoord coordinates){
        if(maxTimeout == -1){
            Configuration config = ThaumicInfusion.instance.config;
            config.load();
            maxTimeout = config.get("Networking", "Packet Timeout", 10000, "How many times a single block can send a packet per update, the lower the numbers, the faster an infused or essentia block will be update however can cause lag").getInt();
            config.save();
        }


        if (syncTimeouts.containsKey(coordinates)) {
            long timeout = syncTimeouts.get(coordinates);
            if ((timeout + maxTimeout) < System.currentTimeMillis()) {
                syncTimeouts.put(coordinates, System.currentTimeMillis());
                return true;
            }
        } else {
            syncTimeouts.put(coordinates, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public abstract void fromBytes(ByteBuf buf);

    @Override
    public abstract void toBytes(ByteBuf buf);
}
