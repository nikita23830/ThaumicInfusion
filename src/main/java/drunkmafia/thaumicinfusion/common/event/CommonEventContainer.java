package drunkmafia.thaumicinfusion.common.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.helper.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;

/**
 * Created by DrunkMafia on 04/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class CommonEventContainer {

    @SubscribeEvent
    public void onClick(PlayerInteractEvent event) {
        BlockSavable[] blocks = BlockHelper.getWorldData(event.world).getAllBocks();
        if(blocks == null || blocks.length == 0)
            return;

        for(BlockSavable savable : blocks)
            if(savable instanceof BlockData)
                for(AspectEffect effect : ((BlockData)savable).getEffects())
                    effect.worldBlockInteracted(event.entityPlayer, event.world, event.x, event.y, event.z, event.face);
    }



    @SubscribeEvent
    public void load(WorldEvent.Load loadEvent){
        TIWorldData data = BlockHelper.getWorldData(loadEvent.world);
        data.postLoad();
    }
}
