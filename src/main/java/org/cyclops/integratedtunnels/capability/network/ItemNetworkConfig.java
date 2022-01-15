package org.cyclops.integratedtunnels.capability.network;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.commoncapabilities.CommonCapabilities;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;

/**
 * Config for the item network capability.
 * @author rubensworks
 *
 */
public class ItemNetworkConfig extends CapabilityConfig<IItemNetwork> {

    public static Capability<IItemNetwork> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public ItemNetworkConfig() {
        super(
                CommonCapabilities._instance,
                "itemNetwork",
                IItemNetwork.class
        );
    }

}
