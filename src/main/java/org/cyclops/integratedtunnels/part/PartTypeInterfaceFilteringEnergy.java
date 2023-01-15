package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * Interface for filtering energy storages.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringEnergy extends PartTypeInterfacePositionedAddonFiltering<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceFilteringEnergy, PartTypeInterfaceFilteringEnergy.State> {
    public PartTypeInterfaceFilteringEnergy(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER
        ));
    }

    @Override
    public Capability<IEnergyNetwork> getNetworkCapability() {
        return Capabilities.NETWORK_ENERGY;
    }

    @Override
    public Capability<IEnergyStorage> getTargetCapability() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public LazyOptional<IEnergyStorage> getTargetCapabilityInstance(PartPos pos) {
        return EnergyHelpers.getEnergyStorage(pos);
    }

    @Override
    protected PartTypeInterfaceFilteringEnergy.State constructDefaultState() {
        return new PartTypeInterfaceFilteringEnergy.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceEnergyBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceFilteringEnergy, PartTypeInterfaceFilteringEnergy.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IEnergyStorage> getTargetCapability() {
            return ForgeCapabilities.ENERGY;
        }

        @Override
        public IEnergyStorage getCapabilityInstance() {
            return new PartTypeInterfaceEnergy.EnergyStorage(this);
        }
    }
}
