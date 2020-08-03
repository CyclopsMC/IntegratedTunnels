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
public class ItemNetworkConfig extends CapabilityConfig<IItemNetwork> {

    @CapabilityInject(IItemNetwork.class)
    public static Capability<IItemNetwork> CAPABILITY = null;

    public ItemNetworkConfig() {
        super(
                CommonCapabilities._instance,
                "itemNetwork",
                IItemNetwork.class,
                new DefaultCapabilityStorage<IItemNetwork>(),
                () -> new ItemNetwork(null)
        );
    }

}
