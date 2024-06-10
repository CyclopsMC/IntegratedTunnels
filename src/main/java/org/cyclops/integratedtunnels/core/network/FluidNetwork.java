package org.cyclops.integratedtunnels.core.network;

import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;

/**
 * A network that can hold fluids.
 * @author rubensworks
 */
public class FluidNetwork extends PositionedAddonsNetworkIngredients<FluidStack, Integer> implements IFluidNetwork {

    public FluidNetwork(IngredientComponent<FluidStack, Integer> component) {
        super(component);
    }

    @Override
    public long getRateLimit() {
        return GeneralConfig.fluidRateLimit;
    }
}
