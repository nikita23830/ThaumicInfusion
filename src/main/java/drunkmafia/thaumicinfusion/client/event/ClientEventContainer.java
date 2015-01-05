package drunkmafia.thaumicinfusion.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.lib.BlockInfo;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.MathHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.TIWorldData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by DrunkMafia on 27/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@SideOnly(Side.CLIENT)
public class ClientEventContainer {

    public static boolean debug;

    float angle = 0F, yLevel = 0F, target = 0.05F;

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

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if(!(debug = Minecraft.getMinecraft().gameSettings.showDebugInfo))
            return;

        TIWorldData data = BlockHelper.getWorldData(Minecraft.getMinecraft().theWorld);
        event.left.add("TI: Block Data's in world: " + data.getAllBocks().length);
    }

    ArrayList<String> usernames;

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event) {
        getPlayerNames();

        if(usernames == null || !usernames.contains(event.entityPlayer.getCommandSenderName()))
            return;

         IModelCustom model = AdvancedModelLoader.loadModel(BlockInfo.infusionCore_Model);

         GL11.glPushMatrix();
         GL11.glColor4f(1,  1, 1, 1);
         GL11.glTranslatef(0, 0.3F + yLevel, 0F);
         GL11.glScaled(1F, 1F, 1F);
         GL11.glRotatef(angle, 0, 1, 0);

         Minecraft.getMinecraft().renderEngine.bindTexture(BlockInfo.infusionCore_Texture);
         model.renderAll();

         GL11.glPopMatrix();

         GL11.glPushMatrix();
         GL11.glColor4f(1, 1, 1, 1);
         GL11.glTranslatef(0, 0.3F + -yLevel, 0F);
         GL11.glScaled(0.7F, 1F, 0.7F);
         GL11.glRotatef(-angle, 0, 1, 0);

         Minecraft.getMinecraft().renderEngine.bindTexture(BlockInfo.infusionCore_Texture);
         model.renderAll();

         GL11.glPopMatrix();

         angle++;
         if(angle >= 360)
            angle = 0;

        yLevel = MathHelper.lerp(yLevel, target, 0.005F, 0.0025F);
        if(yLevel == target)
            target = -target;
    }

    boolean init = false;

    public void getPlayerNames(){
        if(init)
            return;

        usernames = new ArrayList<String>();
        try {
            URL url = new URL("https://raw.githubusercontent.com/TheDrunkMafia/ThaumicInfusion/master/TIData.txt");
            Scanner file = new Scanner(url.openStream());
            while(file.hasNext()){
                String str = file.nextLine();
                if(str.contains("US:"))
                    usernames.add(str.substring(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        init = true;
    }
}
