package drunkmafia.thaumicinfusion.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;

import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.BlockSavable;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;

import java.util.Map;

/**
 * Created by DrunkMafia on 27/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class EventHookContainerClass {

    public boolean renderLast = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    // void blockHighlight(World world, int x, int y, int z, EntityPlayer player, MovingObjectPosition pos, float partialTicks)
    public void blockHighlight(DrawBlockHighlightEvent event){
        if(event.target != null){

            MovingObjectPosition pos =  event.target;
            EntityPlayer player = event.player;
            World world = player.worldObj;
            if(world.getBlock(pos.blockX, pos.blockY, pos.blockZ) instanceof InfusedBlock){
                BlockSavable blockData = BlockHelper.getData(world, new ChunkCoordinates(pos.blockX, pos.blockY, pos.blockZ));
                if (isBlockData(blockData))
                    ((BlockData) blockData).runMethod(false, Object.class, null, world, pos.blockX, pos.blockY, pos.blockZ, player, pos, event.partialTicks);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    // void renderLast(ChunkCoordinates pos, RenderGlobal context, float partialTick)
    public void renderLast(RenderWorldLastEvent event){
        if(renderLast) {
            TIWorldData data = BlockHelper.getWorldData(Minecraft.getMinecraft().theWorld);
            if (data != null) {
                Map.Entry[] worldDataEntries = data.getAllBocks();
                for (Map.Entry ent : worldDataEntries)
                    ((BlockData) ent.getValue()).runMethod(false, Object.class, null, (ChunkCoordinates) ent.getKey(), event.context, event.partialTicks);
            }
        }
    }

    public boolean isBlockData(BlockSavable savable){
        if (savable != null && savable instanceof BlockData) return true;
        return false;
    }
}
