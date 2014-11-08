package drunkmafia.thaumicinfusion.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import drunkmafia.thaumicinfusion.client.gui.CreativeInfusionGUI;
import drunkmafia.thaumicinfusion.client.renderer.item.InfusedItemRenderer;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.InfusedBlock;
import drunkmafia.thaumicinfusion.common.container.CreativeInfusionContainer;
import drunkmafia.thaumicinfusion.common.container.InfusedBlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import static drunkmafia.thaumicinfusion.common.block.TIBlocks.initBlocks;

public class CommonProxy implements IGuiHandler {

    public void initRenderers() {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID){
            case 0: return new InfusedBlockContainer();
            case 1: return new CreativeInfusionContainer(player.inventory);
            default: return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {return null;}
}
