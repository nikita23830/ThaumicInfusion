package drunkmafia.thaumicinfusion.common.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TickEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent event){
        World world = Minecraft.getMinecraft().theWorld;
        if(world != null)
            tickWorld(world);
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event){
        tickWorld(event.world);
    }

    void tickWorld(World world){
        BlockSavable[] savables = BlockHelper.getWorldData(world).getAllBocks();
        for(BlockSavable block : savables) {
            if (block instanceof BlockData) {
                for (AspectEffect effect : ((BlockData) block).getEffects()) {
                    try {
                        effect.updateBlock(world);
                    }catch (Exception e){
                        if(!world.isRemote)
                            BlockHelper.destroyBlock(world, block.getCoords());
                        ThaumicInfusion.getLogger().log(Level.ERROR, "Block at: " + block.getCoords().toString());
                    }
                }
            }
        }
    }
}
