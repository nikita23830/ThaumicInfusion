package drunkmafia.thaumicinfusion.common.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import drunkmafia.thaumicinfusion.common.block.IWorldData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import drunkmafia.thaumicinfusion.net.packet.CooldownPacket;
import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent;

/**
 * Created by DrunkMafia on 04/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class CommonEventContainer {

    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event){
        Block block = event.block;
        if(!(block instanceof IWorldData) || event.world.isRemote)
            return;

        WorldCoord pos = WorldCoord.get(event.x, event.y, event.z);
        if(!event.getPlayer().capabilities.isCreativeMode)
            for (BlockSavable savable : BlockHelper.getWorldData(event.world).getAllDatasAt(pos))
                ((IWorldData) block).breakBlock(event.world, savable);

        BlockHelper.destroyBlock(event.world, pos);
    }

    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.PlaceEvent event){
        Block block = event.block;
        if(!(block instanceof IWorldData) || event.world.isRemote)
            return;

        BlockSavable savable = ((IWorldData)block).getData(event.world, event.itemInHand, WorldCoord.get(event.x, event.y, event.z));
        if(savable == null)
            return;

        BlockHelper.getWorldData(event.world).addBlock(event.world, savable);
    }
}
