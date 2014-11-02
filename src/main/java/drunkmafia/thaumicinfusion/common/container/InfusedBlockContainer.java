package drunkmafia.thaumicinfusion.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by DrunkMafia on 12/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class InfusedBlockContainer extends Container {
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
