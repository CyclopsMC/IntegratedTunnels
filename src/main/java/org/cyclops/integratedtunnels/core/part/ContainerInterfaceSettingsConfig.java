package org.cyclops.integratedtunnels.core.part;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
import org.cyclops.integrateddynamics.core.client.gui.container.ContainerScreenPartSettings;
import org.cyclops.integratedtunnels.IntegratedTunnels;

/**
 * Config for {@link ContainerInterfaceSettings}.
 * @author rubensworks
 */
public class ContainerInterfaceSettingsConfig extends GuiConfig<ContainerInterfaceSettings> {

    public ContainerInterfaceSettingsConfig() {
        super(IntegratedTunnels._instance,
                "interface_settings",
                eConfig -> new ContainerTypeData<>(ContainerInterfaceSettings::new));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & IHasContainer<ContainerInterfaceSettings>> ScreenManager.IScreenFactory<ContainerInterfaceSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenInterfaceSettings::new);
    }

}
