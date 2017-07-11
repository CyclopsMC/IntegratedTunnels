package org.cyclops.integratedtunnels.core;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.Level;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * @author rubensworks
 */
public class TunnelItemHelpers {

    public static final ItemStackPredicate MATCH_ALL = new ItemStackPredicate(ItemStack.EMPTY, ItemMatch.ANY) {
        @Override
        public boolean apply(@Nullable ItemStack input) {
            return true;
        }
    };
    public static final ItemStackPredicate MATCH_NONE = new ItemStackPredicate(ItemStack.EMPTY, ItemMatch.ANY) {
        @Override
        public boolean apply(@Nullable ItemStack input) {
            return false;
        }
    };

    private static final Cache<Integer, Integer> CACHE_INV_STATES = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS).build();
    private static final Cache<Integer, Boolean> CACHE_INV_CHECKS = CacheBuilder.newBuilder()
            .expireAfterWrite(GeneralConfig.inventoryUnchangedTickTimeout * (1000 / MinecraftHelpers.SECOND_IN_TICKS),
                    TimeUnit.MILLISECONDS).build();

    /**
     * Move items from source to target.
     * @param source The source item handler.
     * @param sourceSlot The source slot.
     * @param sourceSlotless The slotless source item handler.
     * @param target The target item handler.
     * @param targetSlot The target slot.
     * @param targetSlotless The slotless target item handler.
     * @param amount The maximum item amount to transfer.
     * @param itemStackMatcher Only itemstack matching this predicate will be moved.
     * @param simulate If the transfer should be simulated.
     * @return The moved itemstack.
     */
    @Nonnull
    public static ItemStack moveItemsSingle(IItemHandler source, int sourceSlot, @Nullable ISlotlessItemHandler sourceSlotless,
                                            IItemHandler target, int targetSlot, @Nullable ISlotlessItemHandler targetSlotless,
                                            int amount, ItemStackPredicate itemStackMatcher, boolean simulate) {
        boolean loopSourceSlots = sourceSlot < 0;
        boolean loopTargetSlots = targetSlot < 0;

        if ((!loopSourceSlots || (sourceSlotless != null && itemStackMatcher.hasMatchFlags())) // Only use slotless source for match flags
                && (!loopTargetSlots || targetSlotless != null)) {
            ItemStack extracted;
            boolean appliedMatcher = false;
            if (loopSourceSlots && itemStackMatcher.hasMatchFlags()) { // In this case it is implied that sourceSlotless != null
                if (itemStackMatcher.getItemStack().isEmpty()) {
                    extracted = sourceSlotless.extractItem(amount, simulate);
                    appliedMatcher = true;
                } else {
                    ItemStack itemStack = itemStackMatcher.getItemStack();
                    if (!itemStack.isEmpty() && itemStack.getCount() != amount) {
                        itemStack.setCount(amount);
                    }
                    extracted = sourceSlotless.extractItem(itemStack, itemStackMatcher.getMatchFlags(), simulate);
                    appliedMatcher = true;
                }
            } else {
                extracted = source.extractItem(sourceSlot, amount, simulate);
            }
            if (!extracted.isEmpty() && (!simulate || appliedMatcher || itemStackMatcher.apply(extracted))) {
                ItemStack remaining = !loopTargetSlots ? target.insertItem(targetSlot, extracted, simulate) : targetSlotless.insertItem(extracted, simulate);
                if (remaining.isEmpty()) {
                    return extracted;
                } else {
                    extracted = extracted.copy();
                    extracted.shrink(remaining.getCount());
                    if (!simulate) {
                        // Re-insert remaining stacks that failed to go into the target, back into the source.
                        remaining = loopSourceSlots ? sourceSlotless.insertItem(remaining, false) : source.insertItem(sourceSlot, remaining, false);
                        if (!remaining.isEmpty()) {
                            IntegratedTunnels.clog(Level.WARN, "Just lost stack " + remaining + " while transfering items, report this to the Integrated Tunnels issue tracker with some details about your setup!");
                        }
                    }
                    return extracted.getCount() > 0 && (simulate || itemStackMatcher.apply(extracted)) ? extracted : ItemStack.EMPTY;
                }
            }
        } else if (loopSourceSlots) {
            for (sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
                if (loopTargetSlots) {
                    if (!source.getStackInSlot(sourceSlot).isEmpty()) {
                        for (targetSlot = 0; targetSlot < target.getSlots(); targetSlot++) {
                            if (!simulate) {
                                ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, true);
                                if (movedSimulated.isEmpty()) continue;
                            }
                            ItemStack moved = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, simulate);
                            if (!moved.isEmpty()) {
                                return moved;
                            }
                        }
                    }
                } else {
                    if (!simulate) {
                        ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, true);
                        if (movedSimulated.isEmpty()) continue;
                    }
                    ItemStack moved = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, simulate);
                    if (!moved.isEmpty()) {
                        return moved;
                    }
                }
            }
        } else if (loopTargetSlots) {
            for (targetSlot = 0; targetSlot < target.getSlots(); targetSlot++) {
                if (!simulate) {
                    ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, true);
                    if (movedSimulated.isEmpty()) continue;
                }
                ItemStack moved = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, simulate);
                if (!moved.isEmpty()) {
                    return moved;
                }
            }
        }

        return ItemStack.EMPTY;
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

    public static int getItemStackHashCode(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }
        return Objects.hashCode(itemStack.getCount(), itemStack.getMetadata(),
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
     * @param sourceSlotless The slotless source item handler.
     * @param target The target item handler.
     * @param targetSlot The target slot.
     * @param targetSlotless The slotless target item handler.
     * @param amount The maximum item amount to transfer.
     * @return The moved itemstack.
     */
    @Nonnull
    public static ItemStack moveItems(IItemHandler source, int sourceSlot, @Nullable ISlotlessItemHandler sourceSlotless,
                                      IItemHandler target, int targetSlot, @Nullable ISlotlessItemHandler targetSlotless,
                                      int amount) {
        ItemStack simulatedTransfer = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, MATCH_ALL, true);
        if (simulatedTransfer.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, simulatedTransfer.getCount(), MATCH_ALL, false);
    }

    /**
     * Move items from source to target.
     * @param connectionHash The connection hash.
     * @param sourceHandler The source item handler.
     * @param sourceInvState Optional inventory state of the source.
     * @param sourceSlot The source slot.
     * @param sourceSlotless The slotless source item handler.
     * @param targetHandler The target item handler.
     * @param targetInvState Optional inventory state of the target.
     * @param targetSlot The target slot.
     * @param targetSlotless The slotless target item handler.
     * @param amount The maximum item amount to transfer.
     * @param itemStackMatcher Only itemstack matching this predicate will be moved.
     * @return The moved itemstack.
     */
    @Nonnull
    public static ItemStack moveItemsStateOptimized(int connectionHash,
                                                    IItemHandler sourceHandler, @Nullable IInventoryState sourceInvState, int sourceSlot, @Nullable ISlotlessItemHandler sourceSlotless,
                                                    IItemHandler targetHandler, @Nullable IInventoryState targetInvState, int targetSlot, @Nullable ISlotlessItemHandler targetSlotless,
                                                    int amount, ItemStackPredicate itemStackMatcher) {
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
            ItemStack simulatedTransfer = moveItemsSingle(sourceHandler, sourceSlot, sourceSlotless, targetHandler, targetSlot, targetSlotless, amount, itemStackMatcher, true);

            // If transfer failed, cache the current states and return
            if (simulatedTransfer.isEmpty()) {
                if (!calculatedStates) {
                    currentState = calculateInventoryState(sourceHandler, sourceInvState) + calculateInventoryState(targetHandler, targetInvState);
                }
                setCachedState(connectionHash, currentState);
                return ItemStack.EMPTY;
            }

            invalidateCachedState(connectionHash);
            return moveItemsSingle(sourceHandler, sourceSlot, sourceSlotless, targetHandler, targetSlot, targetSlotless, simulatedTransfer.getCount(), itemStackMatcher, false);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStackPredicate matchItemStack(final ItemStack itemStack, final boolean checkStackSize,
                                                      final boolean checkDamage, final boolean checkNbt) {
        int matchFlags = ItemMatch.ANY;
        if (checkDamage)    matchFlags = matchFlags | ItemMatch.DAMAGE;
        if (checkNbt)       matchFlags = matchFlags | ItemMatch.NBT;
        if (checkStackSize) matchFlags = matchFlags | ItemMatch.STACKSIZE;
        return new ItemStackPredicate(itemStack, matchFlags) {
            @Override
            public boolean apply(@Nullable ItemStack input) {
                return areItemStackEqual(input, itemStack, checkStackSize, true, checkDamage, checkNbt);
            }
        };
    }

    public static ItemStackPredicate matchItemStacks(final IValueTypeListProxy<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> itemStacks,
                                                       final boolean checkStackSize, final boolean checkDamage, final boolean checkNbt) {
        return new ItemStackPredicate() {
            @Override
            public boolean apply(@Nullable ItemStack input) {
                for (ValueObjectTypeItemStack.ValueItemStack itemStack : itemStacks) {
                    if (!itemStack.getRawValue().isEmpty()
                            && areItemStackEqual(input, itemStack.getRawValue(), checkStackSize, true, checkDamage, checkNbt)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static ItemStackPredicate matchPredicate(final PartTarget partTarget, final IOperator predicate) {
        return new ItemStackPredicate() {
            @Override
            public boolean apply(@Nullable ItemStack input) {
                ValueObjectTypeItemStack.ValueItemStack valueItemStack = ValueObjectTypeItemStack.ValueItemStack.of(input);
                try {
                    IValue result = ValueHelpers.evaluateOperator(predicate, valueItemStack);
                    return ((ValueTypeBoolean.ValueBoolean) result).getRawValue();
                } catch (EvaluationException e) {
                    PartHelpers.PartStateHolder<?, ?> partData = PartHelpers.getPart(partTarget.getCenter());
                    if (partData != null) {
                        IPartStateWriter partState = (IPartStateWriter) partData.getState();
                        partState.addError(partState.getActiveAspect(), new L10NHelpers.UnlocalizedString(e.getMessage()));
                        partState.setDeactivated(true);
                    }
                    return false;
                }
            }
        };
    }

    public static boolean areItemStackEqual(ItemStack stackA, ItemStack stackB,
                                            boolean checkStackSize, boolean checkItem, boolean checkDamage, boolean checkNbt) {
        if (stackA == null && stackB == null) return true;
        if (stackA != null && stackB != null) {
            if (checkStackSize && stackA.getCount() != stackB.getCount()) return false;
            if (checkItem && stackA.getItem() != stackB.getItem()) return false;
            if (checkDamage && stackA.getItemDamage() != stackB.getItemDamage()) return false;
            if (checkNbt && !ItemStack.areItemStackTagsEqual(stackA, stackB)) return false;
            return true;
        }
        return false;
    }

    /**
     * Place items from the given source in the world.
     * @param connectionHash The connection hash.
     * @param sourceHandler The source item handler.
     * @param sourceInvState Optional inventory state of the source.
     * @param sourceSlotless The slotless source item handler.
     * @param world The target world.
     * @param pos The target position.
     * @param itemStackMatcher The itemstack match predicate.
     * @param hand The hand to place the block with.
     * @param blockUpdate If a block update should occur after placement.
     * @return The placed item.
     */
    public static ItemStack placeItems(int connectionHash,
                                       IItemHandler sourceHandler, @Nullable IInventoryState sourceInvState,
                                       @Nullable ISlotlessItemHandler sourceSlotless,
                                       World world, BlockPos pos, EnumFacing side,
                                       ItemStackPredicate itemStackMatcher, EnumHand hand, boolean blockUpdate) {
        IBlockState destBlockState = world.getBlockState(pos);
        final Material destMaterial = destBlockState.getMaterial();
        final boolean isDestNonSolid = !destMaterial.isSolid();
        final boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
        // TODO: add parameter to not replace replacables
        if (!world.isAirBlock(pos)
                && (!isDestNonSolid || !isDestReplaceable || destMaterial.isLiquid())) {
            return null;
        }

        IItemHandler targetBlock = new ItemHandlerBlockWrapper((WorldServer) world, pos, side, hand, blockUpdate);
        return TunnelItemHelpers.moveItemsStateOptimized(connectionHash, sourceHandler, sourceInvState, -1,
                sourceSlotless, targetBlock, null, 0, null, 1, itemStackMatcher);
    }

}
