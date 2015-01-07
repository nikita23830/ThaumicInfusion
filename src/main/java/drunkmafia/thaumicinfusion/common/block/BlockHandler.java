package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.util.classes.SafeClassGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;


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
        config.addCustomCategoryComment("Blocks", "Blocks that are not allowed to be infused - NOTE: Will use server side config to decide, no conflicts will arise if configs are different");

        Iterator blocksIter = Block.blockRegistry.iterator();
        while (blocksIter.hasNext()) {
            Block block = (Block) blocksIter.next();
            if (config.get("Blocks", block.getLocalizedName(), false).getBoolean())
                blacklistedBlocks.add(block.getUnlocalizedName().toLowerCase());
        }

        config.save();
        ThaumicInfusion.instance.logger.info("Blacklisted: " + blacklistedBlocks.size() + " out of " + Block.blockRegistry.getKeys().size());
    }

    @SuppressWarnings("unchecked")
    public static void generateSafeTiles(){
        Logger logger = ThaumicInfusion.getLogger();
        logger.info("Generating Safe version of TE's");
        long start = System.currentTimeMillis();
        int size = 0;
        ArrayList<Class<? extends TileEntity>> safeTiles = new ArrayList<Class<? extends TileEntity>>();
        try{
            SafeClassGenerator generator = new SafeClassGenerator();
            generator.lowestSuper(generator.getCtClass(TileEntity.class));
            generator.setLog(logger);

            Map<String, Class> tiles = (Map<String, Class>) TileEntity.nameToClassMap;
            size = tiles.size();
            Iterator<String> iterator = tiles.keySet().iterator();

            while(iterator.hasNext()){
                Class tileClass = tiles.get(iterator.next());
                Class safeTile = generator.generateSafeClass(generator.getCtClass(tileClass));
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
        logger.info("Finished the generation of safe TE, " + safeTiles.size() + " out of " + size + " were generated and it took " + (System.currentTimeMillis() - start) + " ms");
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

    public static void banBlock(Block block){
        Configuration config = ThaumicInfusion.instance.config;
        config.load();
        boolean ban = !config.get("Blocks", block.getLocalizedName(), false).getBoolean();
        config.get("Blocks", block.getLocalizedName(), false).set(ban);

        if(!ban)
            blacklistedBlocks.remove(block.getUnlocalizedName().toLowerCase());
        else
            blacklistedBlocks.add(block.getUnlocalizedName().toLowerCase());
        config.save();
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
