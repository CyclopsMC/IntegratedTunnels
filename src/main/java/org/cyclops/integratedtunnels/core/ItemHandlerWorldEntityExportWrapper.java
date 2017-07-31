package org.cyclops.integratedtunnels.core;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * An item handler for exporting item entities to the world.
 * @author rubensworks
 */
public class ItemHandlerWorldEntityExportWrapper implements IItemHandler {

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

    public ItemHandlerWorldEntityExportWrapper(WorldServer world, BlockPos pos,
                                               double offsetX, double offsetY, double offsetZ,
                                               int lifespan, int delayBeforePickup,
                                               EnumFacing facing, double velocity,
                                               double yawOffset, double pitchOffset) {
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
        }
        return ItemStack.EMPTY;
    }

    protected void setThrowableHeading(EntityItem entity, double x, double y, double z, double velocity) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.world.rand.nextGaussian() * 0.0075D;
        y = y + this.world.rand.nextGaussian() * 0.0075D;
        z = z + this.world.rand.nextGaussian() * 0.0075D;
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
}
