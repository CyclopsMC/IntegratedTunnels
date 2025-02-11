package org.cyclops.integratedtunnels.core.part;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.integratedtunnels.IntegratedTunnels;

/**
 * Config for {@link ContainerInterfaceSettings}.
 * @author rubensworks
 */
public class ContainerInterfaceSettingsConfig extends GuiConfigCommon<ContainerInterfaceSettings, IModBase> {

    public ContainerInterfaceSettingsConfig() {
        super(IntegratedTunnels._instance,
                "part_interface_settings",
                eConfig -> new ContainerTypeData<>(ContainerInterfaceSettings::new, FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerInterfaceSettings> getScreenFactoryProvider() {
        return new ContainerInterfaceSettingsConfigScreenFactoryProvider();
    }
}
