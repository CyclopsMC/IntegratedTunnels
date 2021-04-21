package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
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
public class PartTypeInterfaceItemFiltering extends PartTypeInterfacePositionedAddonFiltering<IItemNetwork, IItemHandler, PartTypeInterfaceItemFiltering, PartTypeInterfaceItemFiltering.State> {
    public PartTypeInterfaceItemFiltering(String name) {
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
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    protected PartTypeInterfaceItemFiltering.State constructDefaultState() {
        return new PartTypeInterfaceItemFiltering.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }
    
    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<PartTypeInterfaceItemFiltering, IItemNetwork, IItemHandler, PartTypeInterfaceItemFiltering.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IItemHandler> getTargetCapability() {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == Capabilities.SLOTLESS_ITEMHANDLER) {
                return LazyOptional.of(() -> new PartTypeInterfaceItem.ItemHandler(this)).cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }
}
