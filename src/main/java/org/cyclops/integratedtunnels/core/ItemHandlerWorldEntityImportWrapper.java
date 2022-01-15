package org.cyclops.integratedtunnels.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.Helpers;
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

    public ItemHandlerWorldEntityImportWrapper(ServerLevel world, BlockPos pos, Direction facing, final boolean ignorePickupDelay) {
        this(world, pos, facing, new AABB(pos), ignorePickupDelay);
    }

    public ItemHandlerWorldEntityImportWrapper(ServerLevel world, BlockPos pos, Direction facing, AABB area, final boolean ignorePickupDelay) {
        this.world = world;
        this.pos = pos;
        this.facing = facing;
        this.entities = world.getEntitiesOfClass(ItemEntity.class, area,
                input -> (ignorePickupDelay || !input.hasPickUpDelay()) && input.isAlive());
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
    public ItemStack insert(@Nonnull ItemStack ingredient, boolean simulate) {
        return ItemStack.EMPTY;
    }

    protected void postExtract(ItemEntity entity, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        } else {
            entity.setItem(itemStack);
        }
        if (GeneralConfig.worldInteractionEvents) {
            world.levelEvent(1000, pos, 0); // Sound
            world.levelEvent(2000, pos.relative(facing.getOpposite()), facing.get3DDataValue()); // Particles
        }
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
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
                ItemStack ret = itemStack.split(Helpers.castSafe(prototype.getCount()));

                // Check if all items have been extracted, if so, remove block
                if (!simulate) {
                    postExtract(entity, itemStack);
                }

                return ret;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        if (this.entities.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemEntity entity = this.entities.get(0);
        ItemStack itemStack = entity.getItem();
        itemStack = itemStack.copy();
        ItemStack ret = itemStack.split(Helpers.castSafe(maxQuantity));
        if (!simulate) {
            postExtract(entity, itemStack);
        }

        return ret;
    }
}
