package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.aspect.effect.vanilla.Aqua;
import drunkmafia.thaumicinfusion.common.block.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.*;

/**
 * Created by DrunkMafia on 01/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class TIBlocks {

    public static Block infusedBlock, aquaBlock;
    public static Block essentiaBlock;
    public static Block infusionCoreBlock;

    public static void initBlocks() {
        GameRegistry.registerBlock(infusedBlock = new InfusedBlock(Material.rock, infusedBlock_UnlocalizedName), infusedBlock_RegistryName);
        GameRegistry.registerBlock(aquaBlock = new InfusedBlock(Material.water, infusedBlock_UnlocalizedName + "_Aqua"), infusedBlock_RegistryName + "_Aqua");

        GameRegistry.registerBlock(essentiaBlock = new EssentiaBlock(), essentiaBlock_RegistryName);
        GameRegistry.registerBlock(infusionCoreBlock = new InfusionCoreBlock(), infusionCoreBlock_RegistryName);

        GameRegistry.registerTileEntity(InfusionCoreTile.class, infusionCoreBlock_TileEntity);
    }
}
