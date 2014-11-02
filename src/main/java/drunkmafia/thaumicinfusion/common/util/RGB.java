package drunkmafia.thaumicinfusion.common.util;

import java.util.Random;

/**
 * Created by DrunkMafia on 19/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class RGB {

    private float r, g, b;

    public RGB(){
        Random rand = new Random();
        r = rand.nextFloat();
        g = rand.nextFloat();
        b = rand.nextFloat();
    }

    public RGB(float r, float g, float b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGB(int rgb){
        r = (float)(rgb >> 16 & 255) / 255.0F;
        g = (float)(rgb >> 8 & 255) / 255.0F;
        b = (float)(rgb & 255) / 255.0F;
    }

    public float getB() {
        return b;
    }

    public float getG() {
        return g;
    }

    public float getR() {
        return r;
    }
}
