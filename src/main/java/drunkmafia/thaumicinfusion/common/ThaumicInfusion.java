package drunkmafia.thaumicinfusion.common;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import drunkmafia.thaumicinfusion.common.aspect.AspectHandler;
import drunkmafia.thaumicinfusion.common.block.BlockHandler;
import drunkmafia.thaumicinfusion.common.block.TIBlocks;
import drunkmafia.thaumicinfusion.common.commands.InfusedInWorldCommand;
import drunkmafia.thaumicinfusion.common.recipe.BlockInfusionRecipe;
import drunkmafia.thaumicinfusion.common.tab.TITab;
import drunkmafia.thaumicinfusion.common.world.WorldEventHandler;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.InfusionRecipe;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static drunkmafia.thaumicinfusion.common.lib.ModInfo.*;

@Mod(modid = MODID, name = NAME, version = VERSION)
public class ThaumicInfusion {

    @Instance(MODID)
    public static ThaumicInfusion instance;

    @SidedProxy(clientSide = CLIENT_PROXY_PATH, serverSide = COMMON_PROXY_PATH)
    public static CommonProxy proxy;

    public static boolean isServer;
    public Item debug;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ThaumicInfusion.isServer = event.getSide().isServer();
        TITab.init();
        TIBlocks.initBlocks();
        AspectHandler.initEffects();

        registerRecipe(new BlockInfusionRecipe("", 10));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ChannelHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
        proxy.initRenderers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        BlockHandler.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new InfusedInWorldCommand());
    }

    public void registerRecipe(InfusionRecipe recipe){
        try{
            Field recipesF = ThaumcraftApi.class.getDeclaredField("craftingRecipes");
            recipesF.setAccessible(true);
            ArrayList recipes = (ArrayList) recipesF.get(null);
            recipes.add(recipe);
            recipesF.set(null, recipes);
            System.out.println("Recipe was registered succesfully");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
