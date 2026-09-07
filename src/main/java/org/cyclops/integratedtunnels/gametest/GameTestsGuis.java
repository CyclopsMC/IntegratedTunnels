package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.gametest.GameTest;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.core.part.ContainerInterfaceSettings;
import org.cyclops.integratedtunnels.part.PartTypes;

import java.util.Optional;

/**
 * Game tests for the gui containers of tunnel parts.
 */
public class GameTestsGuis {

    public static final String TEMPLATE_EMPTY = Reference.MOD_ID + ":empty10";
    public static final int TIMEOUT = 200;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    /**
     * Places the given part, and creates its interface settings container.
     * @param settingsGui If the settings sub-gui must be created instead of the part's own gui.
     */
    private static ContainerInterfaceSettings createInterfaceSettingsMenu(GameTestHelper helper, IPartType<?, ?> partType,
                                                                         boolean settingsGui) {
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, partType, new ItemStack(partType.getItem()));
        PartPos partPos = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);

        //noinspection removal
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        Optional<MenuProvider> containerProvider = settingsGui
                ? partType.getContainerProviderSettings(partPos)
                : partType.getContainerProvider(partPos);
        AbstractContainerMenu menu = containerProvider
                .orElseThrow(() -> new GameTestAssertException(Component.literal("No container provider for " + partType), (int) helper.getTick()))
                .createMenu(0, player.getInventory(), player);

        helper.assertTrue(menu instanceof ContainerInterfaceSettings,
                Component.literal("Expected an interface settings container, but got " + menu));
        return (ContainerInterfaceSettings) menu;
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testInterfaceSettingsAreShownAsPartGui(GameTestHelper helper) {
        // Interfaces without filter have no separate part gui,
        // as their settings gui is opened directly when right-clicking them.
        ContainerInterfaceSettings menu = createInterfaceSettingsMenu(helper, PartTypes.INTERFACE_ITEM, false);
        helper.assertTrue(menu.isPartGui(), Component.literal("Interface settings gui is not marked as part gui"));
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFilteringInterfaceSettingsAreShownAsSubGui(GameTestHelper helper) {
        // Filtering interfaces do have a separate part gui, from which the settings gui can be opened.
        ContainerInterfaceSettings menu = createInterfaceSettingsMenu(helper, PartTypes.INTERFACE_FILTERING_ITEM, true);
        helper.assertFalse(menu.isPartGui(), Component.literal("Filtering interface settings gui is marked as part gui"));
        helper.succeed();
    }

}
