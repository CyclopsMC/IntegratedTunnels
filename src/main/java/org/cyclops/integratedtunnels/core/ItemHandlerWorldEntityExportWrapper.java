package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Iterators;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.BlockState;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    private final ServerWorld world;
    private final BlockPos pos;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final int lifespan;
    private final int delayBeforePickup;
    private final Direction facing;
    private final double velocity;
    private final float yawOffset;
    private final float pitchOffset;
    private final boolean dispense;

    private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;

    private static final DefaultDispenseItemBehavior DISPENSE_ITEM_DIRECTLY = new DefaultDispenseItemBehavior();

    public ItemHandlerWorldEntityExportWrapper(ServerWorld world, BlockPos pos,
                                               double offsetX, double offsetY, double offsetZ,
                                               int lifespan, int delayBeforePickup,
                                               Direction facing, double velocity,
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

    protected void setThrowableHeading(ItemEntity entity, double x, double y, double z, double velocity) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x * velocity;
        y = y * velocity;
        z = z * velocity;
        entity.setDeltaMovement(new Vector3d(x, y, z));
        float f1 = MathHelper.sqrt(x * x + z * z);
        entity.yRot = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        entity.xRot = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        entity.yRotO = entity.yRot;
        entity.xRotO = entity.xRot;
    }

    protected static void handleDispenseResult(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler,
                                               IBlockSource blockSource, ItemStack itemStack) {
        ItemStack remaining = dispenseResultHandler.insert(itemStack, false);
        if (!remaining.isEmpty()) {
            DISPENSE_ITEM_DIRECTLY.dispense(blockSource, remaining);
        }
    }

    @Override
    public double x() {
        return getPos().getX() + offsetX;
    }

    @Override
    public double y() {
        return getPos().getY() + offsetY;
    }

    @Override
    public double z() {
        return getPos().getZ() + offsetZ;
    }

    @Override
    public BlockPos getPos() {
        return this.pos.relative(this.facing.getOpposite());
    }

    @Override
    public BlockState getBlockState() {
        return Blocks.DISPENSER.defaultBlockState()
                .setValue(DispenserBlock.TRIGGERED, false)
                .setValue(DispenserBlock.FACING, this.facing);
    }

    @Override
    public DispenserTileEntity getEntity() {
        return new SimulatedTileEntityDispenser(dispenseResultHandler, this);
    }

    @Override
    public ServerWorld getLevel() {
        return world;
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
                IDispenseItemBehavior behaviorDispenseItem = DispenserBlock.DISPENSER_REGISTRY.get(stack.getItem());
                if (behaviorDispenseItem.getClass() != DefaultDispenseItemBehavior.class) {
                    ItemStack result = behaviorDispenseItem.dispense(this, stack.copy());
                    if (!result.isEmpty()) {
                        handleDispenseResult(this.dispenseResultHandler, this, result);
                    }
                    return ItemStack.EMPTY;
                }
            }
            ItemEntity entity = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack.copy());
            entity.lifespan = lifespan <= 0 ? stack.getItem().getEntityLifespan(stack, world) : lifespan;
            float yaw = facing.toYRot() + yawOffset;
            float pitch = (facing == Direction.UP ? -90F : (facing == Direction.DOWN ? 90F : 0)) - pitchOffset;
            this.setThrowableHeading(entity,
                    -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F),
                    -MathHelper.sin((pitch) * 0.017453292F),
                    MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F),
                    this.velocity);
            entity.setPickUpDelay(delayBeforePickup);
            world.addFreshEntity(entity);

            if (GeneralConfig.worldInteractionEvents) {
                world.levelEvent(1000, pos, 0); // Sound
                world.levelEvent(2000, pos.relative(facing.getOpposite()), facing.get3DDataValue()); // Particles
            }
        } else if (this.dispense) {
            stack = stack.copy();
            stack.split(1);
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

    protected static class SimulatedTileEntityDispenser extends DispenserTileEntity {

        private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;
        private final IBlockSource blockSource;

        public SimulatedTileEntityDispenser(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler, IBlockSource blockSource) {
            this.dispenseResultHandler = dispenseResultHandler;
            this.blockSource = blockSource;
        }

        @Override
        public int getContainerSize() {
            return 0;
        }

        @Override
        public int getRandomSlot() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int addItem(ItemStack stack) {
            handleDispenseResult(this.dispenseResultHandler, this.blockSource, stack);
            return 0;
        }

        @Override
        protected NonNullList<ItemStack> getItems() {
            return NonNullList.create();
        }
    }
}
