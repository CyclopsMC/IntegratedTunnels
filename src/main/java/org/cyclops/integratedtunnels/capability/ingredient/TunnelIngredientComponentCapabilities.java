package org.cyclops.integratedtunnels.capability.ingredient;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.capability.ingredient.IngredientComponentCapabilities;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;

/**
 * Value handlers for ingredient components.
 * @author rubensworks
 */
public class TunnelIngredientComponentCapabilities {

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Network handler
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ItemStack, Integer>(IngredientComponentCapabilities.INGREDIENT_ITEMSTACK_NAME, org.cyclops.integrateddynamics.Capabilities.PositionedAddonsNetworkIngredientsHandler.INGREDIENT) {
            @Override
            public ICapabilityProvider<INetwork, Void, IItemNetwork> createCapabilityProvider(IngredientComponent<ItemStack, Integer> ingredientComponent) {
                return (network, context) -> network.getCapability(Capabilities.ItemNetwork.NETWORK).orElse(null);
            }
        });
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<FluidStack, Integer>(IngredientComponentCapabilities.INGREDIENT_FLUIDSTACK_NAME, org.cyclops.integrateddynamics.Capabilities.PositionedAddonsNetworkIngredientsHandler.INGREDIENT) {
            @Override
            public ICapabilityProvider<INetwork, Void, IFluidNetwork> createCapabilityProvider(IngredientComponent<FluidStack, Integer> ingredientComponent) {
                return (network, context) -> network.getCapability(Capabilities.FluidNetwork.NETWORK).orElse(null);
            }
        });
    }

}
