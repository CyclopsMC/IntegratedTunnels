package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
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
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsGuis {

    public static final String TEMPLATE_EMPTY = "empty10";
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
                .orElseThrow(() -> new GameTestAssertException("No container provider for " + partType))
                .createMenu(0, player.getInventory(), player);

        helper.assertTrue(menu instanceof ContainerInterfaceSettings,
                "Expected an interface settings container, but got " + menu);
        return (ContainerInterfaceSettings) menu;
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testInterfaceSettingsAreShownAsPartGui(GameTestHelper helper) {
        // Interfaces without filter have no separate part gui,
        // as their settings gui is opened directly when right-clicking them.
        ContainerInterfaceSettings menu = createInterfaceSettingsMenu(helper, PartTypes.INTERFACE_ITEM, false);
        helper.assertTrue(menu.isPartGui(), "Interface settings gui is not marked as part gui");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFilteringInterfaceSettingsAreShownAsSubGui(GameTestHelper helper) {
        // Filtering interfaces do have a separate part gui, from which the settings gui can be opened.
        ContainerInterfaceSettings menu = createInterfaceSettingsMenu(helper, PartTypes.INTERFACE_FILTERING_ITEM, true);
        helper.assertFalse(menu.isPartGui(), "Filtering interface settings gui is marked as part gui");
        helper.succeed();
    }

}
