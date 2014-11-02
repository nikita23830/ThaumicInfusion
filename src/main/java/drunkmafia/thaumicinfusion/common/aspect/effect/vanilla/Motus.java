package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.util.EffectGUI;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by DrunkMafia on 19/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@SuppressWarnings("ALL")
@Effect(aspect = ("motus"), cost = 3)
public class Motus extends Savable {

    private ForgeDirection dir;

    public Motus() {
        dir = ForgeDirection.EAST;
    }

    public void onEntityWalking(World world, int x, int y, int z, Entity ent) {
        moveEntity(ent);
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {
        moveEntity(ent);
    }

    public void moveEntity(Entity ent) {
        switch (dir) {
            case NORTH:
                ent.motionX = 1;
                break;
            case SOUTH:
                ent.motionX = -1;
                break;
            case EAST:
                ent.motionZ = 1;
                break;
            case WEST:
                ent.motionZ = -1;
                break;
        }
    }

    public static class GUI extends EffectGUI {
        @Override
        public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            fontRendererObj.drawString("Hello World", 10, 10, 0x404040);
        }
    }
}
