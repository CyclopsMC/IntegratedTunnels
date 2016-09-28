package org.cyclops.integratedtunnels.part;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.part.PartStateEmpty;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnel;

/**
 * @author rubensworks
 */
public class PartTypeInterfaceEnergy extends PartTypeTunnel<PartTypeInterfaceEnergy, PartStateEmpty<PartTypeInterfaceEnergy>> {
    public PartTypeInterfaceEnergy(String name) {
        super(name);
    }

    @Override
    protected PartStateEmpty<PartTypeInterfaceEnergy> constructDefaultState() {
        return new PartStateEmpty<PartTypeInterfaceEnergy>();
    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEmpty<PartTypeInterfaceEnergy> state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetBatteryToNetwork(network, target.getTarget(), state.getPriority());
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEmpty<PartTypeInterfaceEnergy> state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        removeTargetBatteryFromNetwork(network, target.getTarget());
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEmpty<PartTypeInterfaceEnergy> state, IBlockAccess world, Block neighborBlock) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighborBlock);
        removeTargetBatteryFromNetwork(network, target.getTarget());
        addTargetBatteryToNetwork(network, target.getTarget(), state.getPriority());
    }

    @Override
    public void setPriority(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEmpty<PartTypeInterfaceEnergy> state, int priority) {
        // We need to do this because the energy network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetBatteryFromNetwork(network, target.getTarget());
        super.setPriority(network, partNetwork, target, state, priority);
        addTargetBatteryToNetwork(network, target.getTarget(), priority);
    }

    protected void addTargetBatteryToNetwork(INetwork network, PartPos pos, int priority) {
        if (network.hasCapability(Capabilities.NETWORK_ENERGY)) {
            IEnergyStorage energyStorage = TileHelpers.getCapability(pos.getPos(), CapabilityEnergy.ENERGY);
            if (energyStorage != null && energyStorage.canExtract() && energyStorage.canReceive()) {
                IEnergyNetwork energyNetwork = network.getCapability(Capabilities.NETWORK_ENERGY);
                energyNetwork.addEnergyBattery(pos, priority);
            }
        }
    }

    protected void removeTargetBatteryFromNetwork(INetwork network, PartPos pos) {
        if (network.hasCapability(Capabilities.NETWORK_ENERGY)) {
            IEnergyNetwork energyNetwork = network.getCapability(Capabilities.NETWORK_ENERGY);
            energyNetwork.removeEnergyBattery(pos);
        }
    }
}
