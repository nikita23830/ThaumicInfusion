package drunkmafia.thaumicinfusion.common.world;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DrunkMafia on 27/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class WorldEventHandler {
    @SubscribeEvent
    public void load(WorldEvent.Load loadEvent){
        WorldSavedData data = loadEvent.world.perWorldStorage.loadData(TIWorldData.class, loadEvent.world.getWorldInfo().getWorldName() + "_TIDATA");
        if(data != null) {
            ((TIWorldData) data).world = loadEvent.world;
            ((TIWorldData) data).postLoad();
        }
    }
}
