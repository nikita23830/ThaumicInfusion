package drunkmafia.thaumicinfusion.common.aspect.tileentity;

import net.minecraft.tileentity.TileEntityChest;

/**
 * Created by DrunkMafia on 06/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class VacuosTile extends TileEntityChest {

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public void checkForAdjacentChests() {}
}
