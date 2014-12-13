package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import com.mojang.authlib.GameProfile;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.lib.FakeThaumcraftPlayer;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("humanus"), cost = 4)
public class Humanus extends AspectEffect {
    @Override
    public boolean shouldRegister() {
        return false;
    }
}
