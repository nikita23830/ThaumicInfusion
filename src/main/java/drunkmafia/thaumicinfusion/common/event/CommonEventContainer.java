package drunkmafia.thaumicinfusion.common.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.IWorldData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import drunkmafia.thaumicinfusion.net.packet.CooldownPacket;
import net.minecraft.block.Block;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

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
        WorldSavedData data = loadEvent.world.perWorldStorage.loadData(TIWorldData.class, loadEvent.world.getWorldInfo().getWorldName() + "_TIDATA");
        if(data != null) {
            ((TIWorldData) data).world = loadEvent.world;
            ((TIWorldData) data).postLoad();
        }
    }
}
