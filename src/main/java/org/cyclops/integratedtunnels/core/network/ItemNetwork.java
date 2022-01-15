package org.cyclops.integratedtunnels.core.network;

import net.minecraft.world.item.ItemStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;

/**
 * A network that can hold items.
 * @author rubensworks
 */
public class ItemNetwork extends PositionedAddonsNetworkIngredients<ItemStack, Integer> implements IItemNetwork {

    public ItemNetwork(IngredientComponent<ItemStack, Integer> component) {
        super(component);
    }

    @Override
    public long getRateLimit() {
        return 64;
    }
}
