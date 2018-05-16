package org.cyclops.integratedtunnels.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.cyclopscore.helper.BlockHelpers;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.NbtHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author rubensworks
 */
public class TunnelItemHelpers {

    public static final ItemStackPredicate MATCH_ALL = new ItemStackPredicate(ItemStack.EMPTY, ItemMatch.ANY,
            false, ItemStackPredicate.EmptyBehaviour.ANY) {
        @Override
        public boolean test(@Nullable ItemStack input) {
            return true;
        }
    };
    public static final ItemStackPredicate MATCH_NONE = new ItemStackPredicate(ItemStack.EMPTY, ItemMatch.EXACT,
            false, ItemStackPredicate.EmptyBehaviour.NONE) {
        @Override
        public boolean test(@Nullable ItemStack input) {
            return false;
        }
    };
    public static final ItemStackPredicate MATCH_BLOCK = new ItemStackPredicate(false) {
        @Override
        public boolean test(@Nullable ItemStack input) {
            return !input.isEmpty();
        }
    };

    private static final Cache<Integer, Pair<Integer, Integer>> CACHE_INV_STATES = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS).build();
    private static final Cache<Integer, Boolean> CACHE_CONNECTION_TRANSFER_ATTEMPTED = CacheBuilder.newBuilder()
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
        if (sourceSlot >= source.getSlots() || targetSlot >= target.getSlots()) {
            return ItemStack.EMPTY;
        }
        boolean loopSourceSlots = sourceSlot < 0;
        boolean loopTargetSlots = targetSlot < 0;

        if ((!loopSourceSlots || (sourceSlotless != null && itemStackMatcher.hasMatchFlags() && !itemStackMatcher.isBlacklist())) // Only use slotless source for match flags and NOT blacklist
                && (!loopTargetSlots || targetSlotless != null)) {
            ItemStack extracted;
            boolean appliedMatcher = false;
            // In this case it is implied that sourceSlotless != null
            // Don't use matcher if we have a blacklist, as the flags don't support that (yet: https://github.com/CyclopsMC/CommonCapabilitiesAPI/issues/4)
            if (loopSourceSlots && itemStackMatcher.hasMatchFlags() && !itemStackMatcher.isBlacklist()) {
                if (itemStackMatcher.getItemStack().isEmpty()) {
                    extracted = sourceSlotless.extractItem(amount, simulate);
                    appliedMatcher = true;
                } else {
                    ItemStack itemStack = itemStackMatcher.getItemStack();
                    if (!itemStack.isEmpty() && itemStack.getCount() != amount) {
                        itemStack = itemStack.copy();
                        itemStack.setCount(amount);
                    }
                    extracted = sourceSlotless.extractItem(itemStack, itemStackMatcher.getMatchFlags(), simulate);
                    appliedMatcher = true;
                }
            } else {
                extracted = source.extractItem(sourceSlot, amount, simulate);
            }
            if (!extracted.isEmpty() && (!simulate || appliedMatcher || itemStackMatcher.test(extracted))) {
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
                    return extracted.getCount() > 0 && (simulate || itemStackMatcher.test(extracted)) ? extracted : ItemStack.EMPTY;
                }
            }
        } else if (loopSourceSlots) {
            for (sourceSlot = 0; sourceSlot < source.getSlots(); sourceSlot++) {
                if (!simulate) {
                    ItemStack movedSimulated = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, true);
                    if (movedSimulated.isEmpty()) continue;
                }
                ItemStack moved = moveItemsSingle(source, sourceSlot, sourceSlotless, target, targetSlot, targetSlotless, amount, itemStackMatcher, simulate);
                if (!moved.isEmpty()) {
                    return moved;
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

    protected static boolean shouldAttemptTransfer(int posHash) {
        return CACHE_CONNECTION_TRANSFER_ATTEMPTED.getIfPresent(posHash) == null;
    }

    protected static void invalidateCachedState(int posHash) {
        CACHE_INV_STATES.invalidate(posHash);
        CACHE_CONNECTION_TRANSFER_ATTEMPTED.invalidate(posHash);
    }

    /**
     * Calculate an inventory state.
     * @param itemHandler The item handler.
     * @param inventoryState The optional inventory state.
     * @return The inventory state.
     */
    @Deprecated
    public static int calculateInventoryState(IItemHandler itemHandler, @Nullable IInventoryState inventoryState) {
        if (inventoryState != null) {
            return inventoryState.getHash();
        }
        // TODO: this is an incorrect implementation of the inv state hash contract.
        // TODO: this should be reimplemented by observing the network and changing the state if anything in the inv has changed
        int hash = itemHandler.hashCode();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            hash += ItemStackHelpers.getItemStackHashCode(itemHandler.getStackInSlot(i)) << (i % 100);
        }
        return hash;
    }

    protected static Pair<Integer, Integer> getConnectionInventoryState(IInventoryState sourceInvState, IInventoryState targetInvState) {
        return Pair.of(sourceInvState.hashCode(), targetInvState.getHash());
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
     * @param exact If only the exact amount is allowed to be transferred.
     * @return The moved itemstack.
     */
    @Nonnull
    public static ItemStack moveItemsStateOptimized(int connectionHash,
                                                    IItemHandler sourceHandler, @Nullable IInventoryState sourceInvState, int sourceSlot, @Nullable ISlotlessItemHandler sourceSlotless,
                                                    IItemHandler targetHandler, @Nullable IInventoryState targetInvState, int targetSlot, @Nullable ISlotlessItemHandler targetSlotless,
                                                    int amount, ItemStackPredicate itemStackMatcher, boolean exact) {
        // TODO: connectionHash should make a unique id, as this should not be able to clash!
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        boolean shouldTransfer = shouldAttemptTransfer(connectionHash);

        // Optimization if a source and target inventory state is available
        if (shouldTransfer && sourceInvState != null && targetInvState != null
                && getConnectionInventoryState(sourceInvState, targetInvState)
                .equals(CACHE_INV_STATES.getIfPresent(connectionHash))) {
            shouldTransfer = false;
        }

        // If cache miss or a cache state is different
        if (shouldTransfer) {
            ItemStack simulatedTransfer = moveItemsSingle(sourceHandler, sourceSlot, sourceSlotless, targetHandler, targetSlot, targetSlotless, amount, itemStackMatcher, true);

            // If transfer failed, cache the current states and return
            if (simulatedTransfer.isEmpty() || (exact && amount != simulatedTransfer.getCount())) {
                if (sourceInvState != null && targetInvState != null) {
                    CACHE_INV_STATES.put(connectionHash, getConnectionInventoryState(sourceInvState, targetInvState));
                }
                CACHE_CONNECTION_TRANSFER_ATTEMPTED.put(connectionHash, true);
                return ItemStack.EMPTY;
            }

            invalidateCachedState(connectionHash);
            return moveItemsSingle(sourceHandler, sourceSlot, sourceSlotless, targetHandler, targetSlot, targetSlotless, simulatedTransfer.getCount(), itemStackMatcher, false);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStackPredicate matchItemStack(final ItemStack itemStack, final boolean checkStackSize,
                                                    final boolean checkDamage, final boolean checkNbt,
                                                    final boolean blacklist,
                                                    final ItemStackPredicate.EmptyBehaviour emptyBehaviour) {
        int matchFlags = ItemMatch.ANY;
        if (checkDamage)    matchFlags = matchFlags | ItemMatch.DAMAGE;
        if (checkNbt)       matchFlags = matchFlags | ItemMatch.NBT;
        if (checkStackSize) matchFlags = matchFlags | ItemMatch.STACKSIZE;
        return new ItemStackPredicate(itemStack.copy(), matchFlags, blacklist, emptyBehaviour) {
            @Override
            public boolean test(@Nullable ItemStack input) {
                if (getItemStack().isEmpty()) {
                    return emptyBehaviour == EmptyBehaviour.ANY;
                }
                boolean result = areItemStackEqual(input, itemStack, checkStackSize, true, checkDamage, checkNbt);
                if (blacklist) {
                    result = !result;
                }
                return result;
            }
        };
    }

    public static ItemStackPredicate matchItemStacks(final IValueTypeListProxy<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> itemStacks,
                                                       final boolean checkStackSize, final boolean checkDamage, final boolean checkNbt, final boolean blacklist) {
        return new ItemStackPredicate(blacklist) {
            @Override
            public boolean test(@Nullable ItemStack input) {
                for (ValueObjectTypeItemStack.ValueItemStack itemStack : itemStacks) {
                    if (!itemStack.getRawValue().isEmpty()
                            && areItemStackEqual(input, itemStack.getRawValue(), checkStackSize, true, checkDamage, checkNbt)) {
                        return !blacklist;
                    }
                }
                return blacklist;
            }
        };
    }

    public static ItemStackPredicate matchPredicateItem(final PartTarget partTarget, final IOperator predicate) {
        return new ItemStackPredicate(false) {
            @Override
            public boolean test(@Nullable ItemStack input) {
                ValueObjectTypeItemStack.ValueItemStack valueItemStack = ValueObjectTypeItemStack.ValueItemStack.of(input);
                try {
                    IValue result = ValueHelpers.evaluateOperator(predicate, valueItemStack);
                    ValueHelpers.validatePredicateOutput(predicate, result);
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

    public static ItemStackPredicate matchBlocks(final IValueTypeListProxy<ValueObjectTypeBlock, ValueObjectTypeBlock.ValueBlock> blocks,
                                                     final boolean checkStackSize, final boolean checkDamage, final boolean checkNbt, final boolean blacklist) {
        return new ItemStackPredicate(blacklist) {
            @Override
            public boolean test(@Nullable ItemStack input) {
                for (ValueObjectTypeBlock.ValueBlock block : blocks) {
                    if (!block.getRawValue().isPresent()
                            && areItemStackEqual(input, BlockHelpers.getItemStackFromBlockState(block.getRawValue().get()), checkStackSize, true, checkDamage, checkNbt)) {
                        return !blacklist;
                    }
                }
                return blacklist;
            }
        };
    }

    public static ItemStackPredicate matchPredicateBlock(final PartTarget partTarget, final IOperator predicate) {
        return new ItemStackPredicate(false) {
            @Override
            public boolean test(@Nullable ItemStack input) {
                ValueObjectTypeBlock.ValueBlock valueBlock = ValueObjectTypeBlock.ValueBlock.of(
                        input.getItem() instanceof ItemBlock ? BlockHelpers.getBlockStateFromItemStack(input) : null);
                try {
                    IValue result = ValueHelpers.evaluateOperator(predicate, valueBlock);
                    ValueHelpers.validatePredicateOutput(predicate, result);
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

    public static ItemStackPredicate matchNbt(final NBTTagCompound tag, final boolean subset, final boolean superset, final boolean requireNbt, final boolean recursive, final boolean blacklist) {
        return new ItemStackPredicate(blacklist) {
            @Override
            public boolean test(@Nullable ItemStack input) {
                if (!input.hasTagCompound() && requireNbt) {
                    return isBlacklist();
                }
                NBTTagCompound itemTag = input.hasTagCompound() ? input.getTagCompound() : new NBTTagCompound();
                boolean ret = (!subset || NbtHelpers.nbtMatchesSubset(tag, itemTag, recursive))
                        && (!superset || NbtHelpers.nbtMatchesSubset(itemTag, tag, recursive));
                if (blacklist) {
                    ret = !ret;
                }
                return ret;
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
     * Place item blocks from the given source in the world.
     * @param connectionHash The connection hash.
     * @param sourceHandler The source item handler.
     * @param sourceInvState Optional inventory state of the source.
     * @param sourceSlotless The slotless source item handler.
     * @param world The target world.
     * @param pos The target position.
     * @param side The target side.
     * @param itemStackMatcher The itemstack match predicate.
     * @param hand The hand to place the block with.
     * @param blockUpdate If a block update should occur after placement.
     * @param ignoreReplacable If replacable blocks should be overriden when placing blocks.
     * @param exact If only the exact amount is allowed to be transferred.
     * @return The placed item.
     */
    public static ItemStack placeItems(int connectionHash,
                                       IItemHandler sourceHandler, @Nullable IInventoryState sourceInvState,
                                       @Nullable ISlotlessItemHandler sourceSlotless,
                                       World world, BlockPos pos, EnumFacing side,
                                       ItemStackPredicate itemStackMatcher, EnumHand hand,
                                       boolean blockUpdate, boolean ignoreReplacable, boolean exact) {
        IBlockState destBlockState = world.getBlockState(pos);
        final Material destMaterial = destBlockState.getMaterial();
        final boolean isDestNonSolid = !destMaterial.isSolid();
        final boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
        if (!world.isAirBlock(pos)
                && (!isDestNonSolid || !(ignoreReplacable && isDestReplaceable))) {
            return null;
        }

        IItemHandler targetBlock = new ItemHandlerBlockWrapper(true, (WorldServer) world, pos, side,
                hand, blockUpdate, 0, false, ignoreReplacable, true);
        return TunnelItemHelpers.moveItemsStateOptimized(connectionHash, sourceHandler, sourceInvState, -1,
                sourceSlotless, targetBlock, null, 0, null, 1,
                itemStackMatcher, exact);
    }

    /**
     * Pick up item blocks from the given source in the world.
     * @param connectionHash The connection hash.
     * @param world The target world.
     * @param pos The target position.
     * @param side The target side.
     * @param targetHandler The source item handler.
     * @param targetInvState Optional inventory state of the source.
     * @param targetSlotless The slotless source item handler.
     * @param itemStackMatcher The itemstack match predicate.
     * @param hand The hand to place the block with.
     * @param blockUpdate If a block update should occur after placement.
     * @param ignoreReplacable If replacable blocks should be ignored from picking up.
     * @param fortune The fortune level.
     * @param silkTouch If the block should be broken with silk touch.
     * @param breakOnNoDrops If the block should be broken if it produced no drops.
     * @param exact If only the exact amount is allowed to be transferred.
     * @return The picked-up items.
     */
    public static List<ItemStack> pickUpItems(int connectionHash, World world, BlockPos pos, EnumFacing side,
                                              IItemHandler targetHandler, @Nullable IInventoryState targetInvState,
                                              @Nullable ISlotlessItemHandler targetSlotless,
                                              ItemStackPredicate itemStackMatcher, EnumHand hand, boolean blockUpdate,
                                              boolean ignoreReplacable, int fortune, boolean silkTouch,
                                              boolean breakOnNoDrops, boolean exact) {
        IBlockState destBlockState = world.getBlockState(pos);
        final Material destMaterial = destBlockState.getMaterial();
        final boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
        if (world.isAirBlock(pos)
                || ((ignoreReplacable && isDestReplaceable) || destMaterial.isLiquid())) {
            return null;
        }

        IItemHandler sourceBlock = new ItemHandlerBlockWrapper(false, (WorldServer) world, pos, side,
                hand, blockUpdate, fortune, silkTouch, ignoreReplacable, breakOnNoDrops);
        List<ItemStack> itemStacks = Lists.newArrayList();
        ItemStack itemStack;
        while (!(itemStack = TunnelItemHelpers.moveItemsStateOptimized(connectionHash, sourceBlock, null, -1, null,
                targetHandler, targetInvState, -1, targetSlotless, Integer.MAX_VALUE, itemStackMatcher, exact)).isEmpty()) {
            itemStacks.add(itemStack);
        }
        return itemStacks;
    }

}
