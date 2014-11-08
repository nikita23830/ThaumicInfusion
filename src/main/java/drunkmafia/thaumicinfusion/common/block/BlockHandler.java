package drunkmafia.thaumicinfusion.common.block;

import com.esotericsoftware.reflectasm.MethodAccess;
import drunkmafia.thaumicinfusion.client.renderer.item.InfusedItemRenderer;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by DrunkMafia on 04/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockHandler {

    private static HashMap<String, InfusedBlock> infusedBlocks = new HashMap<String, InfusedBlock>();
    private static HashMap<String, Integer> blockMethods = new HashMap<String, Integer>();

    public static void addBlock(String key, InfusedBlock block){
        infusedBlocks.put(key.toLowerCase(), block);
    }

    public static InfusedBlock getBlock(String key){
        return infusedBlocks.get(key.toLowerCase());
    }

    public static Method getMethod(String methName, Class[] pars) {
        Method[] methods = Block.class.getDeclaredMethods();
        for (Method meth : methods)
            if (meth.getName().matches(methName) && meth.getParameterTypes().length == pars.length) return meth;
        return null;
    }

    public static void whitelistBlocks(){
        Iterator blocksIter = Block.blockRegistry.iterator();
        while(blocksIter.hasNext()){
            Class block = blocksIter.next().getClass();
        }
    }

    public static void phaseBlock(){
        MethodAccess methodAccess = MethodAccess.get(Block.class);
        String[] methods = methodAccess.getMethodNames();
        for(String name : methods){
            blockMethods.put(name, methodAccess.getIndex(name));
        }
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

    public static void init() {
        phaseBlock();
    }
}
