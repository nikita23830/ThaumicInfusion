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
import drunkmafia.thaumicinfusion.common.event.WorldEventHandler;
import drunkmafia.thaumicinfusion.common.tab.TITab;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import static drunkmafia.thaumicinfusion.common.lib.ModInfo.*;

@Mod(modid = MODID, name = NAME, version = VERSION)
public class ThaumicInfusion {

    @Instance(MODID)
    public static ThaumicInfusion instance;

    @SidedProxy(clientSide = CLIENT_PROXY_PATH, serverSide = COMMON_PROXY_PATH)
    public static CommonProxy proxy;

    public boolean isServer;
    public Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        isServer = event.getSide().isServer();
        TITab.init();
        TIBlocks.initBlocks();
        AspectHandler.initEffects();
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
        AspectHandler.registerIcons();
        ThaumcraftIntergration.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new InfusedInWorldCommand());
    }

    public static String translate(String key, Object... params) {
        return StatCollector.translateToLocalFormatted(key, params);
    }
}
