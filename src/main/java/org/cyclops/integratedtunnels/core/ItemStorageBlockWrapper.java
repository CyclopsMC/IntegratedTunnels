package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.BlockHelpers;
import org.cyclops.cyclopscore.helper.Helpers;
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An item storage for world block placement.
 * @author rubensworks
 */
public class ItemStorageBlockWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final boolean writeOnly;
    private final ServerWorld world;
    private final BlockPos pos;
    private final Direction side;
    private final Hand hand;
    private final boolean blockUpdate;
    private final int fortune;
    private final boolean silkTouch;
    private final boolean ignoreReplacable;
    private final boolean breakOnNoDrops;

    private IBlockBreakHandler blockBreakHandler = null;
    private List<ItemStack> cachedDrops = null;
    private boolean extracted = false;

    public ItemStorageBlockWrapper(boolean writeOnly, ServerWorld world, BlockPos pos, Direction side, Hand hand,
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
    }

    protected void sendBlockUpdate() {
        world.neighborChanged(pos, Blocks.AIR, pos);
    }

    protected IBlockBreakHandler getBlockBreakHandler(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        return IntegratedTunnels._instance.getRegistryManager().getRegistry(IBlockBreakHandlerRegistry.class)
                .getHandler(blockState, world, pos, player);
    }

    protected void removeBlock(BlockState blockState, PlayerEntity player) {
        if (blockBreakHandler != null) {
            blockBreakHandler.breakBlock(blockState, world, pos, player);
        } else {
            blockState.getBlock().removedByPlayer(blockState, world, pos, player, false, world.getFluidState(pos));
        }
        if (GeneralConfig.worldInteractionEvents) {
            world.levelEvent(2001, pos, Block.getId(blockState)); // Particles + Sound
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    // Modified from Block#getDrops
    public static List<ItemStack> getDrops(BlockState state, ServerWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(worldIn))
                .withRandom(worldIn.random)
                .withParameter(LootParameters.ORIGIN, new Vector3d(pos.getX(), pos.getY(), pos.getZ()))
                .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
                .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootParameters.BLOCK_ENTITY, tileEntityIn);
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
        if (writeOnly) {
            if (!world.isEmptyBlock(pos)) {
                boolean isDestReplaceable = world.getBlockState(pos).canBeReplaced(TunnelHelpers.createBlockItemUseContext(world, null, pos, side, hand));
                if (!isDestReplaceable || !ignoreReplacable) {
                    BlockState blockState = world.getBlockState(pos);
                    return Lists.newArrayList(BlockHelpers.getItemStackFromBlockState(blockState));
                }
            }
        } else {
            if (cachedDrops != null) {
                return cachedDrops;
            }
            if (!world.isEmptyBlock(pos)) {
                BlockState blockState = world.getBlockState(pos);

                PlayerEntity player = PlayerHelpers.getFakePlayer(world);
                PlayerHelpers.setPlayerState(player, hand, pos, 0, 0, 0, side, false);

                blockBreakHandler = getBlockBreakHandler(blockState, world, pos, player);
                if (blockBreakHandler != null) {
                    cachedDrops = blockBreakHandler.getDrops(blockState, world, pos, player);
                } else {
                    BlockEvent.BreakEvent blockBreakEvent = new BlockEvent.BreakEvent(world, pos, blockState, player);
                    if (!MinecraftForge.EVENT_BUS.post(blockBreakEvent)) {
                        List<ItemStack> drops = Block.getDrops(blockState, world, pos, world.getBlockEntity(pos), null, ItemDummyPickAxe.getItemStack(silkTouch, fortune));
                        if (drops.size() == 0) {
                            // Remove the block if it dropped nothing (and will drop nothing)
                            if (breakOnNoDrops) {
                                removeBlock(blockState, player);
                            }
                            drops = Lists.newArrayList(ItemStack.EMPTY);
                        } else {
                            // Make sure there are no empty stacks in the list
                            drops.removeIf(ItemStack::isEmpty);
                        }
                        return cachedDrops = drops;
                    }
                }
            }
        }
        return Lists.newArrayList(ItemStack.EMPTY);
    }

    protected IBlockPlaceHandler getBlockPlaceHandler(ItemStack itemStack, World world, BlockPos pos, Direction side,
                                                      float hitX, float hitY, float hitZ, PlayerEntity player) {
        return IntegratedTunnels._instance.getRegistryManager().getRegistry(IBlockPlaceHandlerRegistry.class)
                .getHandler(itemStack, world, pos, side, hitX, hitY, hitZ, player);
    }

    protected ItemStack setItemStack(ItemStack itemStack, boolean simulate) {
        if (!itemStack.isEmpty() && itemStack.getCount() == 1) {
            Item item = itemStack.getItem();
            if (item instanceof BlockItem) {
                BlockItem itemBlock = (BlockItem) item;

                PlayerEntity player = PlayerHelpers.getFakePlayer(world);
                PlayerHelpers.setPlayerState(player, hand, pos, 0, 0, 0, side, false);

                IBlockPlaceHandler blockPlaceHandler = getBlockPlaceHandler(itemStack, world, pos, side.getOpposite(),
                        0, 0, 0, player);
                if (blockPlaceHandler != null) {
                    blockPlaceHandler.placeBlock(itemStack, world, pos, side.getOpposite(), 0, 0, 0, player);
                } else {
                    BlockItemUseContext blockItemUseContext = TunnelHelpers.createBlockItemUseContext(world, player, pos, side.getOpposite(), hand);
                    BlockState blockState = itemBlock.getBlock().getStateForPlacement(blockItemUseContext);
                    if (blockState != null && (simulate || itemBlock.placeBlock(blockItemUseContext, blockState))) {
                        if (!simulate) {
                            itemBlock.updateCustomBlockEntityTag(pos, world, blockItemUseContext.getPlayer(), itemStack, blockState);
                            itemBlock.getBlock().setPlacedBy(world, pos, blockState, player, itemStack);
                            if (GeneralConfig.worldInteractionEvents) {
                                SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
                                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F); // Sound
                            }
                            if (blockUpdate) {
                                sendBlockUpdate();
                            }
                        }
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return itemStack;
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
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
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
        if (!setItemStack(remaining.split(1), simulate).isEmpty()) {
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
            PlayerEntity player = PlayerHelpers.getFakePlayer(world);
            player.startUsingItem(hand);
            removeBlock(blockState, player);
        }
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
        Integer quantityFlag = getComponent().getPrimaryQuantifier().getMatchCondition();
        Integer subMatchCondition = matcher.withoutCondition(matchCondition, quantityFlag);
        List<ItemStack> itemStacks = getItemStacks();
        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ListIterator<ItemStack> it = itemStacks.listIterator();
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            if (matcher.matches(prototype, itemStack, subMatchCondition)
                    && (!matcher.hasCondition(matchCondition, quantityFlag) || itemStack.getCount() >= prototype.getCount())) {
                itemStack = itemStack.copy();
                ItemStack ret = itemStack.split(Helpers.castSafe(prototype.getCount()));
                if (!simulate) {
                    if (itemStack.isEmpty()) {
                        it.remove();
                    } else {
                        it.set(itemStack);
                    }
                }

                // Check if all items have been extracted, if so, remove block
                if (!simulate) {
                    this.extracted = true;
                    postExtract();
                }

                return ret;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        List<ItemStack> itemStacks = getItemStacks();
        if (itemStacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = itemStacks.get(0);
        itemStack = itemStack.copy();
        ItemStack ret = itemStack.split(Helpers.castSafe(maxQuantity));
        if (!simulate) {
            if (itemStack.isEmpty()) {
                itemStacks.remove(0);
            } else {
                itemStacks.set(0, itemStack);
            }
        }

        // Check if all items have been extracted, if so, remove block
        if (!simulate) {
            this.extracted = true;
            postExtract();
        }

        return ret;
    }
}
