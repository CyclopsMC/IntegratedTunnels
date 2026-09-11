package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.part.PartConfigApplyResult;
import org.cyclops.integrateddynamics.core.part.PartConfigEntry;
import org.cyclops.integrateddynamics.core.part.PartConfigSection;
import org.cyclops.integrateddynamics.core.part.PartConfigSnapshot;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.cyclops.integratedtunnels.part.PartTypes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Game tests for copying and pasting the configuration of interface parts with the Wrench.
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsInterfaceConfig {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos POS_SOURCE = BlockPos.ZERO.offset(2, 0, 2);
    public static final BlockPos POS_TARGET = BlockPos.ZERO.offset(2, 0, 4);

    protected static PartPos placePart(GameTestHelper helper, BlockPos pos, IPartType<?, ?> partType) {
        helper.setBlock(pos, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(pos), Direction.WEST, partType,
                new ItemStack(partType.getItem()));
        return PartPos.of(helper.getLevel(), helper.absolutePos(pos), Direction.WEST);
    }

    protected static IPartType<?, ?> partType(PartPos partPos) {
        return PartHelpers.getPart(partPos).getPart();
    }

    protected static IPartState<?> partState(PartPos partPos) {
        return PartHelpers.getPart(partPos).getState();
    }

    protected static int getChannelInterface(PartPos partPos) {
        return ((IPartTypeInterfacePositionedAddon.IState) partState(partPos)).getChannelInterface();
    }

    protected static void setChannelInterface(PartPos partPos, int channelInterface) {
        ((IPartTypeInterfacePositionedAddon.IState) partState(partPos)).setChannelInterface(channelInterface);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static PartConfigSnapshot snapshotConfig(GameTestHelper helper, PartPos partPos,
                                                       Set<PartConfigSection> sections) {
        IPartType partType = partType(partPos);
        return partType.snapshotConfig(ValueDeseralizationContext.of(helper.getLevel()), partState(partPos), sections);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static PartConfigApplyResult applyConfig(GameTestHelper helper, PartPos partPos,
                                                       PartConfigSnapshot snapshot, Set<PartConfigSection> sections) {
        IPartType partType = partType(partPos);
        IPartState state = partState(partPos);
        INetwork network = NetworkHelpers.getNetwork(partPos).orElse(null);
        PartTarget target = partType.getTarget(partPos, state);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        return partType.applyConfig(ValueDeseralizationContext.of(helper.getLevel()), network,
                NetworkHelpers.getPartNetwork(network).orElse(null), target, state, snapshot, sections, player);
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testInterfaceChannelIsCopiedAndPasted(GameTestHelper helper) {
        PartPos source = placePart(helper, POS_SOURCE, PartTypes.INTERFACE_ITEM);
        PartPos target = placePart(helper, POS_TARGET, PartTypes.INTERFACE_ITEM);
        setChannelInterface(source, 5);

        PartConfigSnapshot snapshot = snapshotConfig(helper, source, PartConfigSection.ALL);
        PartConfigApplyResult result = applyConfig(helper, target, snapshot, PartConfigSection.ALL);

        helper.succeedWhen(() -> {
            helper.assertValueEqual(snapshot.getExtraData(PartConfigSection.PART_SETTINGS)
                            .getInt(IPartTypeInterfacePositionedAddon.CONFIG_KEY_CHANNEL_INTERFACE), 5,
                    "The interface channel was not copied");
            helper.assertValueEqual(getChannelInterface(target), 5, "The interface channel was not pasted");
            helper.assertTrue(result.getMessage().getString().contains("interface channel"),
                    "The pasted interface channel was not reported");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testInterfaceChannelIsCopiedBetweenInterfaceTypes(GameTestHelper helper) {
        PartPos source = placePart(helper, POS_SOURCE, PartTypes.INTERFACE_ITEM);
        PartPos target = placePart(helper, POS_TARGET, PartTypes.INTERFACE_FLUID);
        setChannelInterface(source, 3);

        // The subset modes paste onto other part types, and the channel means the same thing for every interface
        Set<PartConfigSection> sections = Set.of(PartConfigSection.PART_SETTINGS);
        applyConfig(helper, target, snapshotConfig(helper, source, sections), sections);

        helper.succeedWhen(() -> helper.assertValueEqual(getChannelInterface(target), 3,
                "The interface channel was not pasted onto another interface"));
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testInterfaceChannelIsNotCopiedByTheAspectSection(GameTestHelper helper) {
        PartPos source = placePart(helper, POS_SOURCE, PartTypes.INTERFACE_ITEM);
        PartPos target = placePart(helper, POS_TARGET, PartTypes.INTERFACE_ITEM);
        setChannelInterface(source, 5);

        // The interface channel is a part setting, so the aspect section must leave it alone
        Set<PartConfigSection> sections = Set.of(PartConfigSection.ASPECT);
        PartConfigSnapshot snapshot = snapshotConfig(helper, source, sections);
        applyConfig(helper, target, snapshot, sections);

        helper.succeedWhen(() -> {
            helper.assertTrue(snapshot.getExtraData(PartConfigSection.PART_SETTINGS).isEmpty(),
                    "The interface channel was copied by the aspect section");
            helper.assertValueEqual(getChannelInterface(target), 0,
                    "The interface channel was pasted by the aspect section");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testInterfaceChannelIsListedAsAnEntry(GameTestHelper helper) {
        PartPos source = placePart(helper, POS_SOURCE, PartTypes.INTERFACE_ITEM);
        setChannelInterface(source, 5);

        PartConfigSnapshot snapshot = snapshotConfig(helper, source, PartConfigSection.ALL);
        PartConfigEntry entry = snapshot.getEntries(ValueDeseralizationContext.of(helper.getLevel())).stream()
                .filter(e -> e.id().equals(PartConfigEntry.idExtra(PartConfigSection.PART_SETTINGS,
                        IPartTypeInterfacePositionedAddon.CONFIG_KEY_CHANNEL_INTERFACE)))
                .findFirst()
                .orElse(null);

        helper.succeedWhen(() -> {
            helper.assertTrue(entry != null, "The interface channel can not be switched off on its own");
            helper.assertValueEqual(entry.value().getString(), "5",
                    "The interface channel does not show its value");
            helper.assertValueEqual(entry.section(), PartConfigSection.PART_SETTINGS,
                    "The interface channel is not listed as a part setting");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testDisabledInterfaceChannelIsNotPasted(GameTestHelper helper) {
        PartPos source = placePart(helper, POS_SOURCE, PartTypes.INTERFACE_ITEM);
        PartPos target = placePart(helper, POS_TARGET, PartTypes.INTERFACE_ITEM);
        setChannelInterface(source, 5);

        PartConfigSnapshot snapshot = snapshotConfig(helper, source, PartConfigSection.ALL)
                .withEntryEnabled(PartConfigEntry.idExtra(PartConfigSection.PART_SETTINGS,
                        IPartTypeInterfacePositionedAddon.CONFIG_KEY_CHANNEL_INTERFACE), false);
        PartConfigApplyResult result = applyConfig(helper, target, snapshot, PartConfigSection.ALL);

        helper.succeedWhen(() -> {
            helper.assertValueEqual(getChannelInterface(target), 0,
                    "The interface channel was pasted even though it was switched off");
            helper.assertTrue(!result.getMessage().getString().contains("interface channel"),
                    "The interface channel was reported as pasted even though it was switched off");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testInterfaceChannelIsNotPastedFromAnotherPartType(GameTestHelper helper) {
        PartPos target = placePart(helper, POS_TARGET, PartTypes.INTERFACE_ITEM);

        // A part type that is not an interface can hold the same key for something else entirely
        CompoundTag tag = new CompoundTag();
        tag.putInt(IPartTypeInterfacePositionedAddon.CONFIG_KEY_CHANNEL_INTERFACE, 5);
        PartConfigSnapshot snapshot = new PartConfigSnapshot(PartConfigSnapshot.VERSION,
                PartTypes.EXPORTER_ITEM.getUniqueName(), Optional.empty(), Map.of(), List.of(),
                Map.of(PartConfigSection.PART_SETTINGS, tag));
        applyConfig(helper, target, snapshot, PartConfigSection.ALL);

        helper.succeedWhen(() -> helper.assertValueEqual(getChannelInterface(target), 0,
                "The interface channel was pasted from a part type that is not an interface"));
    }

}
