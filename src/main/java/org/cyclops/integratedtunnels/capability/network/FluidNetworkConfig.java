package org.cyclops.integratedtunnels.capability.network;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.commoncapabilities.CommonCapabilities;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityStorage;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.network.FluidNetwork;

/**
 * Config for the item network capability.
 * @author rubensworks
 *
 */
public class FluidNetworkConfig extends CapabilityConfig<IFluidNetwork> {

    @CapabilityInject(IFluidNetwork.class)
    public static Capability<IFluidNetwork> CAPABILITY = null;

    public FluidNetworkConfig() {
        super(
                CommonCapabilities._instance,
                "fluidNetwork",
                IFluidNetwork.class,
                new DefaultCapabilityStorage<IFluidNetwork>(),
                () -> new FluidNetwork(null)
        );
    }

}
