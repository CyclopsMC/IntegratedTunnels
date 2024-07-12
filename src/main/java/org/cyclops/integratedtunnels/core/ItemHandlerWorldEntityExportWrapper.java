package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Iterators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.datastructure.Wrapper;
import org.cyclops.integratedtunnels.GeneralConfig;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * An item storage for exporting item entities to the world.
 * @author rubensworks
 */
public class ItemHandlerWorldEntityExportWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final ServerLevel world;
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

    public ItemHandlerWorldEntityExportWrapper(ServerLevel world, BlockPos pos,
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
        float f = Mth.sqrt((float) (x * x + y * y + z * z));
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x * velocity;
        y = y * velocity;
        z = z * velocity;
        entity.setDeltaMovement(new Vec3(x, y, z));
        float f1 = Mth.sqrt((float) (x * x + z * z));
        entity.yRotO = (float)(Mth.atan2(x, z) * (180D / Math.PI));
        entity.xRotO = (float)(Mth.atan2(y, (double)f1) * (180D / Math.PI));
        entity.yRotO = entity.yRotO;
        entity.xRotO = entity.xRotO;
    }

    protected static void handleDispenseResult(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler,
                                               BlockSource blockSource, ItemStack itemStack) {
        ItemStack remaining = dispenseResultHandler.insert(itemStack, false);
        if (!remaining.isEmpty()) {
            DISPENSE_ITEM_DIRECTLY.dispense(blockSource, remaining);
        }
    }

    public BlockSource getBlockSource() {
        Wrapper<BlockSource> blockSourceWrapper = new Wrapper<>();
        blockSourceWrapper.set(new BlockSource(world, getPos().offset((int) offsetX, (int) offsetY, (int) offsetZ), getBlockState(), new SimulatedTileEntityDispenser(dispenseResultHandler, blockSourceWrapper::get)));
        return blockSourceWrapper.get();
    }

    public BlockPos getPos() {
        return this.pos.relative(this.facing.getOpposite());
    }

    public BlockState getBlockState() {
        return Blocks.DISPENSER.defaultBlockState()
                .setValue(DispenserBlock.TRIGGERED, false)
                .setValue(DispenserBlock.FACING, this.facing);
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
                DispenseItemBehavior behaviorDispenseItem = DispenserBlock.DISPENSER_REGISTRY.get(stack.getItem());
                if (behaviorDispenseItem.getClass() != DefaultDispenseItemBehavior.class) {
                    BlockSource blockSource = getBlockSource();
                    ItemStack result = behaviorDispenseItem.dispense(blockSource, stack.copy());
                    if (!result.isEmpty()) {
                        handleDispenseResult(this.dispenseResultHandler, blockSource, result);
                    }
                    return ItemStack.EMPTY;
                }
            }
            ItemEntity entity = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, stack.copy());
            entity.lifespan = lifespan <= 0 ? stack.getItem().getEntityLifespan(stack, world) : lifespan;
            float yaw = facing.toYRot() + yawOffset;
            float pitch = (facing == Direction.UP ? -90F : (facing == Direction.DOWN ? 90F : 0)) - pitchOffset;
            this.setThrowableHeading(entity,
                    -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F),
                    -Mth.sin((pitch) * 0.017453292F),
                    Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F),
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

    protected static class SimulatedTileEntityDispenser extends DispenserBlockEntity {

        private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;
        private final Supplier<BlockSource> blockSource;

        public SimulatedTileEntityDispenser(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler, Supplier<BlockSource> blockSource) {
            super(BlockPos.ZERO, Blocks.DISPENSER.defaultBlockState());
            this.dispenseResultHandler = dispenseResultHandler;
            this.blockSource = blockSource;
        }

        @Override
        public int getContainerSize() {
            return 0;
        }

        @Override
        public int getRandomSlot(RandomSource randomSource) {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ItemStack insertItem(ItemStack stack) {
            handleDispenseResult(this.dispenseResultHandler, this.blockSource.get(), stack);
            return ItemStack.EMPTY;
        }

        @Override
        protected NonNullList<ItemStack> getItems() {
            return NonNullList.create();
        }
    }
}
