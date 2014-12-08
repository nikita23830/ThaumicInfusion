package drunkmafia.thaumicinfusion.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import drunkmafia.thaumicinfusion.client.event.ClientEventContainer;
import drunkmafia.thaumicinfusion.client.event.ClientTickHandler;
import drunkmafia.thaumicinfusion.client.gui.CreativeInfusionGUI;
import drunkmafia.thaumicinfusion.client.gui.InfusedBlockGUI;
import drunkmafia.thaumicinfusion.client.renderer.RenderInfused;
import drunkmafia.thaumicinfusion.client.renderer.item.*;
import drunkmafia.thaumicinfusion.client.renderer.tile.*;
import drunkmafia.thaumicinfusion.common.CommonProxy;
import drunkmafia.thaumicinfusion.common.block.*;
import drunkmafia.thaumicinfusion.common.block.tile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void initRenderers() {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TIBlocks.essentiaBlock), new EssentiaBlockRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TIBlocks.infusionCoreBlock), new CoreItemRenderer());

        InfusedBlock[] blocks = BlockHandler.getBlocks();
        for(InfusedBlock block : blocks) {
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), new InfusedItemRenderer());

        }

        RenderingRegistry.registerBlockHandler(new RenderInfused());

        ClientRegistry.bindTileEntitySpecialRenderer(InfusionCoreTile.class, new InfusionCoreRenderer());
        
        FMLCommonHandler.instance().bus().register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventContainer());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID){
            case 0: return new InfusedBlockGUI(new ChunkCoordinates(x, y, z));
            case 1: return new CreativeInfusionGUI(player);
            default: return null;
        }
    }
}
