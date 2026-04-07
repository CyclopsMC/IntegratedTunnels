package org.cyclops.integratedtunnels.core.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;

/**
 * A block break handler for shulker boxes.
 * @author rubensworks
 */
public class BlockBreakHandlerShulkerBox implements IBlockBreakHandler {
    @Override
    public boolean shouldApply(BlockState blockState, Level world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getDrops(BlockState blockState, Level world, BlockPos pos, Player player) {
        return IModHelpers.get().getBlockEntityHelpers().get(world, pos, ShulkerBoxBlockEntity.class)
                .map(tile -> {
                    if (!tile.isEmpty()) {
                        ItemStack itemStack = new ItemStack(blockState.getBlock());
                        BlockItem.updateCustomBlockEntityTag(world, player, pos, itemStack);

                        if (tile.hasCustomName()) {
                            itemStack.set(DataComponents.CUSTOM_NAME, tile.getName());
                        }

                        NonNullList<ItemStack> list = NonNullList.create();
                        list.add(itemStack);
                        return list;
                    }
                    return NonNullList.<ItemStack>create();
                })
                .orElseGet(NonNullList::create);
    }

    @Override
    public void breakBlock(BlockState blockState, Level world, BlockPos pos, Player player) {
        IModHelpers.get().getBlockEntityHelpers().get(world, pos, ShulkerBoxBlockEntity.class)
                .ifPresent(RandomizableContainerBlockEntity::clearContent);
        blockState.getBlock().onDestroyedByPlayer(blockState, world, pos, player, player.getItemInHand(player.getUsedItemHand()), false, world.getFluidState(pos));
    }
}
