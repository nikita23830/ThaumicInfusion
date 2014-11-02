package drunkmafia.thaumicinfusion.client.gui;

import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.container.CreativeInfusionContainer;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.client.SendInfusePacketS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.AspectList;

/**
 * Created by DrunkMafia on 07/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class CreativeInfusionGUI extends GuiContainer {

    ResourceLocation gui = new ResourceLocation(ModInfo.MODID, "textures/gui/CreativeInfusionGUI.png");
    EntityPlayer player;

    public CreativeInfusionGUI(EntityPlayer player) {
        super(new CreativeInfusionContainer(player.inventory));

        this.player = player;

        xSize = 176;
        ySize = 132;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float deltaTime, int mX, int mY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(gui);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.clear();

        buttonList.add(new GuiButton(0, guiLeft + 80, guiTop + 14, 48, 20, "Infuse"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0){
            CreativeInfusionContainer.Inventory inventory = ((CreativeInfusionContainer)inventorySlots).getTempInv();
            ItemStack phial = inventory.getStackInSlot(0);
            ItemStack block = inventory.getStackInSlot(1);
            if(phial != null && block != null)
                ChannelHandler.network.sendToServer(new SendInfusePacketS(inventory));
        }
    }
}
