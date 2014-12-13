package drunkmafia.thaumicinfusion.common.aspect.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class VolatusTile extends TileEntity {

    boolean isFlying;

    @Override
    @SideOnly(Side.CLIENT)
    public void updateEntity() {
        if(!worldObj.isRemote)
            return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(player == null || player.capabilities.isCreativeMode)
            return;

        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 10, zCoord + 1);
        List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

        if(list.contains(player)) {
            isFlying = true;
            player.capabilities.isFlying = true;
        }else if(isFlying) {
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
            isFlying = false;
        }
    }
}
