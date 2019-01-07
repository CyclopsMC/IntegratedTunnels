package org.cyclops.integratedtunnels.core.predicate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.core.helper.NbtHelpers;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateFluidStackNbt extends IngredientPredicate<FluidStack, Integer> {
    private final boolean blacklist;
    private final boolean requireNbt;
    private final boolean subset;
    private final NBTTagCompound tag;
    private final boolean recursive;
    private final boolean superset;

    public IngredientPredicateFluidStackNbt(boolean blacklist, int amount, boolean exactAmount, boolean requireNbt, boolean subset, NBTTagCompound tag, boolean recursive, boolean superset) {
        super(IngredientComponent.FLUIDSTACK, blacklist, false, amount, exactAmount);
        this.blacklist = blacklist;
        this.requireNbt = requireNbt;
        this.subset = subset;
        this.tag = tag;
        this.recursive = recursive;
        this.superset = superset;
    }

    @Override
    public boolean test(@Nullable FluidStack input) {
        if (input.tag != null && requireNbt) {
            return isBlacklist();
        }
        NBTTagCompound itemTag = input.tag != null ? input.tag : new NBTTagCompound();
        boolean ret = (!subset || NbtHelpers.nbtMatchesSubset(tag, itemTag, recursive))
                && (!superset || NbtHelpers.nbtMatchesSubset(itemTag, tag, recursive));
        if (blacklist) {
            ret = !ret;
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateFluidStackNbt)) {
            return false;
        }
        IngredientPredicateFluidStackNbt that = (IngredientPredicateFluidStackNbt) obj;
        return super.equals(obj)
                && this.blacklist == that.blacklist
                && this.requireNbt == that.requireNbt
                && this.subset == that.subset
                && this.tag.equals(that.tag)
                && this.recursive == that.recursive
                && this.superset == that.superset;
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                ^ (this.blacklist ? 1 : 0) << 1
                ^ (this.requireNbt ? 1 : 0) << 2
                ^ (this.subset ? 1 : 0) << 3
                ^ this.tag.hashCode() << 4
                ^ (this.recursive ? 1 : 0) << 5
                ^ (this.superset ? 1 : 0) << 6;
    }
}
