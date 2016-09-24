package org.cyclops.integratedtunnels;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    @CapabilityInject(IEnergyNetwork.class)
    public static Capability<IEnergyNetwork> NETWORK_ENERGY = null;
}
