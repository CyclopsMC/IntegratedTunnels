package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.cyclopscore.ingredient.collection.FilteredIngredientCollectionIterator;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandlerRegistry;
import org.cyclops.integratedtunnels.api.world.IBlockPlaceHandler;
import org.cyclops.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;
import org.cyclops.integratedtunnels.item.ItemDummyPickAxe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * An item storage for world block placement.
 * @author rubensworks
 */
public class ItemStorageBlockWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final boolean writeOnly;
    private final ServerLevel world;
    private final BlockPos pos;
    private final Direction side;
    private final InteractionHand hand;
    private final boolean blockUpdate;
    private final int fortune;
    private final boolean silkTouch;
    private final boolean ignoreReplacable;
    private final boolean breakOnNoDrops;
    private final ItemStorageBlockWrapper.JournalExtract journalExtract;
    private final ItemStorageBlockWrapper.JournalInsert journalInsert;

    private IBlockBreakHandler blockBreakHandler = null;
    private List<ItemStack> cachedDrops = null;
    private boolean extracted = false;
    private ItemStack insertedItem;

    public ItemStorageBlockWrapper(boolean writeOnly, ServerLevel world, BlockPos pos, Direction side, InteractionHand hand,
                                   boolean blockUpdate, int fortune, boolean silkTouch, boolean ignoreReplacable,
                                   boolean breakOnNoDrops) {
        this.writeOnly = writeOnly;
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.hand = hand;
        this.blockUpdate = blockUpdate;
        this.fortune = fortune;
        this.silkTouch = silkTouch;
        this.ignoreReplacable = ignoreReplacable;
        this.breakOnNoDrops = breakOnNoDrops;
        this.journalExtract = new ItemStorageBlockWrapper.JournalExtract();
        this.journalInsert = new ItemStorageBlockWrapper.JournalInsert();
    }

    protected void sendBlockUpdate() {
        world.neighborChanged(pos, Blocks.AIR, null);
    }

    /**
     * Check if a block is blacklisted from being imported.
     * @param blockState The block state to check.
     * @return True if the block is blacklisted, false otherwise.
     */
    protected boolean isBlockBlacklisted(BlockState blockState) {
        var blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
        String blockIdString = blockId.toString();

        for (String patternString : GeneralConfig.blockImporterBlacklist) {
            try {
                if (Pattern.matches(patternString, blockIdString)) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                // If the pattern is invalid, log and skip it
                IntegratedTunnels.clog(org.apache.logging.log4j.Level.WARN,
                        "Invalid block importer blacklist pattern: " + patternString + " - " + e.getMessage());
            }
        }
        return false;
    }

    protected IBlockBreakHandler getBlockBreakHandler(BlockState blockState, Level world, BlockPos pos, Player player) {
        return IntegratedTunnels._instance.getRegistryManager().getRegistry(IBlockBreakHandlerRegistry.class)
                .getHandler(blockState, world, pos, player);
    }

    protected void removeBlock(BlockState blockState, Player player) {
        if (blockBreakHandler != null) {
            blockBreakHandler.breakBlock(blockState, world, pos, player);
        } else {
            FluidState fluidState = world.getFluidState(pos);
            boolean removed = blockState.onDestroyedByPlayer(this.world, pos, player, player.getItemInHand(player.getUsedItemHand()), false, fluidState);
            if (removed) {
                blockState.getBlock().destroy(this.world, pos, blockState);
            }
        }
        if (GeneralConfig.worldInteractionEvents) {
            world.levelEvent(2001, pos, Block.getId(blockState)); // Particles + Sound
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    // Modified from Block#getDrops
    public static List<ItemStack> getDrops(BlockState state, ServerLevel worldIn, BlockPos pos, @Nullable BlockEntity tileEntityIn) {
        LootParams.Builder lootcontext$builder = (new LootParams.Builder(worldIn))
                .withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, tileEntityIn);
        return state.getDrops(lootcontext$builder);
    }

    public boolean isExtracted() {
        return extracted;
    }

    @Nullable
    public List<ItemStack> getCachedDrops() {
        return cachedDrops;
    }

    protected List<ItemStack> getItemStacks() {
        if (this.insertedItem != null) {
            return Collections.emptyList();
        }
        if (writeOnly) {
            if (!world.isEmptyBlock(pos)) {
                boolean isDestReplaceable = world.getBlockState(pos).canBeReplaced(TunnelHelpers.createBlockItemUseContext(world, null, pos, side, hand));
                if (!isDestReplaceable || !ignoreReplacable) {
                    BlockState blockState = world.getBlockState(pos);
                    return Lists.newArrayList(IModHelpers.get().getBlockHelpers().getItemStackFromBlockState(blockState));
                }
            }
        } else {
            if (cachedDrops != null) {
                return cachedDrops;
            }
            if (!world.isEmptyBlock(pos)) {
                BlockState blockState = world.getBlockState(pos);

                // Check if the block is blacklisted
                if (isBlockBlacklisted(blockState)) {
                    return cachedDrops = Lists.newArrayList();
                }

                Player player = PlayerHelpers.getFakePlayer(world);
                PlayerHelpers.setPlayerState(player, hand, pos, 0, 0, 0, side, false);

                blockBreakHandler = getBlockBreakHandler(blockState, world, pos, player);
                if (blockBreakHandler != null) {
                    return cachedDrops = blockBreakHandler.getDrops(blockState, world, pos, player);
                } else {
                    BreakBlockEvent blockBreakEvent = new BreakBlockEvent(world, pos, blockState, player);
                    if (!NeoForge.EVENT_BUS.post(blockBreakEvent).isCanceled()) {
                        List<ItemStack> drops = Block.getDrops(blockState, world, pos, world.getBlockEntity(pos), null, ItemDummyPickAxe.getItemStack(silkTouch, fortune));
                        if (drops.size() == 0) {
                            // Remove the block if it dropped nothing (and will drop nothing)
                            if (breakOnNoDrops) {
                                removeBlock(blockState, player);
                            }
                            drops = Lists.newArrayList(ItemStack.EMPTY);
                        } else {
                            // Make sure there are no empty stacks in the list
                            drops = Lists.newArrayList(drops);
                            drops.removeIf(ItemStack::isEmpty);
                        }
                        return cachedDrops = drops;
                    }
                }
            }
        }
        return Lists.newArrayList(ItemStack.EMPTY);
    }

    protected IBlockPlaceHandler getBlockPlaceHandler(ItemStack itemStack, Level world, BlockPos pos, Direction side,
                                                      float hitX, float hitY, float hitZ, Player player) {
        return IntegratedTunnels._instance.getRegistryManager().getRegistry(IBlockPlaceHandlerRegistry.class)
                .getHandler(itemStack, world, pos, side, hitX, hitY, hitZ, player);
    }

    protected ItemStack setItemStack(ItemStack itemStack, TransactionContext transactionContext) {
        if (!itemStack.isEmpty() && itemStack.getCount() == 1) {
            Item item = itemStack.getItem();
            if (item instanceof BlockItem) {
                BlockItem itemBlock = (BlockItem) item;

                Player player = PlayerHelpers.getFakePlayer(world);
                PlayerHelpers.setPlayerState(player, hand, pos, 0, 0, 0, side, false);

                IBlockPlaceHandler blockPlaceHandler = getBlockPlaceHandler(itemStack, world, pos, side.getOpposite(),
                        0, 0, 0, player);
                if (blockPlaceHandler != null) {
                    blockPlaceHandler.placeBlock(itemStack, world, pos, side.getOpposite(), 0, 0, 0, player, transactionContext);
                } else {
                    BlockPlaceContext blockItemUseContext = TunnelHelpers.createBlockItemUseContext(world, player, pos, side.getOpposite(), hand, itemStack);
                    BlockState blockState = itemBlock.getBlock().getStateForPlacement(blockItemUseContext);
                    if (blockState != null) {
                        this.insertedItem = itemStack;
                        this.journalInsert.updateSnapshots(transactionContext);
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return itemStack;
    }

    // Copied from BlockItem
    private static void updateBlockEntityComponents(Level pLevel, BlockPos pPoa, ItemStack pStack) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPoa);
        if (blockentity != null) {
            blockentity.applyComponentsFromItemStack(pStack);
            blockentity.setChanged();
        }
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return Lists.newArrayList(getItemStacks()).iterator();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return new FilteredIngredientCollectionIterator<>(this, getComponent().getMatcher(), prototype, matchCondition);
    }

    @Override
    public long getMaxQuantity() {
        return 1;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, TransactionContext transaction) {
        List<ItemStack> itemStacks = getItemStacks();
        if (itemStacks.size() > 0) {
            ItemStack itemStack = itemStacks.get(0);
            if (!itemStack.isEmpty()) {
                return stack;
            }
        }

        if (stack.isEmpty()) {
            return stack;
        }

        ItemStack remaining = stack.copy();
        if (!setItemStack(remaining.split(1), transaction).isEmpty()) {
            return stack;
        }

        return remaining;
    }

    public void postExtract() {
        boolean allEmpty = true;
        for (ItemStack stack : getItemStacks()) {
            if (!stack.isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        if (allEmpty) {
            BlockState blockState = world.getBlockState(pos);
            Player player = PlayerHelpers.getFakePlayer(world);
            player.startUsingItem(hand);
            removeBlock(blockState, player);
        }
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, TransactionContext transaction) {
        IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
        Integer quantityFlag = getComponent().getPrimaryQuantifier().getMatchCondition();
        Integer subMatchCondition = matcher.withoutCondition(matchCondition, quantityFlag);
        List<ItemStack> itemStacks = getItemStacks();
        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        this.journalExtract.updateSnapshots(transaction);
        ListIterator<ItemStack> it = itemStacks.listIterator();
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            if (matcher.matches(prototype, itemStack, subMatchCondition)
                    && (!matcher.hasCondition(matchCondition, quantityFlag) || itemStack.getCount() >= prototype.getCount())) {
                itemStack = itemStack.copy();
                ItemStack ret = itemStack.split(IModHelpers.get().getBaseHelpers().castSafe(prototype.getCount()));
                if (itemStack.isEmpty()) {
                    it.remove();
                } else {
                    it.set(itemStack);
                }
                return ret;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, TransactionContext transaction) {
        List<ItemStack> itemStacks = getItemStacks();
        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.journalExtract.updateSnapshots(transaction);
        ItemStack itemStack = itemStacks.get(0);
        itemStack = itemStack.copy();
        ItemStack ret = itemStack.split(IModHelpers.get().getBaseHelpers().castSafe(maxQuantity));
        if (itemStack.isEmpty()) {
            itemStacks.remove(0);
        } else {
            itemStacks.set(0, itemStack);
        }
        return ret;
    }

    private class JournalExtract extends SnapshotJournal<List<ItemStack>> {

        @Override
        protected List<ItemStack> createSnapshot() {
            return Lists.newArrayList(getItemStacks());
        }

        @Override
        protected void revertToSnapshot(List<ItemStack> itemStacks) {
            cachedDrops = itemStacks;
        }

        @Override
        protected void onRootCommit(List<ItemStack> originalState) {
            super.onRootCommit(originalState);

            // Check if all items have been extracted, if so, remove block
            extracted = true;
            postExtract();
        }
    }

    private class JournalInsert extends SnapshotJournal<ItemStack> {
        @Override
        protected ItemStack createSnapshot() {
            return insertedItem.copy();
        }

        @Override
        protected void revertToSnapshot(ItemStack unused) {

        }

        @Override
        protected void onRootCommit(ItemStack itemStack) {
            super.onRootCommit(itemStack);

            Player player = PlayerHelpers.getFakePlayer(world);
            PlayerHelpers.setPlayerState(player, hand, pos, 0, 0, 0, side, false);
            BlockItem itemBlock = (BlockItem) itemStack.getItem();
            BlockPlaceContext blockItemUseContext = TunnelHelpers.createBlockItemUseContext(world, player, pos, side.getOpposite(), hand, itemStack);
            BlockState blockState = itemBlock.getBlock().getStateForPlacement(blockItemUseContext);

            // Finalize placement of the item
            if (itemBlock.placeBlock(blockItemUseContext, blockState)) {
                itemBlock.updateCustomBlockEntityTag(pos, world, blockItemUseContext.getPlayer(), itemStack, blockState);
                updateBlockEntityComponents(world, pos, itemStack);
                itemBlock.getBlock().setPlacedBy(world, pos, blockState, player, itemStack);
                if (GeneralConfig.worldInteractionEvents) {
                    SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F); // Sound
                }
                if (blockUpdate) {
                    sendBlockUpdate();
                }
            }
        }
    }
}
