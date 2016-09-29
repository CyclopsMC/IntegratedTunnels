package org.cyclops.integratedtunnels.core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * @author rubensworks
 */
public class TunnelItemHelpers {

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

}
