package org.cyclops.integratedtunnels.core;

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
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;

import javax.annotation.Nonnull;

/**
 * An item handler for exporting item entities to the world.
 * @author rubensworks
 */
public class ItemHandlerWorldEntityExportWrapper implements IItemHandler, IBlockSource {

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

    private final ISlotlessItemHandler dispenseResultHandler;

    private static final BehaviorDefaultDispenseItem DISPENSE_ITEM_DIRECTLY = new BehaviorDefaultDispenseItem();

    public ItemHandlerWorldEntityExportWrapper(WorldServer world, BlockPos pos,
                                               double offsetX, double offsetY, double offsetZ,
                                               int lifespan, int delayBeforePickup,
                                               EnumFacing facing, double velocity,
                                               double yawOffset, double pitchOffset,
                                               boolean dispense, ISlotlessItemHandler dispenseResultHandler) {
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

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
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
        } else if (this.dispense) {
            stack = stack.copy();
            stack.splitStack(1);
            return stack;
        }
        return ItemStack.EMPTY;
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

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected static void handleDispenseResult(ISlotlessItemHandler dispenseResultHandler,
                                               IBlockSource blockSource, ItemStack itemStack) {
        ItemStack remaining = dispenseResultHandler.insertItem(itemStack, false);
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

    protected static class SimulatedTileEntityDispenser extends TileEntityDispenser {

        private final ISlotlessItemHandler dispenseResultHandler;
        private final IBlockSource blockSource;

        public SimulatedTileEntityDispenser(ISlotlessItemHandler dispenseResultHandler, IBlockSource blockSource) {
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
