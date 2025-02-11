package org.cyclops.integratedtunnels.core.part;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;

/**
 * @author rubensworks
 */
public class ContainerInterfaceSettingsConfigScreenFactoryProvider extends GuiConfigScreenFactoryProvider<ContainerInterfaceSettings> {
    @Override
    public <U extends Screen & MenuAccess<ContainerInterfaceSettings>> MenuScreens.ScreenConstructor<ContainerInterfaceSettings, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenInterfaceSettings::new);
    }
}
