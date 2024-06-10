package org.cyclops.integratedtunnels.capability.ingredient;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.api.ingredient.capability.IPositionedAddonsNetworkIngredientsHandler;
import org.cyclops.integrateddynamics.capability.ingredient.IngredientComponentCapabilities;
import org.cyclops.integratedtunnels.Capabilities;

import java.util.Optional;

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
            public ICapabilityProvider<IngredientComponent<ItemStack, Integer>, Void, IPositionedAddonsNetworkIngredientsHandler<ItemStack, Integer>> createCapabilityProvider(IngredientComponent<ItemStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(network -> (Optional) network.getCapability(Capabilities.ItemNetwork.NETWORK));
            }
        });
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<FluidStack, Integer>(IngredientComponentCapabilities.INGREDIENT_FLUIDSTACK_NAME, org.cyclops.integrateddynamics.Capabilities.PositionedAddonsNetworkIngredientsHandler.INGREDIENT) {
            @Override
            public ICapabilityProvider<IngredientComponent<FluidStack, Integer>, Void, IPositionedAddonsNetworkIngredientsHandler<FluidStack, Integer>> createCapabilityProvider(IngredientComponent<FluidStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(network -> (Optional) network.getCapability(Capabilities.FluidNetwork.NETWORK));
            }
        });
    }

}
