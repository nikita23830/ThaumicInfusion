package drunkmafia.thaumicinfusion.common.block;

import com.esotericsoftware.reflectasm.MethodAccess;
import net.minecraft.block.Block;

import java.lang.reflect.Method;
import java.util.HashMap;

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

    public static void phaseBlock(){
        MethodAccess methodAccess = MethodAccess.get(Block.class);
        String[] methods = methodAccess.getMethodNames();
        for(String name : methods){
            blockMethods.put(name, methodAccess.getIndex(name));
        }
    }

    public static int getMethod(String methName){
        return blockMethods.get(methName);
    }

    public static void init() {
        phaseBlock();
    }
}
