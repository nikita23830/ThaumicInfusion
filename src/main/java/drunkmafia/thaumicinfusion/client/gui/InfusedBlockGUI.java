package drunkmafia.thaumicinfusion.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.container.InfusedBlockContainer;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.EffectGUI;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

/**
 * Created by DrunkMafia on 12/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class InfusedBlockGUI extends GuiContainer {

    protected BlockData data;
    private World world;
    private ResourceLocation gui = new ResourceLocation(ModInfo.MODID, "textures/gui/InfusedGUI.png");
    private Slider slider;

    private EffectGUI currentEffect;

    public InfusedBlockGUI(WorldCoord coordinates) {
        super(new InfusedBlockContainer());

        xSize = 180;
        ySize = 104;

        world = FMLClientHandler.instance().getClient().theWorld;
        data = BlockHelper.getData(BlockData.class, world, coordinates);
    }

    public void setupEffect(Aspect aspects) {
        AspectHandler handler = AspectHandler.getInstance();
        if (aspects != null) {
            Class effect = handler.getEffectFromAspect(slider.getSelectedEffect());
            if (effect != null) {
                currentEffect = handler.getEffectGUI(effect);
                if (currentEffect != null) {
                    currentEffect.fontRendererObj = fontRendererObj;

                    currentEffect.guiTop = (this.height - this.ySize) / 2 + 20;
                    currentEffect.guiLeft = (this.width - this.xSize) / 2 + 20;

                    currentEffect.initGui();
                }
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        slider = new Slider(this, (guiLeft + (xSize / 2)) - (118 / 2), guiTop + ySize + 10, data.getAspects());
        setupEffect(slider.getSelectedEffect());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float tpf, int mouseX, int mouseY) {
        if (data != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(gui);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

            if (currentEffect != null) currentEffect.drawGuiContainerBackgroundLayer(tpf, mouseX, mouseY);

            slider.drawGuiContainerBackgroundLayer(tpf, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (data != null) {
            if (currentEffect != null) currentEffect.drawGuiContainerForegroundLayer(mouseX, mouseY);

            slider.drawGuiContainerForegroundLayer(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int lastButtonClicked) {
        super.mouseClicked(mouseX, mouseY, lastButtonClicked);
        slider.mouseClicked(mouseX, mouseY, lastButtonClicked);
        setupEffect(slider.getSelectedEffect());
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        slider.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        setupEffect(slider.getSelectedEffect());
    }

    protected int getLeft() {
        return guiLeft;
    }

    protected int getTop() {
        return guiTop;
    }

    protected FontRenderer getFontRenderer() {
        return fontRendererObj;
    }
}
