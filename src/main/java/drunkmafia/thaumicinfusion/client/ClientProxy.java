package drunkmafia.thaumicinfusion.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import drunkmafia.thaumicinfusion.client.cape.CapeHandler;
import drunkmafia.thaumicinfusion.client.event.EventHookContainerClass;
import drunkmafia.thaumicinfusion.client.gui.CreativeInfusionGUI;
import drunkmafia.thaumicinfusion.client.gui.InfusedBlockGUI;
import drunkmafia.thaumicinfusion.client.renderer.item.*;
import drunkmafia.thaumicinfusion.client.renderer.tile.*;
import drunkmafia.thaumicinfusion.common.CommonProxy;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.block.*;
import drunkmafia.thaumicinfusion.common.block.tile.*;
import drunkmafia.thaumicinfusion.common.container.InfusedBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.common.Thaumcraft;

import static drunkmafia.thaumicinfusion.common.block.TIBlocks.*;

public class ClientProxy extends CommonProxy {
    @Override
    public void initRenderers() {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TIBlocks.essentiaBlock), new EssentiaBlockRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TIBlocks.infusionCoreBlock), new CoreItemRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TIBlocks.infusedBlock), new InfusedItemRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(InfusionCoreTile.class, new InfusionCoreRenderer());

        MinecraftForge.EVENT_BUS.register(new EventHookContainerClass());
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
