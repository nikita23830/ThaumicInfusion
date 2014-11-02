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

    public static final String essentiaBlock_UnlocalizedName = "local_essentia";
    public static final String essentiaBlock_RegistryName = "reg_essentia";
    public static final String essentiaBlock_BlockTexture = TEXTUREBASE + "essentiablock";
    public static final String essentiaBlock_BrickTexture = TEXTUREBASE + "bricks_essentiablock";
    public static final String essentiaBlock_SquareTexture = TEXTUREBASE + "squarebrick_essentiablock";

    public static final String jarPedestalBlock_UnlocalizedName = "local_jarPedestal";
    public static final String jarPedestalBlock_RegistryName = "reg_jarPedestal";
    public static final String jarPedestalBlock_TileEntity = "tile_jarPedestal";
    public static final ResourceLocation jarPedestalBlock_Model = new ResourceLocation(ModInfo.MODID, "models/pillar.obj");
    public static final ResourceLocation jarPedestalBlock_Texture = new ResourceLocation("thaumcraft", "textures/models/pillar.png");

    public static final String infusionCoreBlock_UnlocalizedName = "local_infusionCore";
    public static final String infusionCoreBlock_RegistryName = "reg_infusionCore";
    public static final String infusionCoreBlock_TileEntity = "tile_infusionCore";
    public static final ResourceLocation infusionCore_Model = new ResourceLocation(ModInfo.MODID, "models/core.obj");
    public static final ResourceLocation infusionCore_Texture = new ResourceLocation(ModInfo.MODID, "textures/models/core.png");

    public static final ResourceLocation mixerBlock_Model = new ResourceLocation(ModInfo.MODID, "models/mixer.obj");
    public static final ResourceLocation mixerBlock_Texture = new ResourceLocation(ModInfo.MODID, "textures/models/mixer.png");
}
