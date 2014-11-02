package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.tileentity.EffectTile;
import net.minecraftforge.fluids.*;

/**
 * Created by DrunkMafia on 09/10/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
public class AquaTile extends EffectTile implements IFluidTank {

    public FluidTank tank;

    public AquaTile() {
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 8);
    }

    @Override
    public void updateEntity() {
        System.out.println(worldObj.isRemote);
    }

    @Override
    public FluidStack getFluid() {
        return tank.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return tank.getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return tank.getInfo();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }
}
