package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;

/**
 * Collection of all tunnel aspects.
 * @author rubensworks
 */
public class TunnelAspects {

    public static void load() {}

    public static final class Write {

        public static final class Energy {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EXPORT =
                    TunnelAspectWriteBuilders.Energy.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Energy.PROPERTIES)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_ENERGYTARGET)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_EXPORT =
                    TunnelAspectWriteBuilders.Energy.BUILDER_INTEGER
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_ENERGYTARGET)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.Energy.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Energy.PROPERTIES)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_ENERGYTARGET)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_IMPORT =
                    TunnelAspectWriteBuilders.Energy.BUILDER_INTEGER
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_ENERGYTARGET)
                            .handle(TunnelAspectWriteBuilders.Energy.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

    }

}
