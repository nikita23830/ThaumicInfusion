package drunkmafia.thaumicinfusion.client.renderer.item;

import com.sun.javafx.geom.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.infusionCore_Model;
import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.infusionCore_Texture;

/**
 * Created by DrunkMafia on 20/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class CoreItemRenderer implements IItemRenderer {

    private IModelCustom model = AdvancedModelLoader.loadModel(infusionCore_Model);

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        renderCore(new Vec3f(0.5F, 0.5F, 0.5F), new Vec3f(1.4F, 1.3F, 1.4F), new Vec3f(0, 0, 0), 0);
        renderCore(new Vec3f(0.5F, 0.5F, 0.5F), new Vec3f(1F, 1.3F, 1F), new Vec3f(0, 0, 0), 0);
    }

    public void renderCore(Vec3f coords, Vec3f scale, Vec3f rotation, float angle){
        GL11.glPushMatrix();

        GL11.glTranslatef(coords.x, coords.y, coords.z);
        GL11.glScalef(scale.x, scale.y, scale.z);
        GL11.glRotatef(angle, rotation.x, rotation.y, rotation.z);

        Minecraft.getMinecraft().renderEngine.bindTexture(infusionCore_Texture);
        model.renderAll();

        GL11.glPopMatrix();
    }
}
