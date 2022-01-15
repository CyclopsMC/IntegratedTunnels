package org.cyclops.integratedtunnels.capability.network;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.commoncapabilities.CommonCapabilities;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;

/**
 * Config for the item network capability.
 * @author rubensworks
 *
 */
public class FluidNetworkConfig extends CapabilityConfig<IFluidNetwork> {

    public static Capability<IFluidNetwork> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public FluidNetworkConfig() {
        super(
                CommonCapabilities._instance,
                "fluidNetwork",
                IFluidNetwork.class
        );
    }

}
