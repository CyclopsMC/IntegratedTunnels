package org.cyclops.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;

import javax.annotation.Nullable;

/**
 * Interface for positioned network addons.
 * @author rubensworks
 */
public abstract class PartTypeInterfacePositionedAddon<N extends IPositionedAddonsNetwork, T, P extends IPartType<P, S>, S extends IPartState<P>> extends PartTypeTunnel<P, S> {
    public PartTypeInterfacePositionedAddon(String name) {
        super(name);
    }

    protected abstract Capability<N> getNetworkCapability();
    protected abstract Capability<T> getTargetCapability();
    protected boolean isTargetCapabilityValid(T capability) {
        return capability != null;
    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target.getTarget(), state.getPriority());
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        removeTargetFromNetwork(network, target.getTarget());
    }

    @Override
    public void onBlockNeighborChange(@Nullable INetwork network, @Nullable IPartNetwork partNetwork, PartTarget target, S state, IBlockAccess world, Block neighborBlock) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighborBlock);
        if (network != null) {
            removeTargetFromNetwork(network, target.getTarget());
            addTargetToNetwork(network, target.getTarget(), state.getPriority());
        }
    }

    @Override
    public void setPriority(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, int priority) {
        // We need to do this because the energy network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetFromNetwork(network, target.getTarget());
        super.setPriority(network, partNetwork, target, state, priority);
        addTargetToNetwork(network, target.getTarget(), priority);
    }

    protected void addTargetToNetwork(INetwork network, PartPos pos, int priority) {
        if (network.hasCapability(getNetworkCapability())) {
            T capability = TileHelpers.getCapability(pos.getPos(), getTargetCapability());
            if (isTargetCapabilityValid(capability)) {
                N networkCapability = network.getCapability(getNetworkCapability());
                networkCapability.addPosition(pos, priority);
            }
        }
    }

    protected void removeTargetFromNetwork(INetwork network, PartPos pos) {
        if (network.hasCapability(getNetworkCapability())) {
            N networkCapability = network.getCapability(getNetworkCapability());
            networkCapability.removePosition(pos);
        }
    }
}
