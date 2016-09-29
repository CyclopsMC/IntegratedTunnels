package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.integrateddynamics.core.part.PartStateEmpty;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

/**
 * Interface for item handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceItem extends PartTypeInterfacePositionedAddon<IItemNetwork, IItemHandler, PartTypeInterfaceItem, PartStateEmpty<PartTypeInterfaceItem>> {
    public PartTypeInterfaceItem(String name) {
        super(name);
    }

    @Override
    protected Capability<IItemNetwork> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    @Override
    protected Capability<IItemHandler> getTargetCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    protected PartStateEmpty<PartTypeInterfaceItem> constructDefaultState() {
        return new PartStateEmpty<PartTypeInterfaceItem>();
    }
}
