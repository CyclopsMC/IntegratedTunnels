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

    /**
     * The unique instance.
     */
    public static FluidNetworkConfig _instance;

    @CapabilityInject(IFluidNetwork.class)
    public static Capability<IFluidNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public FluidNetworkConfig() {
        super(
                CommonCapabilities._instance,
                true,
                "fluidNetwork",
                "A capability for networks that can hold fluids.",
                IFluidNetwork.class,
                new DefaultCapabilityStorage<IFluidNetwork>(),
                FluidNetwork.class
        );
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
