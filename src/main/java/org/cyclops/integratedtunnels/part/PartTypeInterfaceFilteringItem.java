package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * Interface for filtering item handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringItem extends PartTypeInterfacePositionedAddonFiltering<IItemNetwork, IItemHandler, PartTypeInterfaceFilteringItem, PartTypeInterfaceFilteringItem.State> {
    public PartTypeInterfaceFilteringItem(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER,
                TunnelAspects.Write.ItemFilter.ITEMSTACK_SET_FILTER,
                TunnelAspects.Write.ItemFilter.LIST_SET_FILTER,
                TunnelAspects.Write.ItemFilter.PREDICATE_SET_FILTER,
                TunnelAspects.Write.ItemFilter.NBT_SET_FILTER
        ));
    }

    @Override
    public Capability<IItemNetwork> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IItemHandler> getTargetCapability() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    protected PartTypeInterfaceFilteringItem.State constructDefaultState() {
        return new PartTypeInterfaceFilteringItem.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IItemNetwork, IItemHandler, PartTypeInterfaceFilteringItem, PartTypeInterfaceFilteringItem.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IItemHandler> getTargetCapability() {
            return ForgeCapabilities.ITEM_HANDLER;
        }

        @Override
        public IItemHandler getCapabilityInstance() {
            return new PartTypeInterfaceItem.ItemHandler(this);
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == Capabilities.SLOTLESS_ITEMHANDLER) {
                return LazyOptional.of(this::getCapabilityInstance).cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }
}
