package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import java.util.Optional;

/**
 * Interface for filtering item handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringItem extends PartTypeInterfacePositionedAddonFiltering<IItemNetwork, ResourceHandler<ItemResource>, PartTypeInterfaceFilteringItem, PartTypeInterfaceFilteringItem.State> {
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
    public NetworkCapability<IItemNetwork> getNetworkCapability() {
        return Capabilities.ItemNetwork.NETWORK;
    }

    @Override
    public PartCapability<ResourceHandler<ItemResource>> getPartCapability() {
        return Capabilities.Item.PART;
    }

    @Override
    public BlockCapability<ResourceHandler<ItemResource>, Direction> getBlockCapability() {
        return net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK;
    }

    @Override
    protected PartTypeInterfaceFilteringItem.State constructDefaultState() {
        return new PartTypeInterfaceFilteringItem.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IItemNetwork, ResourceHandler<ItemResource>, PartTypeInterfaceFilteringItem, PartTypeInterfaceFilteringItem.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public PartCapability<ResourceHandler<ItemResource>> getTargetCapability() {
            return Capabilities.Item.PART;
        }

        @Override
        public ResourceHandler<ItemResource> getCapabilityInstance() {
            return new PartTypeInterfaceItem.ItemHandler(this);
        }

        @Override
        public <T> Optional<T> getCapability(PartTypeInterfaceFilteringItem partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == Capabilities.Item.PART) {
                return Optional.of((T) this.getCapabilityInstance());
            }
            return super.getCapability(partType, capability, network, partNetwork, target);
        }
    }
}
