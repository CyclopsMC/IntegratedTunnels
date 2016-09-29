package org.cyclops.integratedtunnels.capability.network;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.commoncapabilities.CommonCapabilities;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityStorage;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.network.ItemNetwork;

/**
 * Config for the item network capability.
 * @author rubensworks
 *
 */
public class ItemNetworkConfig extends CapabilityConfig {

    /**
     * The unique instance.
     */
    public static ItemNetworkConfig _instance;

    @CapabilityInject(IItemNetwork.class)
    public static Capability<IItemNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public ItemNetworkConfig() {
        super(
                CommonCapabilities._instance,
                true,
                "itemNetwork",
                "A capability for networks that can hold items.",
                IItemNetwork.class,
                new DefaultCapabilityStorage<IItemNetwork>(),
                ItemNetwork.class
        );
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
