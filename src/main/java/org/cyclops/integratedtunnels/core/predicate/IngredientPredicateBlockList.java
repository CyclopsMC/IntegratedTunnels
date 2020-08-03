package org.cyclops.integratedtunnels.core.predicate;

import net.minecraft.item.ItemStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.helper.BlockHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import org.cyclops.integratedtunnels.core.TunnelItemHelpers;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateBlockList extends IngredientPredicate<ItemStack, Integer> {
    private final boolean blacklist;
    private final IValueTypeListProxy<ValueObjectTypeBlock, ValueObjectTypeBlock.ValueBlock> blocks;
    private final boolean checkStackSize;
    private final boolean checkItem;
    private final boolean checkNbt;

    public IngredientPredicateBlockList(boolean blacklist, int amount, boolean exactAmount, IValueTypeListProxy<ValueObjectTypeBlock, ValueObjectTypeBlock.ValueBlock> blocks, boolean checkStackSize, boolean checkItem, boolean checkNbt) {
        super(IngredientComponent.ITEMSTACK, blacklist, false, amount, exactAmount);
        this.blacklist = blacklist;
        this.blocks = blocks;
        this.checkStackSize = checkStackSize;
        this.checkItem = checkItem;
        this.checkNbt = checkNbt;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        for (ValueObjectTypeBlock.ValueBlock block : blocks) {
            if (block.getRawValue().isPresent()
                    && TunnelItemHelpers.areItemStackEqual(input, BlockHelpers.getItemStackFromBlockState(block.getRawValue().get()), checkStackSize, checkItem, checkNbt)) {
                return !blacklist;
            }
        }
        return blacklist;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateBlockList)) {
            return false;
        }
        IngredientPredicateBlockList that = (IngredientPredicateBlockList) obj;
        return super.equals(obj)
                && this.blacklist == that.blacklist
                && this.checkItem == that.checkItem
                && this.checkStackSize == that.checkStackSize
                && this.checkNbt == that.checkNbt
                && this.blocks.equals(that.blocks);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                ^ (this.blacklist ? 1 : 0) << 1
                ^ (this.checkItem ? 1 : 0) << 2
                ^ (this.checkStackSize ? 1 : 0) << 3
                ^ (this.checkNbt ? 1 : 0) << 4
                ^ this.blocks.hashCode();
    }
}
