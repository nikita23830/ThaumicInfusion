package drunkmafia.thaumicinfusion.client.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import org.apache.logging.log4j.Logger;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class RenderInfused implements ISimpleBlockRenderingHandler {

    public static int id = -1;

    public RenderInfused() {
        if(id == -1)
            id = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void renderInventoryBlock(Block block, int i, int i1, RenderBlocks renderBlocks) { }

    @Override
    public boolean renderWorldBlock(IBlockAccess access, int x, int y, int z, Block block, int meta, RenderBlocks renderBlocks) {
        BlockData data = BlockHelper.getData(BlockData.class, Minecraft.getMinecraft().theWorld, new ChunkCoordinates(x, y, z));

        if(data == null)
            return false;

        return renderBlocks.renderBlockByRenderType(data.getContainingBlock(), x, y, z);
    }

    @Override
    public boolean shouldRender3DInInventory(int i) {
        return false;
    }

    @Override
    public int getRenderId() {
        return id;
    }
}
