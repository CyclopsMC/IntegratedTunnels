package org.cyclops.integratedtunnels.capability.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherAdapter;
import org.cyclops.commoncapabilities.api.ingredient.capability.IngredientComponentCapabilityAttacherManager;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.capability.ingredient.IngredientComponentCapabilities;
import org.cyclops.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;

/**
 * Value handlers for ingredient components.
 * @author rubensworks
 */
public class TunnelIngredientComponentCapabilities {

    public static void load() {
        IngredientComponentCapabilityAttacherManager attacherManager = new IngredientComponentCapabilityAttacherManager();

        // Network handler
        ResourceLocation networkHandler = new ResourceLocation(Reference.MOD_ID, "networkHandler");
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<ItemStack, Integer>(IngredientComponentCapabilities.INGREDIENT_ITEMSTACK_NAME, networkHandler) {
            @Override
            public ICapabilityProvider createCapabilityProvider(IngredientComponent<ItemStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(() -> PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY,
                        (network) -> network.getCapability(ItemNetworkConfig.CAPABILITY));
            }
        });
        attacherManager.addAttacher(new IngredientComponentCapabilityAttacherAdapter<FluidStack, Integer>(IngredientComponentCapabilities.INGREDIENT_FLUIDSTACK_NAME, networkHandler) {
            @Override
            public ICapabilityProvider createCapabilityProvider(IngredientComponent<FluidStack, Integer> ingredientComponent) {
                return new DefaultCapabilityProvider<>(() -> PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY,
                        (network) -> network.getCapability(FluidNetworkConfig.CAPABILITY));
            }
        });
    }

}
