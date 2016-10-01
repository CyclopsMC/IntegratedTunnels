package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;

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

        public static final class Item {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_BOOLEAN)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_INTEGER)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_ITEMSTACK
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_ITEMSTACK)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_ITEMSTACK)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_BOOLEAN)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_INTEGER)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_ITEMSTACK
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_ITEMSTACK)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_ITEMSTACK)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

    }

}
