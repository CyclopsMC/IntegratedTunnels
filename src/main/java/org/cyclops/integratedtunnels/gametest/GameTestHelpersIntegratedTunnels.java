package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.Direction;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.network.PartNetworkElement;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;

import javax.annotation.Nullable;

/**
 * Helpers for game tests.
 * @author rubensworks
 */
public class GameTestHelpersIntegratedTunnels {

    /**
     * Configure the passive interaction properties of the given aspect within the given part.
     * @param partPos The position of the part.
     * @param aspect The active aspect of the part.
     * @param passiveInteraction If passive interaction should be allowed.
     * @param ignoreFilter If passive interaction should ignore the aspect's filter.
     */
    public static void setPassiveInteraction(PartPos partPos, IAspectWrite<?, ?> aspect,
                                             boolean passiveInteraction, boolean ignoreFilter) {
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(partPos);
        IAspectProperties properties = aspect.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(partPos), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO, ValueTypeBoolean.ValueBoolean.of(passiveInteraction));
        properties.setValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO_IGNORE_FILTER, ValueTypeBoolean.ValueBoolean.of(ignoreFilter));
        partStateHolder.getState().setAspectProperties(aspect, properties);
    }

    /**
     * Configure the match block property of the given aspect within the given part.
     * @param partPos The position of the part.
     * @param aspect The active aspect of the part.
     * @param matchBlock If the to-be-broken block should be matched instead of the items it would drop.
     */
    public static void setMatchBlock(PartPos partPos, IAspectWrite<?, ?> aspect, boolean matchBlock) {
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(partPos);
        IAspectProperties properties = aspect.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(partPos), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.World.PROP_MATCH_BLOCK, ValueTypeBoolean.ValueBoolean.of(matchBlock));
        partStateHolder.getState().setAspectProperties(aspect, properties);
    }

    /**
     * Configure the target side override of the given part.
     * @param partPos The position of the part.
     * @param side The side of the target block that should be interacted with,
     *             or null to fall back to the default side.
     */
    public static void setTargetSide(PartPos partPos, @Nullable Direction side) {
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(partPos);
        ((IPartType) partStateHolder.getPart()).setTargetSideOverride(partStateHolder.getState(), side);
    }

    /**
     * Configure the target side override of the given part,
     * in the same way as the part settings gui does when its settings are saved.
     * @param partPos The position of the part.
     * @param side The side of the target block that should be interacted with,
     *             or null to fall back to the default side.
     */
    public static void setTargetSideViaSettings(PartPos partPos, @Nullable Direction side) {
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(partPos);
        IPartType partType = partStateHolder.getPart();
        IPartState partState = partStateHolder.getState();

        partType.setTargetSideOverride(partState, side);

        // The part settings gui re-applies the priority and channel after saving.
        INetwork network = NetworkHelpers.getNetworkChecked(partPos.getPos().getLevel(true), partPos.getPos().getBlockPos(), partPos.getSide());
        network.setPriorityAndChannel(new PartNetworkElement<>(partType, partPos),
                partType.getPriority(partState), partType.getChannel(partState));
    }

    /**
     * Configure the silk touch property of the given aspect within the given part.
     * @param partPos The position of the part.
     * @param aspect The active aspect of the part.
     * @param silkTouch If blocks should be broken with silk touch.
     */
    public static void setSilkTouch(PartPos partPos, IAspectWrite<?, ?> aspect, boolean silkTouch) {
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(partPos);
        IAspectProperties properties = aspect.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(partPos), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.World.PROP_SILK_TOUCH, ValueTypeBoolean.ValueBoolean.of(silkTouch));
        partStateHolder.getState().setAspectProperties(aspect, properties);
    }

}
