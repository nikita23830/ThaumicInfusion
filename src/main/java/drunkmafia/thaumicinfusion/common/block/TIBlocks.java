package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.block.tile.*;
import net.minecraft.block.Block;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.*;

/**
 * Created by DrunkMafia on 01/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TIBlocks {

    public static Block infusedBlock;
    public static Block essentiaBlock;
    public static Block infusionCoreBlock;

    public static void initBlocks() {
        GameRegistry.registerBlock(infusedBlock = new InfusedBlock(), infusedBlock_RegistryName);
        GameRegistry.registerBlock(essentiaBlock = new EssentiaBlock(), essentiaBlock_RegistryName);
        GameRegistry.registerBlock(infusionCoreBlock = new InfusionCoreBlock(), infusionCoreBlock_RegistryName);

        GameRegistry.registerTileEntity(InfusionCoreTile.class, infusionCoreBlock_TileEntity);
    }
}
