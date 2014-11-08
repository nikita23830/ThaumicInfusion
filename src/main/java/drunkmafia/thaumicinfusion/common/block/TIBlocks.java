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

    public static InfusedBlock infusedBlock, aquaBlock, sensusBlock, gelumBlock;
    public static Block essentiaBlock;
    public static Block infusionCoreBlock;

    public static void initBlocks() {
        infusedBlock = new InfusedBlock(Material.rock, infusedBlock_UnlocalizedName);
        sensusBlock = new InfusedBlock(Material.rock, infusedBlock_UnlocalizedName + "_Sensus").setPass(0).setRenderAsNormal(false).setIsOpaqueCube(false);
        gelumBlock = new InfusedBlock(Material.rock, infusedBlock_UnlocalizedName + "_Gelum").setSlipperiness(0.98F);
        aquaBlock = new InfusedBlock(Material.water, infusedBlock_UnlocalizedName + "_Aqua").setRenderAsNormal(false).setIsOpaqueCube(false);

        GameRegistry.registerBlock(infusedBlock.addBlockToHandler(), infusedBlock_RegistryName);
        GameRegistry.registerBlock(sensusBlock.addBlockToHandler(), infusedBlock_RegistryName + "_Aqua");
        GameRegistry.registerBlock(gelumBlock.addBlockToHandler(), infusedBlock_RegistryName + "_Sensus");
        GameRegistry.registerBlock(aquaBlock.addBlockToHandler(), infusedBlock_RegistryName + "_Gelum");

        GameRegistry.registerBlock(essentiaBlock = new EssentiaBlock(), essentiaBlock_RegistryName);
        GameRegistry.registerBlock(infusionCoreBlock = new InfusionCoreBlock(), infusionCoreBlock_RegistryName);

        GameRegistry.registerTileEntity(InfusionCoreTile.class, infusionCoreBlock_TileEntity);
    }
}
