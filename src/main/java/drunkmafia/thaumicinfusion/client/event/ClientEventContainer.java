package drunkmafia.thaumicinfusion.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Created by DrunkMafia on 27/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class ClientEventContainer {

    float angle = 0F;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void blockHighlight(DrawBlockHighlightEvent event) {
        if (event.target != null) {
            MovingObjectPosition pos = event.target;
            EntityPlayer player = event.player;
            World world = player.worldObj;
            if (world.getBlock(pos.blockX, pos.blockY, pos.blockZ) instanceof InfusedBlock) {
                BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(pos.blockX, pos.blockY, pos.blockZ));
                if (blockData != null)
                    for (AspectEffect effect : blockData.runAllAspectMethod())
                        effect.blockHighlight(world, pos.blockX, pos.blockY, pos.blockZ, player, pos, event.partialTicks);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event) {
        /**
         IModelCustom model = AdvancedModelLoader.loadModel(infusionCore_Model);

         GL11.glPushMatrix();
         GL11.glColor4f(1, 1, 1, 1);
         GL11.glTranslatef(0, 0.3F, 0F);
         GL11.glScaled(1F, 1F, 1F);
         GL11.glRotatef(angle, 0, 1, 0);

         Minecraft.getMinecraft().renderEngine.bindTexture(infusionCore_Texture);
         model.renderAll();

         GL11.glPopMatrix();

         GL11.glPushMatrix();
         GL11.glColor4f(1, 1, 1, 1);
         GL11.glTranslatef(0, 0.3F, 0F);
         GL11.glScaled(0.7F, 1F, 0.7F);
         GL11.glRotatef(-angle, 0, 1, 0);

         Minecraft.getMinecraft().renderEngine.bindTexture(infusionCore_Texture);
         model.renderAll();

         GL11.glPopMatrix();

         angle++;
         if(angle >= 360)
         angle = 0;
         **/
    }
}
