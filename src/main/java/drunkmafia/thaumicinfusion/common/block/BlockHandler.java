package drunkmafia.thaumicinfusion.common.block;

import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Method;
import java.util.*;

import static drunkmafia.thaumicinfusion.common.lib.BlockInfo.infusedBlock_UnlocalizedName;

/**
 * Created by DrunkMafia on 04/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockHandler {

    private static ArrayList<String> whitelistedBlocks = new ArrayList<String>();

    private static HashMap<String, InfusedBlock> infusedBlocks = new HashMap<String, InfusedBlock>();
    private static HashMap<String, Integer> blockMethods = new HashMap<String, Integer>();

    public static void addBlock(String key, InfusedBlock block){
        if(!infusedBlocks.containsKey(key.toLowerCase()))
            infusedBlocks.put(key.toLowerCase(), block);
    }

    public static InfusedBlock getBlock(String key){
        return infusedBlocks.get(("tile." + infusedBlock_UnlocalizedName + "." + key).toLowerCase());
    }

    public static Method getMethod(String methName, Class[] pars) {
        Method[] methods = Block.class.getDeclaredMethods();
        for (Method meth : methods)
            if (meth.getName().matches(methName) && meth.getParameterTypes().length == pars.length) return meth;
        return null;
    }

    static String CATEGORY = "BLOCKLIST";
    public static void whitelistBlocks(){
        Configuration config = new Configuration(ThaumicInfusion.instance.configFile);
        config.load();
        String[] configBlocks = config.get(CATEGORY, "BLOCKS", new String[]{}).getStringList();
        configBlocks = new String[0];
        if(configBlocks.length == 0) {
            ArrayList<String> blocks = new ArrayList<String>();
            Iterator blocksIter = Block.blockRegistry.iterator();
            while (blocksIter.hasNext()) {
                Block block = (Block) blocksIter.next();
                if (!(block instanceof ITileEntityProvider) && block.createTileEntity(null, 0) == null) {
                    whitelistedBlocks.add(block.getUnlocalizedName());
                    blocks.add(block.getUnlocalizedName());
                }
            }
            String[] blockA = new String[blocks.size()];
            blocks.toArray(blockA);
            config.get(CATEGORY, "BLOCKS", new String[]{}).set(blockA);
        }else
            for(String block : configBlocks)
                whitelistedBlocks.add(block);
        config.save();
        ThaumicInfusion.instance.logger.info("Whitelisted: " + whitelistedBlocks.size() + " out of " + Block.blockRegistry.getKeys().size());
    }

    public static boolean isBlockWhitelisted(Block block){
        for(String unlocal : whitelistedBlocks)
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

    public static int getMethod(String methName){
        return blockMethods.get(methName);
    }
}
