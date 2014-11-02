package drunkmafia.thaumicinfusion.common.util;

/**
 * Created by DrunkMafia on 31/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class MathHelper {

    public static float clamp(float val, float maxClamp, float minClamp){
        return Math.max(minClamp, Math.min(val, maxClamp));
    }

    public static float lerp(float to, float from, float f){
        float ret = to + f * (int)clamp(from - to, 1, -1);
        if(withinThreshold(from, ret, 1))
            return from;
        return ret;
    }

    public static boolean withinThreshold(float a, float b, float threshold){
        return Math.abs(a - b) < threshold;
    }
}
