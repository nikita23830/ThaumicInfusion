package drunkmafia.thaumicinfusion.common.lib;

import net.minecraft.util.ResourceLocation;

/**
 * Created by DrunkMafia on 16/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockInfo {

    private static final String TEXTUREBASE = ModInfo.MODID + ":";

    public static final String infusedBlock_UnlocalizedName = "local_infused";
    public static final String infusedBlock_RegistryName = "reg_infused";
    public static final String infusedBlock_BlankTexture = TEXTUREBASE + "blank";

    public static final String essentiaBlock_UnlocalizedName = "local_essentia";
    public static final String essentiaBlock_RegistryName = "reg_essentia";
    public static final String essentiaBlock_BlockTexture = TEXTUREBASE + "essentiablock";
    public static final String essentiaBlock_BrickTexture = TEXTUREBASE + "bricks_essentiablock";
    public static final String essentiaBlock_SquareTexture = TEXTUREBASE + "squarebrick_essentiablock";

    public static final String infusionCoreBlock_UnlocalizedName = "local_infusionCore";
    public static final String infusionCoreBlock_RegistryName = "reg_infusionCore";
    public static final String infusionCoreBlock_TileEntity = "tile_infusionCore";
    public static final ResourceLocation infusionCore_Model = new ResourceLocation(ModInfo.MODID, "models/core.obj");
    public static final ResourceLocation infusionCore_Texture = new ResourceLocation(ModInfo.MODID, "textures/models/core.png");
}
