package org.cyclops.integratedtunnels.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.cyclopscore.ingredient.collection.FilteredIngredientCollectionIterator;
import org.cyclops.integratedtunnels.GeneralConfig;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * An item handler for importing item entities from the world.
 * @author rubensworks
 */
public class ItemHandlerWorldEntityImportWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private final ServerLevel world;
    private final BlockPos pos;
    private final Direction facing;
    private final List<ItemEntity> entities;
    private final ItemHandlerWorldEntityImportWrapper.Journal journal;

    public ItemHandlerWorldEntityImportWrapper(ServerLevel world, BlockPos pos, Direction facing, final boolean ignorePickupDelay) {
        this(world, pos, facing, new AABB(pos), ignorePickupDelay);
    }

    public ItemHandlerWorldEntityImportWrapper(ServerLevel world, BlockPos pos, Direction facing, AABB area, final boolean ignorePickupDelay) {
        this.world = world;
        this.pos = pos;
        this.facing = facing;
        this.entities = world.getEntitiesOfClass(ItemEntity.class, area,
                input -> (ignorePickupDelay || !input.hasPickUpDelay()) && input.isAlive());
        this.journal = new Journal();
    }

    public List<ItemEntity> getEntities() {
        return entities;
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return this.entities.stream().map(ItemEntity::getItem).iterator();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return new FilteredIngredientCollectionIterator<>(this, getComponent().getMatcher(), prototype, matchCondition);
    }

    @Override
    public long getMaxQuantity() {
        return 64 * entities.size();
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack ingredient, TransactionContext transaction) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, TransactionContext transaction) {
        IIngredientMatcher<ItemStack, Integer> matcher = getComponent().getMatcher();
        Integer quantityFlag = getComponent().getPrimaryQuantifier().getMatchCondition();
        Integer subMatchCondition = matcher.withoutCondition(matchCondition,
                getComponent().getPrimaryQuantifier().getMatchCondition());
        List<ItemEntity> entities = this.entities;
        if (entities.isEmpty()) {
            return ItemStack.EMPTY;
        }

        for (ItemEntity entity : entities) {
            ItemStack itemStack = entity.getItem();
            if (matcher.matches(prototype, itemStack, subMatchCondition)
                    && (!matcher.hasCondition(matchCondition, quantityFlag) || itemStack.getCount() >= prototype.getCount())) {
                itemStack = itemStack.copy();
                ItemStack ret = itemStack.split(IModHelpers.get().getBaseHelpers().castSafe(prototype.getCount()));
                this.journal.updateSnapshots(transaction);
                entity.setItem(itemStack);
                return ret;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, TransactionContext transaction) {
        if (this.entities.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemEntity entity = this.entities.get(0);
        ItemStack itemStack = entity.getItem();
        itemStack = itemStack.copy();
        ItemStack ret = itemStack.split(IModHelpers.get().getBaseHelpers().castSafe(maxQuantity));
        this.journal.updateSnapshots(transaction);
        entity.setItem(itemStack);
        return ret;
    }

    private class Journal extends SnapshotJournal<ItemStack> {

        @Override
        protected ItemStack createSnapshot() {
            return entities.get(0).getItem().copy();
        }

        @Override
        protected void revertToSnapshot(ItemStack itemStack) {
            entities.get(0).setItem(itemStack);
        }

        @Override
        protected void onRootCommit(ItemStack originalState) {
            super.onRootCommit(originalState);

            ItemEntity entity = entities.get(0);
            if (entity.getItem().isEmpty()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
            if (GeneralConfig.worldInteractionEvents) {
                world.levelEvent(1000, pos, 0); // Sound
                world.levelEvent(2000, pos.relative(facing.getOpposite()), facing.get3DDataValue()); // Particles
            }
        }
    }
}
