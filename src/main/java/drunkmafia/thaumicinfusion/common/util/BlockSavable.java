package drunkmafia.thaumicinfusion.common.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

/**
 * Created by DrunkMafia on 29/06/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class BlockSavable extends Savable {

    private ChunkCoordinates coordinates;

    public BlockSavable() {
    }

    public BlockSavable(ChunkCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ChunkCoordinates getCoords() {
        return coordinates;
    }

    public void writeNBT(NBTTagCompound tagCompound) {
        super.writeNBT(tagCompound);
        tagCompound.setIntArray("coords", new int[]{coordinates.posX, coordinates.posY, coordinates.posZ});
    }

    public void readNBT(NBTTagCompound tagCompound) {
        super.readNBT(tagCompound);
        int[] coords = tagCompound.getIntArray("coords");
        coordinates = new ChunkCoordinates(coords[0], coords[1], coords[2]);
    }
}
