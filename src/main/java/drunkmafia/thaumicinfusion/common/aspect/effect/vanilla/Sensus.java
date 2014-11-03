package drunkmafia.thaumicinfusion.common.aspect.effect.vanilla;

import drunkmafia.thaumicinfusion.common.util.BlockData;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.Savable;
import drunkmafia.thaumicinfusion.common.util.annotation.Effect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.items.armor.ItemGoggles;

import java.util.Random;

/**
 * Created by DrunkMafia on 25/07/2014.
 * <p/>
 * See http://www.wtfpl.net/txt/copying for licence
 */
@Effect(aspect = ("sensus"), cost = 3)
public class Sensus extends Savable {

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase ent, ItemStack stack) {
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), 10);
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
        System.out.println("sensus");
        world.markBlockForUpdate(x, y, z);
    }

    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
        ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.armorInventory[3];
        System.out.println("Sensus " + stack != null);
        return stack != null && stack.getItem() instanceof ItemGoggles;
    }
}
