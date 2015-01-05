package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.ClassFinder;
import drunkmafia.thaumicinfusion.common.util.SafeTile;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AttributeInfo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.infusedBlock_UnlocalizedName;

/**
 * Created by DrunkMafia on 04/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockHandler {

    private static ArrayList<String> blockMethods;
    private static ArrayList<String> blacklistedBlocks = new ArrayList<String>();
    private static HashMap<String, InfusedBlock> infusedBlocks = new HashMap<String, InfusedBlock>();
    private static HashMap<Class<? extends TileEntity>, Class> safeTileEntities = new HashMap<Class<? extends TileEntity>, Class>();

    private static HashMap<Item, Block> extraInfusionIndex = new HashMap<Item, Block>();

    public static void addBlock(String key, InfusedBlock block){
        if(!infusedBlocks.containsKey(key.toLowerCase()))
            infusedBlocks.put(key.toLowerCase(), block);
    }

    public static InfusedBlock getBlock(String key){
        return infusedBlocks.get(("tile." + infusedBlock_UnlocalizedName + "." + key).toLowerCase());
    }

    public static boolean isBlockMethod(String methName) {
        if(blockMethods == null){
            blockMethods = new ArrayList<String>();
            Method[] methods = InfusedBlock.class.getMethods();
            for(Method method : methods)
                blockMethods.add(method.getName());
        }
        return blockMethods.contains(methName);
    }

    public static void addInfusion(Item item, Block block){
        if(extraInfusionIndex.containsKey(item))
            extraInfusionIndex.remove(item);

        if(extraInfusionIndex.containsValue(block))
            for(Item key : extraInfusionIndex.keySet())
                if(extraInfusionIndex.get(key) == block)
                    extraInfusionIndex.remove(key);

        extraInfusionIndex.put(item, block);
    }

    public static Block getInfusionBlock(Item item){
        return extraInfusionIndex.get(item);
    }

    public static void blacklistBlocks(){
        Configuration config = ThaumicInfusion.instance.config;
        config.load();
        config.addCustomCategoryComment("Blocks", "Blocks that are allowed to be infused - NOTE: Will use server side config to decide, no conflicts will arise if configs are different");

        Iterator blocksIter = Block.blockRegistry.iterator();
        while (blocksIter.hasNext()) {
            Block block = (Block) blocksIter.next();
            if (config.hasKey("Blocks", block.getLocalizedName()))
                blacklistedBlocks.add(block.getUnlocalizedName());
        }

        config.save();
        ThaumicInfusion.instance.logger.info("Blacklisted: " + blacklistedBlocks.size() + " out of " + Block.blockRegistry.getKeys().size());
    }
    //TODO:
    public static void generateSafeTiles(){
        Logger logger = ThaumicInfusion.getLogger();
        long start = System.currentTimeMillis();
        ArrayList<Class<? extends TileEntity>> safeTiles = new ArrayList<Class<? extends TileEntity>>();
        try{
            logger.info("Starting Generation of Safe Tileentities");
            Field tileentityMapping = TileEntity.class.getDeclaredField("nameToClassMap");
            tileentityMapping.setAccessible(true);

            ClassPool cp = ClassPool.getDefault();
            Map<String, Class> tiles = (Map<String, Class>) tileentityMapping.get(null);

            Iterator<String> iterator = tiles.keySet().iterator();

            while(iterator.hasNext()){
                Class tileClass = tiles.get(iterator.next());
                CtClass ctTileClass = cp.get(tileClass.getName());
                CtClass ctTileSafe = makeTileSafe(ctTileClass);

                Class safeTile = ctTileSafe.toClass();
                safeTiles.add(safeTile);

                safeTileEntities.put(tileClass, safeTile);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }

        for(Class tile : safeTiles) {
            try {
                GameRegistry.registerTileEntity(tile, tile.getSimpleName() + "_Tile");
            }catch (Throwable e){}
        }

        logger.info("Finished Generation: " + ((start - System.currentTimeMillis()) / 1000L));
    }

    static CtClass exception;

    static CtClass makeTileSafe(CtClass orginal){
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass safeTileClass = cp.makeClass(new ByteArrayInputStream(orginal.toBytecode()), false);

            safeTileClass.stopPruning(true);
            safeTileClass.setName(safeTileClass.getName() + "Safe");
            safeTileClass.setSuperclass(orginal);
            safeTileClass.addInterface(cp.get(SafeTile.class.getName()));


            for (CtMethod method : safeTileClass.getDeclaredMethods())
                addCatchToMethod(method);
            safeTileClass.stopPruning(false);
            return safeTileClass;
        }catch (Exception e){
            e.printStackTrace();
        }
        return orginal;
    }

    static void addCatchToMethod(CtMethod method){
        try {
            if(exception == null)
                exception = ClassPool.getDefault().get(Exception.class.getName());
            method.addCatch("{drunkmafia.thaumicinfusion.common.block.InfusedBlock.handleError($e, worldObj, xCoord, yCoord, zCoord); throw $e;}", exception);
        }catch (Exception e){}
    }

    public static TileEntity getSafeTile(Class<? extends TileEntity> clazz){
        Class<? extends TileEntity> safeTile = safeTileEntities.get(clazz);
        try{
            return safeTile != null ? safeTile.newInstance() : clazz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isBlockBlacklisted(Block block){
        for(String unlocal : blacklistedBlocks)
            if(unlocal.toLowerCase().matches(block.getUnlocalizedName().toLowerCase()))
                return true;
        return false;
    }

    public static boolean hasBlock(String unlocal){
        return infusedBlocks.containsKey(unlocal);
    }

    public static InfusedBlock[] getBlocks(){
        InfusedBlock[] blocks = new InfusedBlock[infusedBlocks.size()];

        Map.Entry[] set = new Map.Entry[blocks.length];
        infusedBlocks.entrySet().toArray(set);

        for(int i = 0; i < set.length; i++)
            blocks[i] = (InfusedBlock) set[i].getValue();

        return blocks;
    }
}
