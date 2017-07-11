package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;

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
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_SLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_ITEMSTACK
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKS)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKS)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_SLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_ITEMSTACK
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKS)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKS)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

        public static final class Fluid {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_INTEGER
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUIDSTACK_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_FLUIDSTACK
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKS)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACK_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKS)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKLIST_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_INTEGER
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUIDSTACK_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_FLUIDSTACK
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKS)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACK_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKS)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKLIST_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

        public static final class World {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> FLUID_BOOLEAN_EXPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID_UPDATE)
                            .handle(TunnelAspectWriteBuilders.World.PROP_BOOLEAN_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_EXPORT)
                            .appendKind("fluid").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUID_FLUIDSTACK_EXPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_FLUIDSTACK
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID_UPDATE)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_EXPORT)
                            .appendKind("fluid").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> FLUID_LIST_EXPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID_UPDATE)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACKLIST_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_EXPORT)
                            .appendKind("fluid").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> FLUID_PREDICATE_EXPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID_UPDATE)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACKPREDICATE_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_EXPORT)
                            .appendKind("fluid").appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> FLUID_BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID)
                            .handle(TunnelAspectWriteBuilders.World.PROP_BOOLEAN_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_IMPORT)
                            .appendKind("fluid").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUID_FLUIDSTACK_IMPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_FLUIDSTACK
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_IMPORT)
                            .appendKind("fluid").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> FLUID_LIST_IMPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACKLIST_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_IMPORT)
                            .appendKind("fluid").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> FLUID_PREDICATE_IMPORT =
                    TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.World.PROPERTIES_FLUID)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACKPREDICATE_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.World.PROP_FLUIDSTACK_IMPORT)
                            .appendKind("fluid").appendKind("import").buildWrite();

        }

    }

}
