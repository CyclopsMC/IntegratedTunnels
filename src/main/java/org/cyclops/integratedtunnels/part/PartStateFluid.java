package org.cyclops.integratedtunnels.part;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.TunnelHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A part state for handling fluid import and export.
 * It also acts as an fluid capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateFluid<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IFluidNetwork, FluidStack> implements ResourceHandler<FluidResource> {

    public PartStateFluid(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T> Optional<T> getCapability(P partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == Capabilities.Fluid.PART) {
            return Optional.of((T) this);
        }
        return super.getCapability(partType, capability, network, partNetwork, target);
    }

    protected ResourceHandler<FluidResource> getFluidHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK, TunnelHelpers.getPassiveInteractionChannel(this));
    }

    @Override
    public int size() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getFluidHandler().size() : 0;
    }

    @Nonnull
    @Override
    public FluidResource getResource(int tank) {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            ResourceHandler<FluidResource> fh = getFluidHandler();
            FluidResource resource = fh.getResource(tank);
            FluidStack fluidStack = resource.toStack(fh.getAmountAsInt(tank));
            if (getStorageFilter().testView(fluidStack)) {
                return resource;
            }
        }
        return FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int tank) {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            ResourceHandler<FluidResource> fh = getFluidHandler();
            FluidResource resource = fh.getResource(tank);
            long amount = fh.getAmountAsLong(tank);
            FluidStack fluidStack = resource.toStack((int) amount);
            if (getStorageFilter().testView(fluidStack)) {
                return amount;
            }
        }
        return 0;
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource fluidResource) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getFluidHandler().getCapacityAsLong(tank, fluidResource) : 0;
    }

    @Override
    public boolean isValid(int tank, FluidResource fluidResource) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(fluidResource.toStack(1)) && getFluidHandler().isValid(tank, fluidResource);
    }

    protected int rateLimit(int amount) {
        return Math.min(amount, GeneralConfig.fluidRateLimit);
    }

    @Override
    public int insert(int tank, FluidResource resource, int amount, TransactionContext transaction) {
        return canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(resource.toStack(amount)) ? getFluidHandler().insert(tank, resource, rateLimit(amount), transaction) : 0;
    }

    @Override
    public int insert(FluidResource resource, int amount, TransactionContext transaction) {
        return canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(resource.toStack(amount)) ? getFluidHandler().insert(resource, rateLimit(amount), transaction) : 0;
    }

    @Override
    public int extract(int tank, FluidResource resource, int amount, TransactionContext transaction) {
        return canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testExtraction(resource.toStack(amount)) ? getFluidHandler().extract(tank, resource, rateLimit(amount), transaction) : 0;
    }

    @Override
    public int extract(FluidResource resource, int amount, TransactionContext transaction) {
        return canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testExtraction(resource.toStack(amount)) ? getFluidHandler().extract(resource, rateLimit(amount), transaction) : 0;
    }
}
