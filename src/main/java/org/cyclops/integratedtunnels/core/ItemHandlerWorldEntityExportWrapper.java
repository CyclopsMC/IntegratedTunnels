package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Iterators;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integratedtunnels.GeneralConfig;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An item storage for exporting item entities to the world.
 * @author rubensworks
 */
public class ItemHandlerWorldEntityExportWrapper implements IIngredientComponentStorage<ItemStack, Integer>, IBlockSource {

    private final WorldServer world;
    private final BlockPos pos;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final int lifespan;
    private final int delayBeforePickup;
    private final EnumFacing facing;
    private final double velocity;
    private final float yawOffset;
    private final float pitchOffset;
    private final boolean dispense;

    private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;

    private static final BehaviorDefaultDispenseItem DISPENSE_ITEM_DIRECTLY = new BehaviorDefaultDispenseItem();

    public ItemHandlerWorldEntityExportWrapper(WorldServer world, BlockPos pos,
                                               double offsetX, double offsetY, double offsetZ,
                                               int lifespan, int delayBeforePickup,
                                               EnumFacing facing, double velocity,
                                               double yawOffset, double pitchOffset,
                                               boolean dispense, IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler) {
        this.world = world;
        this.pos = pos;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.lifespan = lifespan;
        this.delayBeforePickup = delayBeforePickup;
        this.facing = facing;
        this.velocity = velocity;
        this.yawOffset = (float) yawOffset;
        this.pitchOffset = (float) pitchOffset;
        this.dispense = dispense;
        this.dispenseResultHandler = dispenseResultHandler;
    }

    protected void setThrowableHeading(EntityItem entity, double x, double y, double z, double velocity) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x * velocity;
        y = y * velocity;
        z = z * velocity;
        entity.motionX = x;
        entity.motionY = y;
        entity.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        entity.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        entity.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        entity.prevRotationYaw = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;
    }

    protected static void handleDispenseResult(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler,
                                               IBlockSource blockSource, ItemStack itemStack) {
        ItemStack remaining = dispenseResultHandler.insert(itemStack, false);
        if (!remaining.isEmpty()) {
            DISPENSE_ITEM_DIRECTLY.dispense(blockSource, remaining);
        }
    }

    @Override
    public double getX() {
        return getBlockPos().getX() + offsetX;
    }

    @Override
    public double getY() {
        return getBlockPos().getY() + offsetY;
    }

    @Override
    public double getZ() {
        return getBlockPos().getZ() + offsetZ;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.pos.offset(this.facing.getOpposite());
    }

    @Override
    public IBlockState getBlockState() {
        return Blocks.DISPENSER.getDefaultState()
                .withProperty(BlockDispenser.TRIGGERED, false)
                .withProperty(BlockDispenser.FACING, this.facing);
    }

    @Override
    public TileEntityDispenser getBlockTileEntity() {
        return new SimulatedTileEntityDispenser(dispenseResultHandler, this);
    }

    @Override
    public World getWorld() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.forArray();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return iterator();
    }

    @Override
    public long getMaxQuantity() {
        return 64;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        if (!simulate) {
            if (this.dispense) {
                IBehaviorDispenseItem behaviorDispenseItem = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(stack.getItem());
                if (behaviorDispenseItem.getClass() != BehaviorDefaultDispenseItem.class) {
                    ItemStack result = behaviorDispenseItem.dispense(this, stack.copy());
                    if (!result.isEmpty()) {
                        handleDispenseResult(this.dispenseResultHandler, this, result);
                    }
                    return ItemStack.EMPTY;
                }
            }
            EntityItem entity = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack.copy());
            entity.lifespan = lifespan <= 0 ? stack.getItem().getEntityLifespan(stack, world) : lifespan;
            float yaw = facing.getHorizontalAngle() + yawOffset;
            float pitch = (facing == EnumFacing.UP ? -90F : (facing == EnumFacing.DOWN ? 90F : 0)) - pitchOffset;
            this.setThrowableHeading(entity,
                    -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F),
                    -MathHelper.sin((pitch) * 0.017453292F),
                    MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F),
                    this.velocity);
            entity.setPickupDelay(delayBeforePickup);
            world.spawnEntity(entity);

            if (GeneralConfig.worldInteractionEvents) {
                world.playEvent(1000, pos, 0); // Sound
                world.playEvent(2000, pos.offset(facing.getOpposite()), facing.getFrontOffsetX() + 1 + (facing.getFrontOffsetZ() + 1) * 3); // Particles
            }
        } else if (this.dispense) {
            stack = stack.copy();
            stack.splitStack(1);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        return ItemStack.EMPTY;
    }

    protected static class SimulatedTileEntityDispenser extends TileEntityDispenser {

        private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;
        private final IBlockSource blockSource;

        public SimulatedTileEntityDispenser(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler, IBlockSource blockSource) {
            this.dispenseResultHandler = dispenseResultHandler;
            this.blockSource = blockSource;
        }

        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public int getDispenseSlot() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int addItemStack(ItemStack stack) {
            handleDispenseResult(this.dispenseResultHandler, this.blockSource, stack);
            return 0;
        }

        @Override
        protected NonNullList<ItemStack> getItems() {
            return NonNullList.create();
        }
    }
}
