package org.cyclops.integratedtunnels.gametest;

import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;

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
