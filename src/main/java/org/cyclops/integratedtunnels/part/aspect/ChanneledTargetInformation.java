package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integratedtunnels.core.IngredientPredicate;

/**
 * A data holder for channel target information.
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class ChanneledTargetInformation<T, M> {

    private final IngredientPredicate<T, M> fluidStackPredicate;
    private final int transferHash;
    private final int slot;

    protected ChanneledTargetInformation(IngredientPredicate<T, M> fluidStackPredicate, int transferHash, int slot) {
        this.fluidStackPredicate = fluidStackPredicate;
        this.transferHash = transferHash;
        this.slot = slot;
    }

    public static <T, M> ChanneledTargetInformation<T, M> of(IngredientPredicate<T, M> fluidStackPredicate, int transferHash, int slot) {
        return new ChanneledTargetInformation<>(fluidStackPredicate, transferHash, slot);
    }

    public IngredientPredicate<T, M> getIngredientPredicate() {
        return fluidStackPredicate;
    }

    public int getTransferHash() {
        return transferHash;
    }

    public int getSlot() {
        return slot;
    }
}
