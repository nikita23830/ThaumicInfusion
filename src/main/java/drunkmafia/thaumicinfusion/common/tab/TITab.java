package drunkmafia.thaumicinfusion.common.tab;

import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.lib.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class TITab {

    public static CreativeTabs tab;

    public static void init() {
        tab = new Tab();
    }
}

class Tab extends CreativeTabs {

    public Tab() {
        super(ModInfo.CREATIVETAB_UNLOCAL);
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(TIBlocks.infusionCoreBlock);
    }
}
