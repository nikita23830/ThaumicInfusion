package drunkmafia.thaumicinfusion.client.gui;

import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import drunkmafia.thaumicinfusion.common.util.RGB;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by DrunkMafia on 22/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class Slider {

    protected InfusedBlockGUI gui;
    private int posX, posY, sliderX;
    private int xSize = 118;
    private int frame;

    private boolean shouldDraw;

    private ArrayList<SliderSection> sections = new ArrayList<SliderSection>();

    public Slider(InfusedBlockGUI gui, int posX, int posY, Aspect[] aspects){
        this.gui = gui;
        this.posX = posX;
        this.posY = posY;

        this.shouldDraw = aspects.length > 0 ;

        sliderX = posX + (118 / 2);

        for(int i = 0; i < aspects.length; i++){
            Aspect aspect = aspects[i];

            int sectionSizeX = xSize;
            if(aspects.length > 1) sectionSizeX = xSize / aspects.length;
            int sectionPosX = posX + 4;
            if(i > 0) sectionPosX += sectionSizeX * i;

            sections.add(new SliderSection(this, aspect, sectionPosX, posY + 3, sectionSizeX * i, sectionSizeX, 6));
        }
    }

    public void drawGuiContainerBackgroundLayer(float tpf, int mouseX, int mouseY) {
        if(!shouldDraw) return;
        int ySize = 12;
        gui.drawTexturedModalRect(posX, posY, 0, 107, xSize, ySize);

        for(SliderSection aspect : sections) aspect.drawGuiContainerBackgroundLayer(tpf, mouseX, mouseY);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        gui.drawTexturedModalRect(sliderX, posY, 122, 107, 14, 14);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if(!shouldDraw) return;

        for(SliderSection aspect : sections) aspect.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected void mouseClicked(int mouseX, int mouseY, int lastButtonClicked) {
        if(!shouldDraw) return;
        if(mouseX > posX && mouseX < ((posX + xSize) - 12))
            sliderX = mouseX;
    }

    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if(!shouldDraw) return;
        if(mouseX > posX && mouseX < ((posX + xSize) - 12))
            sliderX = mouseX;
    }

    public Aspect getSelectedEffect(){
        for(SliderSection s : sections)
            if(s.inRect(sliderX)) return s.aspect;
        return null;
    }

    public class SliderSection {

        private Slider slider;
        private Aspect aspect;
        private Effect effect;
        private RGB rgb;
        private int posX, posY, u, xSize, ySize;
        private Tooltip tooltip;

        public SliderSection(Slider slider, Aspect aspect, int posX, int posY, int u, int xSize, int ySize){
            this.slider = slider;
            this.aspect = aspect;
            this.posX = posX;
            this.posY = posY;
            this.u = u;
            this.xSize = xSize;
            this.ySize = ySize;

            this.effect = (Effect) AspectHandler.getEffectFromAspect(aspect).getAnnotation(Effect.class);

            tooltip = new Tooltip(StatCollector.translateToLocal("effect." + aspect.getName() + ".name"));

            if(aspect != null)rgb = new RGB(aspect.getColor());
            else rgb = new RGB();
        }

        public void drawGuiContainerBackgroundLayer(float tpf, int mouseX, int mouseY) {
            frame++;
            int maxFrames = 5;
            if(frame >= maxFrames) frame = 1;

            GL11.glColor3f(rgb.getR(), rgb.getG(), rgb.getB());

            slider.gui.drawTexturedModalRect(posX, posY, u, 122 + (6 * frame), xSize, ySize);

            if(isMouseInRect(mouseX, mouseY))
                tooltip.drawGuiContainerBackgroundLayer(tpf, mouseX, mouseY);

        }

        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            if(isMouseInRect(mouseX, mouseY))
                tooltip.drawGuiContainerForegroundLayer(mouseX, mouseY);
        }

        public boolean isMouseInRect(int mouseX, int mouseY){
            return posX <= mouseX && mouseX <= posX + xSize && posY <= mouseY && mouseY <= posY +  ySize;
        }

        public boolean inRect(int sliderX){
            return posX > sliderX || sliderX < (posX + xSize);
        }

        class Tooltip {

            private String str;

            Tooltip(String str){
                this.str = str;
            }

            public void drawGuiContainerBackgroundLayer(float tpf, int mouseX, int mouseY) {
                int baseX = sliderX - (129 / 2);

                slider.gui.drawTexturedModalRect(baseX, posY + 10, 0, 152, 16, 16);
                for(int c = 0; c < str.length(); c++){
                    slider.gui.drawTexturedModalRect(baseX + (c * 8) + 16, posY + 10, 0, 169, 10, 16);
                    if(c == (str.length() / 2)) slider.gui.drawTexturedModalRect(baseX + ((str.length() / 2) * 8) + 16, posY + 7, 17, 152, 6, 6);
                    if((c + 1) == str.length()) slider.gui.drawTexturedModalRect(baseX + (str.length() * 8) + 16, posY + 10, 24, 152, 6, 16);
                }
            }

            public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
                gui.getFontRenderer().drawString(str, sliderX - (129 / 2) - gui.getLeft() + 25 , posY - gui.getTop() + 14, 0x404040);
            }
        }
    }
}
