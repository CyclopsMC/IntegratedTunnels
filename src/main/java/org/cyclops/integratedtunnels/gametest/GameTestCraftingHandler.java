package org.cyclops.integratedtunnels.gametest;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkCraftingHandler;
import org.cyclops.integrateddynamics.api.network.INetworkCraftingHandlerRegistry;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;

import java.util.Collections;
import java.util.List;

/**
 * A crafting handler for game tests that only handles {@link #CRAFTABLE_ITEM}.
 * Just like crafting handlers that are not transaction-aware (such as Integrated Crafting),
 * it opens a root transaction when crafting is requested.
 */
public class GameTestCraftingHandler implements INetworkCraftingHandler {

    public static final ItemStack CRAFTABLE_ITEM = new ItemStack(Items.DRAGON_EGG);

    private static GameTestCraftingHandler INSTANCE;

    private final List<Object> requestedInstances = Collections.synchronizedList(Lists.newArrayList());

    /**
     * @return The crafting handler, which is registered upon first usage.
     */
    public static synchronized GameTestCraftingHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = IntegratedDynamics._instance.getRegistryManager().getRegistry(INetworkCraftingHandlerRegistry.class)
                    .register(new GameTestCraftingHandler());
        }
        return INSTANCE;
    }

    /**
     * @return The instances for which crafting was requested.
     */
    public List<Object> getRequestedInstances() {
        return requestedInstances;
    }

    protected boolean handles(Object instance) {
        return instance instanceof ItemStack itemStack && ItemStack.isSameItem(itemStack, CRAFTABLE_ITEM);
    }

    @Override
    public <T, M> boolean isCrafting(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork, int channel,
                                     IngredientComponent<T, M> ingredientComponent, T instance, M matchCondition) {
        return false;
    }

    @Override
    public <T, M> boolean canCraft(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork, int channel) {
        return true;
    }

    @Override
    public <T, M> boolean craft(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork, int channel,
                                IngredientComponent<T, M> ingredientComponent, T instance, M matchCondition,
                                boolean ignoreExistingJobs) {
        if (!handles(instance)) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            requestedInstances.add(instance);
        }
        return true;
    }
}
