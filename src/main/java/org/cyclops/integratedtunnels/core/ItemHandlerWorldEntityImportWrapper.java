package org.cyclops.integratedtunnels.core;

import com.google.common.base.Predicate;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An item handler for importing item entities from the world.
 * @author rubensworks
 */
public class ItemHandlerWorldEntityImportWrapper implements IItemHandler {

    private final List<EntityItem> entities;

    public ItemHandlerWorldEntityImportWrapper(WorldServer world, BlockPos pos, final boolean ignorePickupDelay) {
        this(world, new AxisAlignedBB(pos), ignorePickupDelay);
    }

    public ItemHandlerWorldEntityImportWrapper(WorldServer world, AxisAlignedBB area, final boolean ignorePickupDelay) {
        this.entities = world.getEntitiesWithinAABB(EntityItem.class, area, new Predicate<EntityItem>() {
            @Override
            public boolean apply(EntityItem input) {
                return (ignorePickupDelay || !input.cannotPickup()) && !input.isDead;
            }
        });
    }

    @Override
    public int getSlots() {
        return entities.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot < this.entities.size() ? this.entities.get(slot).getEntityItem() : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot >= this.entities.size()) {
            return ItemStack.EMPTY;
        }

        EntityItem entity = this.entities.get(slot);
        ItemStack itemStack = entity.getEntityItem();
        itemStack = itemStack.copy();
        ItemStack ret = itemStack.splitStack(amount);
        if (!simulate) {
            if (itemStack.isEmpty()) {
                entity.setDead();
            } else {
                entity.setEntityItemStack(itemStack);
            }
        }

        return ret;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
