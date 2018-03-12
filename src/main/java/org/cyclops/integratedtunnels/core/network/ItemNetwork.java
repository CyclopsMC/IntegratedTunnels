package org.cyclops.integratedtunnels.core.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.DefaultSlotlessItemHandlerWrapper;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetwork;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.TunnelItemHelpers;

import java.util.concurrent.TimeUnit;

/**
 * A network that can hold items.
 * @author rubensworks
 */
public class ItemNetwork extends PositionedAddonsNetwork implements IItemNetwork, IInventoryState {

    // IItemHandler's are only cached for a tick, because we can't assure that they will still be there next tick.
    // Caching is important because IItemHandler's can possibly be looked up many times per tick, when actively exporting for example.
    private static final Cache<PartPos, IItemHandler> CACHE_ITEMHANDLER = CacheBuilder.newBuilder()
            .weakValues().expireAfterAccess(1000 / MinecraftHelpers.SECOND_IN_TICKS, TimeUnit.MILLISECONDS).build();
    private static final Cache<PartPos, ISlotlessItemHandler> CACHE_SLOTLESSITEMHANDLER = CacheBuilder.newBuilder()
            .weakValues().expireAfterAccess(1000 / MinecraftHelpers.SECOND_IN_TICKS, TimeUnit.MILLISECONDS).build();

    protected IItemHandler getItemHandler(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
        IItemHandler itemHandler = CACHE_ITEMHANDLER.getIfPresent(pos.getPartPos());
        if (itemHandler == null) {
            itemHandler = TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (itemHandler != null) {
                CACHE_ITEMHANDLER.put(pos.getPartPos(), itemHandler);
            }
        }
        return itemHandler;
    }

    protected ISlotlessItemHandler getSlotlessItemHandler(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
        ISlotlessItemHandler slotlessItemHandler = CACHE_SLOTLESSITEMHANDLER.getIfPresent(pos.getPartPos());
        if (slotlessItemHandler == null) {
            slotlessItemHandler = TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), Capabilities.SLOTLESS_ITEMHANDLER);
            if (slotlessItemHandler == null) {
                IItemHandler itemHandler = getItemHandler(pos);
                if (itemHandler != null) {
                    slotlessItemHandler = new DefaultSlotlessItemHandlerWrapper(itemHandler);
                }
            }
            CACHE_SLOTLESSITEMHANDLER.put(pos.getPartPos(), slotlessItemHandler);
        }
        return slotlessItemHandler;
    }

    protected IInventoryState getInventoryState(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
        return TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), Capabilities.INVENTORY_STATE);
    }

    @Override
    public boolean addPosition(PartPos pos, int priority, int channel) {
        IItemHandler itemHandler = TileHelpers.getCapability(pos.getPos(), pos.getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        return itemHandler != null && super.addPosition(pos, priority, channel);
    }

    @Override
    public int getHash() {
        int hash = 0;
        int i = 0;
        // TODO: reimplement using an observer
        for(PrioritizedPartPos partPos : getPositions()) {
            IItemHandler itemHandler = getItemHandler(partPos);
            if (itemHandler != null) {
                hash += TunnelItemHelpers.calculateInventoryState(itemHandler, getInventoryState(partPos)) + i++;
            }
        }
        return hash;
    }

    @Override
    public IItemChannel getChannel(int channel) {
        return new ItemChannel(this, channel);
    }
}
