package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

/**
 * A data holder for channel target information.
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class ChanneledTargetInformation<T, M> {

    private final IngredientPredicate<T, M> fluidStackPredicate;
    private final ITunnelTransfer transfer;
    private final int slot;

    protected ChanneledTargetInformation(IngredientPredicate<T, M> fluidStackPredicate, ITunnelTransfer transfer, int slot) {
        this.fluidStackPredicate = fluidStackPredicate;
        this.transfer = transfer;
        this.slot = slot;
    }

    public static <T, M> ChanneledTargetInformation<T, M> of(IngredientPredicate<T, M> fluidStackPredicate, ITunnelTransfer transfer, int slot) {
        return new ChanneledTargetInformation<>(fluidStackPredicate, transfer, slot);
    }

    public IngredientPredicate<T, M> getIngredientPredicate() {
        return fluidStackPredicate;
    }

    public ITunnelTransfer getTransfer() {
        return transfer;
    }

    public int getSlot() {
        return slot;
    }
}
