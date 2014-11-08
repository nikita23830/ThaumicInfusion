package drunkmafia.thaumicinfusion.common.aspect;

import drunkmafia.thaumicinfusion.common.util.Savable;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 05/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class AspectEffect extends Savable {

    protected ChunkCoordinates pos;
    protected World worldObj;

    public void aspectInit(World world, ChunkCoordinates pos){
        worldObj = world;
        this.pos = pos;
    }
}
