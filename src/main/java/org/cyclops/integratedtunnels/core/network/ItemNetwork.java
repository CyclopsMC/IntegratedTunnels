package org.cyclops.integratedtunnels.core.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.TunnelItemHelpers;

import java.util.concurrent.TimeUnit;

/**
 * A network that can hold items.
 * @author rubensworks
 */
public class ItemNetwork extends PositionedAddonsNetworkIngredients<ItemStack, Integer>
        implements IItemNetwork, IInventoryState {

    // IItemHandler's are only cached for a tick, because we can't assure that they will still be there next tick.
    // Caching is important because IItemHandler's can possibly be looked up many times per tick, when actively exporting for example.
    private static final Cache<PartPos, IItemHandler> CACHE_ITEMHANDLER = CacheBuilder.newBuilder()
            .weakValues().expireAfterAccess(1000 / MinecraftHelpers.SECOND_IN_TICKS, TimeUnit.MILLISECONDS).build();

    public ItemNetwork(IngredientComponent<ItemStack, Integer> component) {
        super(component);
    }

    protected IItemHandler getItemHandler(PartPos pos) {
        if (isPositionDisabled(pos)) {
            return null;
        }
        IItemHandler itemHandler = CACHE_ITEMHANDLER.getIfPresent(pos);
        if (itemHandler == null) {
            itemHandler = TileHelpers.getCapability(pos.getPos(), pos.getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (itemHandler != null) {
                CACHE_ITEMHANDLER.put(pos, itemHandler);
            }
        }
        return itemHandler;
    }

    protected IInventoryState getInventoryState(PartPos pos) {
        if (isPositionDisabled(pos)) {
            return null;
        }
        return TileHelpers.getCapability(pos.getPos(), pos.getSide(), Capabilities.INVENTORY_STATE);
    }

    @Override
    public int getHash() {
        int hash = 0;
        int i = 0;
        for(PartPos partPos : getPositions()) {
            IItemHandler itemHandler = getItemHandler(partPos);
            if (itemHandler != null) {
                hash += TunnelItemHelpers.calculateInventoryState(itemHandler, getInventoryState(partPos)) + i++;
            }
        }
        return hash;
    }

    @Override
    public long getRateLimit() {
        return 64;
    }
}
