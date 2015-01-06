package drunkmafia.thaumicinfusion.common.block;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drunkmafia.thaumicinfusion.common.ThaumicInfusion;
import drunkmafia.thaumicinfusion.common.aspect.AspectEffect;
import drunkmafia.thaumicinfusion.common.lib.BlockInfo;
import drunkmafia.thaumicinfusion.common.util.BlockHelper;
import drunkmafia.thaumicinfusion.common.util.InfusionHelper;
import drunkmafia.thaumicinfusion.common.util.WorldCoord;
import drunkmafia.thaumicinfusion.common.world.BlockData;
import drunkmafia.thaumicinfusion.common.world.BlockSavable;
import drunkmafia.thaumicinfusion.net.ChannelHandler;
import drunkmafia.thaumicinfusion.net.packet.client.DestroyBlockPacketS;
import drunkmafia.thaumicinfusion.net.packet.client.RequestBlockPacketS;
import drunkmafia.thaumicinfusion.net.packet.server.PlaySoundPacketC;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.util.List;
import java.util.Random;

public class InfusedBlock extends WorldBlockData implements ITileEntityProvider, IInfusionStabiliser {

    /**
     * =================================================
     * ===== Start Of Generic Block Functionality ======
     * =================================================
     */

    public static int renderType = -1;
    public int pass = 0;

    public InfusedBlock(Material mat) {
        super(mat);
        this.setTickRandomly(true);
        this.setStepSound(new SoundType("stone", -10, 1F));
    }

    public InfusedBlock setPass(int pass) {
        this.pass = pass;
        return this;
    }

    public InfusedBlock setSlipperiness(float slipperiness) {
        this.slipperiness = slipperiness;
        return this;
    }

    public boolean isBlockData(BlockSavable savable) {
        if (savable != null && savable instanceof BlockData) return true;
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(BlockInfo.infusedBlock_BlankTexture);
    }

    @Override
    protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack stack) {}

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        WorldCoord pos = new WorldCoord(x, y, z);
        BlockData block = BlockHelper.getData(BlockData.class, world, pos);
        return InfusionHelper.getInfusedItemStack(block.getAspects(), new ItemStack(block.getContainingBlock()), 1, world.getBlockMetadata(pos.x, pos.y, pos.z));
    }

    @Override
    public boolean shouldUsePlaceEvent() {
        return false;
    }

    @Override
    public BlockSavable getData(World world, ItemStack stack, WorldCoord coord) {
        coord.dim = world.provider.dimensionId;
        BlockData data = BlockHelper.getDataFromStack(stack, coord.x, coord.y, coord.z);
        data.dataLoad(world);
        return data;
    }

    @Override
    public void breakBlock(World world, BlockSavable data, int meta) {
        if (data == null || !(data instanceof BlockData))
            return;

        BlockData block = (BlockData) data;
        WorldCoord pos = data.getCoords();

        try {
            block.runBlockMethod().breakBlock(world, pos.x, pos.y, pos.z, block.getContainingBlock(), world.getBlockMetadata(pos.x, pos.y, pos.z));
        } catch (Exception e) {}

        SoundType type = block.getContainingBlock().stepSound;
        ChannelHandler.network.sendToAllAround(new PlaySoundPacketC(pos.x, pos.y, pos.z, type.getBreakSound(), (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F), new NetworkRegistry.TargetPoint(world.provider.dimensionId, pos.x, pos.y, pos.z, 10));

        ItemStack stack = InfusionHelper.getInfusedItemStack(block.getAspects(), new ItemStack(block.getContainingBlock()), 1, meta);
        if (stack != null)
            super.dropBlockAsItem(world, pos.x, pos.y, pos.z, stack);
    }

    @Override
    public boolean canReplace(World world, int x, int y, int z, int side, ItemStack stack) {
        return Block.getBlockById(InfusionHelper.getInfusedID(stack)).canReplace(world, x, y, z, side, stack);
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return pass;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int tickRate(World world) {
        return 5;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(target.blockX, target.blockY, target.blockZ));
        if (isBlockData(blockData)) {
            try {
                {
                    Block block = blockData.getContainingBlock();
                    if (block.getMaterial() != Material.air) {
                        Random rand = new Random();

                        float space = 0.1F;
                        double x = (double) target.blockX + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (space * 2.0F)) + (double) space + block.getBlockBoundsMinX();
                        double y = (double) target.blockY + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (space * 2.0F)) + (double) space + block.getBlockBoundsMinY();
                        double z = (double) target.blockZ + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (space * 2.0F)) + (double) space + block.getBlockBoundsMinZ();

                        if (target.sideHit == 0)
                            y = (double) target.blockY + block.getBlockBoundsMinY() - (double) space;
                        if (target.sideHit == 1)
                            y = (double) target.blockY + block.getBlockBoundsMaxY() + (double) space;
                        if (target.sideHit == 2)
                            z = (double) target.blockZ + block.getBlockBoundsMinZ() - (double) space;
                        if (target.sideHit == 3)
                            z = (double) target.blockZ + block.getBlockBoundsMaxZ() + (double) space;
                        if (target.sideHit == 4)
                            x = (double) target.blockX + block.getBlockBoundsMinX() - (double) space;
                        if (target.sideHit == 5)
                            x = (double) target.blockX + block.getBlockBoundsMaxX() + (double) space;

                        effectRenderer.addEffect((new EntityDiggingFX(world, x, y, z, 0.0D, 0.0D, 0.0D, block, world.getBlockMetadata(target.blockX, target.blockY, target.blockZ))).applyColourMultiplier(target.blockX, target.blockY, target.blockZ).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
                    }
                }
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return true;
    }

    /**
     * =================================================
     * ===== End Of Generic Block Functionality ========
     * =================================================
     * =================================================
     * ===== Start Of Infused Block Functionality ======
     * =================================================
     */

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().getSelectedBoundingBoxFromPool(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bb, List list, Entity entity) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().addCollisionBoxesToList(world, x, y, z, bb, list, entity);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 pos, Vec3 dir) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().collisionRayTrace(world, x, y, z, pos, dir);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return super.collisionRayTrace(world, x, y, z, pos, dir);
    }

    @Override
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().getIcon(access, x, y, z, side);

        return blockIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase ent, ItemStack stack) {
        if (world.isRemote)
            RequestBlockPacketS.syncTimeouts.remove(new WorldCoord(x, y, z));

        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onBlockPlacedBy(world, x, y, z, ent, stack);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onPostBlockPlaced(world, x, y, z, meta);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().updateTick(world, x, y, z, rand);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                effectRenderer.addBlockDestroyEffects(x, y, z, blockData.getContainingBlock(), meta);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            ItemStack stack = player.getHeldItem();
            if (world.isRemote && stack != null && stack.getItem() instanceof ItemWandCasting && blockData.canOpenGUI()) {
                player.openGui(ThaumicInfusion.instance, 0, world, x, y, z);
                return true;
            }
            try {
                return blockData.runBlockMethod().onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().getBlocksMovement(access, x, y, z);

        return super.getBlocksMovement(access, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onBlockAdded(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            Block block = blockData.runBlockMethod();
            block.setBlockBoundsBasedOnState(access, x, y, z);
            this.setBlockBounds((float) block.getBlockBoundsMinX(), (float) block.getBlockBoundsMinY(), (float) block.getBlockBoundsMinZ(), (float) block.getBlockBoundsMaxX(), (float) block.getBlockBoundsMaxY(), (float) block.getBlockBoundsMaxZ());
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().randomDisplayTick(world, x, y, z, rand);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.getContainingBlock().onNeighborBlockChange(world, x, y, z, block);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity ent, float fall) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onFallenUpon(world, x, y, z, ent, fall);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity ent) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onEntityWalking(world, x, y, z, ent);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onEntityCollidedWithBlock(world, x, y, z, ent);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer ent) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                blockData.runBlockMethod().onBlockClicked(world, x, y, z, ent);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int meta) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isProvidingWeakPower(access, x, y, z, meta);

        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess access, int x, int y, int z, int meta) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isProvidingStrongPower(access, x, y, z, meta);
        return 0;
    }

    @Override
    public int getLightValue(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                Block effect = blockData.runAspectMethod();
                return effect != null ? effect.getLightValue(access, x, y, z) : 0;
            }catch (Exception e){
                handleError(e, BlockHelper.getWorld(blockData.getCoords(), access), blockData, true);
            }
        }
        return 0;
    }

    @Override
    public int getLightOpacity(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().getLightOpacity(access, x, y, z);
            }catch (Exception e){
                handleError(e, BlockHelper.getWorld(blockData.getCoords(), access), blockData, true);
            }
        }
        return getLightOpacity();
    }

    @Override
    public int getFlammability(IBlockAccess access, int x, int y, int z, ForgeDirection face) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().getFlammability(access, x, y, z, face);
            }catch (Exception e){
                handleError(e, BlockHelper.getWorld(blockData.getCoords(), access), blockData, true);
            }
        }
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess access, int x, int y, int z, ForgeDirection face) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().getFireSpreadSpeed(access, x, y, z, face);
            }catch (Exception e){
                handleError(e, BlockHelper.getWorld(blockData.getCoords(), access), blockData, true);
            }
        }

        return 0;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().getValidRotations(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return null;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().getPlayerRelativeBlockHardness(player, world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return 0;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().getComparatorInputOverride(world, x, y, z, side);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        AxisAlignedBB bb = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().getCollisionBoundingBoxFromPool(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return bb;
    }

    @Override
    public float getExplosionResistance(Entity ent, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().getExplosionResistance(ent, world, x, y, z, explosionX, explosionY, explosionZ);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return 0;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().getBlockHardness(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return 0;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess access, int x, int y, int z, int side) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().shouldCheckWeakPower(access, x, y, z, side);
        return false;
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().getEnchantPowerBonus(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
        return !(access.getBlock(x, y, z) instanceof InfusedBlock);
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().rotateBlock(world, x, y, z, axis);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().recolourBlock(world, x, y, z, side, colour);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().isWood(world, x, y, z);
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess access, int x, int y, int z, ForgeDirection side) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().isSideSolid(access, x, y, z, side);
        return true;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess access, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isBeaconBase(access, x, y, z, beaconX, beaconY, beaconZ);
        return false;
    }

    @Override
    public boolean isBed(IBlockAccess access, int x, int y, int z, EntityLivingBase player) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isBed(access, x, y, z, player);
        return false;
    }

    @Override
    public boolean isBedFoot(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isBedFoot(access, x, y, z);
        return false;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess access, int x, int y, int z, int meta) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().isBlockSolid(access, x, y, z, meta);
        return true;
    }

    @Override
    public boolean isBurning(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isBurning(access, x, y, z);
        return false;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().isFertile(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.runBlockMethod().isFireSource(world, x, y, z, side);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public boolean isFlammable(IBlockAccess access, int x, int y, int z, ForgeDirection face) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isFlammable(access, x, y, z, face);
        return false;
    }

    @Override
    public boolean isFoliage(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isFoliage(access, x, y, z);
        return false;
    }

    @Override
    public boolean isLadder(IBlockAccess access, int x, int y, int z, EntityLivingBase entity) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isLadder(access, x, y, z, entity);
        return false;
    }

    @Override
    public boolean isLeaves(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().isLeaves(access, x, y, z);
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().isNormalCube(access, x, y, z);
        return false;
    }

    @Override
    public boolean isReplaceable(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().isReplaceable(access, x, y, z);
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().canPlaceTorchOnTop(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public boolean canSustainPlant(IBlockAccess access, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().canSustainPlant(access, x, y, z, direction, plantable);
        return false;
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().canSustainLeaves(access, x, y, z);
        return false;
    }

    @Override
    public boolean getWeakChanges(IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().getWeakChanges(access, x, y, z);
        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess access, int x, int y, int z, int side) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().canConnectRedstone(access, x, y, z, side);
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess access, int x, int y, int z, Entity entity) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.getContainingBlock().canEntityDestroy(access, x, y, z, entity);
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess access, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, access, new WorldCoord(x, y, z));
        if (isBlockData(blockData))
            return blockData.runBlockMethod().canCreatureSpawn(type, access, x, y, z);
        return false;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            try {
                return blockData.getContainingBlock().canBlockStay(world, x, y, z);
            } catch (Exception e) {
                handleError(e, world, blockData, true);
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
    }

    @Override
    public boolean canStabaliseInfusion(World world, int x, int y, int z) {
        BlockData blockData = BlockHelper.getData(BlockData.class, world, new WorldCoord(x, y, z));
        if (isBlockData(blockData)) {
            Block block = blockData.getContainingBlock();

            for (AspectEffect effect : blockData.getEffects()) {
                if (effect instanceof IInfusionStabiliser) {
                    block = effect;
                    break;
                }
            }

            if (block instanceof IInfusionStabiliser) {
                try {
                    return ((IInfusionStabiliser) block).canStabaliseInfusion(world, x, y, z);
                } catch (Exception e) {
                    handleError(e, world, blockData, true);
                }
            }
        }
        return false;
    }

    /**
     * ===============================================
     * ===== End Of Infused Block Functionality ======
     * ===============================================
     */

    /**
     * ==================================================
     * ===== Start Of Infused Block Error Handling ======
     * ==================================================
     */

    public static void handleError(Exception e, TileEntity entity){
        handleError(e, entity.getWorldObj(), BlockHelper.getData(BlockData.class, entity.getWorldObj(), WorldCoord.get(entity.xCoord, entity.yCoord, entity.zCoord)), true);
    }

    public static void handleError(Exception e, World world, BlockData data, boolean shouldDestroy) {
        String methName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Logger logger = ThaumicInfusion.getLogger();
        logger.error("Block at: " + data.getCoords().toString() + " threw error while running: " + methName + " in block: " + data.getContainingBlock().getLocalizedName() + " it is advised that this block is added to the blacklist in the config.", e);

        if (shouldDestroy) {
            logger.info("Block has been destroyed, to prevent this error from happening again");
            if(!world.isRemote)
                BlockHelper.destroyBlock(world, data.getCoords());
            else
                ChannelHandler.network.sendToServer(new DestroyBlockPacketS(data.getCoords()));
        }
    }

    /**
     * ================================================
     * ===== End Of Infused Block Error Handling ======
     * ================================================
     */
}