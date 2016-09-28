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
        addToNetwork(network, target.getTarget());
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEmpty<PartTypeInterfaceEnergy> state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        removeFromNetwork(network, target.getTarget());
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEmpty<PartTypeInterfaceEnergy> state, IBlockAccess world, Block neighborBlock) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighborBlock);
        removeFromNetwork(network, target.getTarget());
        addToNetwork(network, target.getTarget());
    }

    protected void addToNetwork(INetwork network, PartPos pos) {
        if (network.hasCapability(Capabilities.NETWORK_ENERGY)) {
            IEnergyStorage energyStorage = TileHelpers.getCapability(pos.getPos(), CapabilityEnergy.ENERGY);
            if (energyStorage != null && energyStorage.canExtract() && energyStorage.canReceive()) {
                IEnergyNetwork energyNetwork = network.getCapability(Capabilities.NETWORK_ENERGY);
                energyNetwork.addEnergyBattery(pos);
            }
        }
    }

    protected void removeFromNetwork(INetwork network, PartPos pos) {
        if (network.hasCapability(Capabilities.NETWORK_ENERGY)) {
            IEnergyNetwork energyNetwork = network.getCapability(Capabilities.NETWORK_ENERGY);
            energyNetwork.removeEnergyBattery(pos);
        }
    }
}
