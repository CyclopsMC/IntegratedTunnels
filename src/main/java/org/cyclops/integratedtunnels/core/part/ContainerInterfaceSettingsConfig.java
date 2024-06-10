package org.cyclops.integratedtunnels.core.part;

import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.integratedtunnels.IntegratedTunnels;

/**
 * Config for {@link ContainerInterfaceSettings}.
 * @author rubensworks
 */
public class ContainerInterfaceSettingsConfig extends GuiConfig<ContainerInterfaceSettings> {

    public ContainerInterfaceSettingsConfig() {
        super(IntegratedTunnels._instance,
                "part_interface_settings",
                eConfig -> new ContainerTypeData<>(ContainerInterfaceSettings::new, FeatureFlags.VANILLA_SET));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerInterfaceSettings>> MenuScreens.ScreenConstructor<ContainerInterfaceSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenInterfaceSettings::new);
    }

}
