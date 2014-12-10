package drunkmafia.thaumicinfusion.common.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.WorldCoordinates;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class WorldCoord extends WorldCoordinates {

    public String id = "W";

    public static WorldCoord get(int x, int y, int z){
        return new WorldCoord(x, y, z);
    }

    public WorldCoord(){}

    public WorldCoord(String id, int x, int y, int z){
        super(x, y, z, 0);
        this.id = id;
    }

    public WorldCoord(int x, int y, int z){
        super(x, y, z, 0);
    }

    public void fromBytes(ByteBuf buf) {
        try {
            NBTTagCompound tag = new PacketBuffer(buf).readNBTTagCompoundFromBuffer();
            if (tag != null)
                readNBT(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toBytes(ByteBuf buf) {
        try {
            NBTTagCompound tag = new NBTTagCompound();
            writeNBT(tag);
            new PacketBuffer(buf).writeNBTTagCompoundToBuffer(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readNBT(NBTTagCompound nbt) {
        this.id = nbt.getString("id");
        this.x = nbt.getInteger(id + "_x");
        this.y = nbt.getInteger(id + "_y");
        this.z = nbt.getInteger(id + "_z");
        this.dim = nbt.getInteger(id + "_d");
    }

    @Override
    public void writeNBT(NBTTagCompound nbt) {
        nbt.setString("id", id);
        nbt.setInteger(id + "_x",x);
        nbt.setInteger(id + "_y",y);
        nbt.setInteger(id + "_z",z);
        nbt.setInteger(id + "_d", dim);
    }

    public void removeFromNBT(NBTTagCompound nbt){
        if(!nbt.hasKey("id"))
            return;
        System.out.println("Removing");
        nbt.removeTag("id");
        nbt.removeTag(id + "_x");
        nbt.removeTag(id + "_Y");
        nbt.removeTag(id + "_Z");
    }
}
