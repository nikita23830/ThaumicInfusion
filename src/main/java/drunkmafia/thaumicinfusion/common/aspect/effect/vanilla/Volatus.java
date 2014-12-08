package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * Created by DrunkMafia on 12/11/2014.
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = "Volatus", cost = 4)
public class Volatus extends AspectEffect {
    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fall) {
        if(world.isRemote)
            return;
        AddMotion(entity);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if(world.isRemote)
            return;
        AddMotion(entity);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if(world.isRemote)
            return;
        AddMotion(entity);
    }

    void AddMotion(Entity ent){
        if(!(ent instanceof EntityLivingBase))
            return;
        ent.addVelocity(0, ent.worldObj.rand.nextInt(20), 0);
    }
}
