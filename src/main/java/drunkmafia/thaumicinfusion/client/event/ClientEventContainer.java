package drunkmafia.thaumicinfusion.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

/**
 * Created by DrunkMafia on 27/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ClientEventContainer {

    public boolean renderLast = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void blockHighlight(DrawBlockHighlightEvent event){
        if(event.target != null){
            MovingObjectPosition pos =  event.target;
            EntityPlayer player = event.player;
            World world = player.worldObj;
            if(world.getBlock(pos.blockX, pos.blockY, pos.blockZ) instanceof InfusedBlock){
                BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(pos.blockX, pos.blockY, pos.blockZ));
                if (blockData != null)
                    for(AspectEffect effect : blockData.runAllAspectMethod())
                        effect.blockHighlight(world, pos.blockX, pos.blockY, pos.blockZ, player, pos, event.partialTicks);
            }
        }
    }
}
