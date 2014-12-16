package drunkmafia.thaumicinfusion.common.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TickEventHandler {
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event){
        BlockSavable[] savables = BlockHelper.getWorldData(event.player.worldObj).getAllBocks();

        for(BlockSavable block : savables)
            if(block instanceof BlockData)
                for(AspectEffect effect : ((BlockData) block).getEffects())
                    effect.updateBlock(event.player.worldObj);
    }
}
