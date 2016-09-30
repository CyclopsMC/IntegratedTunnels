package org.cyclops.integratedtunnels.core;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * @author rubensworks
 */
public class TunnelItemHelpers {

    private static final Cache<Integer, Integer> CACHE_INV_STATES = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS).build();
    private static final Cache<Integer, Boolean> CACHE_INV_CHECKS = CacheBuilder.newBuilder()
            .expireAfterWrite(GeneralConfig.inventoryUnchangedTickTimeout * (1000 / MinecraftHelpers.SECOND_IN_TICKS),
                    TimeUnit.MILLISECONDS).build();

    /**
     * Move items from source to target.
     * @param source The source item handler.
     * @param sourceSlot The source slot.
     * @param target The target item handler.
     * @param targetSlot The target slot.
     * @param amount The maximum item amount to transfer.
     * @param simulate If the transfer should be simulated.
     * @return The moved itemstack.
     */
    public static ItemStack moveItemsSingle(IItemHandler source, int sourceSlot, IItemHandler target, int targetSlot, int amount, boolean simulate) {
        boolean loopSourceSlots = sourceSlot < 0;
        boolean loopTargetSlots = targetSlot < 0;

        if (!loopSourceSlots && !loopTargetSlots) {
            ItemStack extracted = source.extractItem(sourceSlot, amount, simulate);
            if (extracted != null) {
                ItemStack remaining = target.insertItem(targetSlot, extracted, simulate);
                if (remaining == null) {
                    return extracted;
                } else {
                    extracted = extracted.copy();
                    extracted.stackSize -= remaining.stackSize;
                    return extracted.stackSize > 0 ? extracted : null;
                }
            }
        } else if (loopSourceSlots) {
            for (sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
                if (loopTargetSlots) {
                    if (source.getStackInSlot(sourceSlot) != null) {
                        for (targetSlot = 0; targetSlot < target.getSlots(); targetSlot++) {
                            if (!simulate) {
                                ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, true);
                                if (movedSimulated == null) continue;
                            }
                            ItemStack moved = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, simulate);
                            if (moved != null) {
                                return moved;
                            }
                        }
                    }
                } else {
                    if (!simulate) {
                        ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, true);
                        if (movedSimulated == null) continue;
                    }
                    ItemStack moved = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, simulate);
                    if (moved != null) {
                        return moved;
                    }
                }
            }
        } else if (loopTargetSlots) {
            for (targetSlot = 0; targetSlot < target.getSlots(); targetSlot++) {
                if (!simulate) {
                    ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, true);
                    if (movedSimulated == null) continue;
                }
                ItemStack moved = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, simulate);
                if (moved != null) {
                    return moved;
                }
            }
        }

        return null;
    }

    protected static Integer getCachedState(int posHash) {
        return CACHE_INV_STATES.getIfPresent(posHash);
    }

    protected static void setCachedState(int posHash, int state) {
        CACHE_INV_STATES.put(posHash, state);
        CACHE_INV_CHECKS.put(posHash, true);
    }

    protected static boolean shouldCheckState(int posHash) {
        return CACHE_INV_CHECKS.getIfPresent(posHash) == null;
    }

    protected static void invalidateCachedState(int posHash) {
        CACHE_INV_STATES.invalidate(posHash);
        CACHE_INV_CHECKS.invalidate(posHash);
    }

    protected static int getItemStackHashCode(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }
        return Objects.hashCode(itemStack.stackSize, itemStack.getMetadata(),
                Item.getIdFromItem(itemStack.getItem()), itemStack.hasTagCompound() ? itemStack.getTagCompound() : 0);
    }

    /**
     * Calculate an inventory state.
     * @param itemHandler The item handler.
     * @param inventoryState The optional inventory state.
     * @return The inventory state.
     */
    public static int calculateInventoryState(IItemHandler itemHandler, @Nullable IInventoryState inventoryState) {
        if (inventoryState != null) {
            return inventoryState.getHash();
        }
        int hash = itemHandler.hashCode();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            hash += getItemStackHashCode(itemHandler.getStackInSlot(i)) + i;
        }
        return hash;
    }

    /**
     * Move items from source to target.
     * @param source The source item handler.
     * @param sourceSlot The source slot.
     * @param target The target item handler.
     * @param targetSlot The target slot.
     * @param amount The maximum item amount to transfer.
     * @return The moved itemstack.
     */
    public static ItemStack moveItems(IItemHandler source, int sourceSlot, IItemHandler target, int targetSlot, int amount) {
        ItemStack simulatedTransfer = moveItemsSingle(source, sourceSlot, target, targetSlot, amount, true);
        if (simulatedTransfer == null) {
            return null;
        }
        return moveItemsSingle(source, sourceSlot, target, targetSlot, simulatedTransfer.stackSize, false);
    }

    /**
     * Move items from source to target.
     * @param sourceHandler The source item handler.
     * @param sourceSlot The source slot.
     * @param targetHandler The target item handler.
     * @param targetSlot The target slot.
     * @param amount The maximum item amount to transfer.
     * @return The moved itemstack.
     */
    public static ItemStack moveItemsStateOptimized(int sourcePosHash, IItemHandler sourceHandler, @Nullable IInventoryState sourceInvState, int sourceSlot,
                                                    int targetPosHash, IItemHandler targetHandler, @Nullable IInventoryState targetInvState, int targetSlot, int amount) {
        int connectionHash = sourcePosHash + targetPosHash;
        Integer cachedState = getCachedState(connectionHash);

        boolean calculatedStates = false;
        int currentState = 0;
        boolean shouldMoveItems = cachedState == null;
        if (!shouldMoveItems && shouldCheckState(connectionHash)) {
            calculatedStates = true;
            currentState = calculateInventoryState(sourceHandler, sourceInvState) + calculateInventoryState(targetHandler, targetInvState);
            shouldMoveItems = cachedState != currentState;
            if (!shouldMoveItems) {
                CACHE_INV_CHECKS.put(connectionHash, true);
            }
        }

        // If cache miss or a cache state is different
        if (shouldMoveItems) {
            ItemStack simulatedTransfer = moveItemsSingle(sourceHandler, sourceSlot, targetHandler, targetSlot, amount, true);

            // If transfer failed, cache the current states and return
            if (simulatedTransfer == null) {
                if (!calculatedStates) {
                    currentState = calculateInventoryState(sourceHandler, sourceInvState) + calculateInventoryState(targetHandler, targetInvState);
                }
                setCachedState(connectionHash, currentState);
                return null;
            }

            invalidateCachedState(connectionHash);
            return moveItemsSingle(sourceHandler, sourceSlot, targetHandler, targetSlot, simulatedTransfer.stackSize, false);
        }
        return null;
    }

}
