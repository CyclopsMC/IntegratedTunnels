package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.item.ItemStack;
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
                            .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_SLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_SLOT_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_SLOT_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("slot").appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_ITEMSTACK
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKS)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKSLIST)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_EXPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_SLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_SLOT_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_INTEGER
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_SLOT_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("slot").appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ITEMSTACK_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_ITEMSTACK
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKS)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOTCHECKSLIST)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_IMPORT =
                    TunnelAspectWriteBuilders.Item.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Item.PROPERTIES_RATESLOT)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                            .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

        public static final class Fluid {

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_INTEGER
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUIDSTACK_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_FLUIDSTACK
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKS)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACK_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKSLIST)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKLIST_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_EXPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                            .appendKind("export").buildWrite();

            public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BOOLEAN_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_BOOLEAN
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_BOOLEAN_GETRATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> INTEGER_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_INTEGER
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUIDSTACK_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_FLUIDSTACK
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKS)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACK_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> LIST_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_LIST
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATECHECKSLIST)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKLIST_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();
            public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> PREDICATE_IMPORT =
                    TunnelAspectWriteBuilders.Fluid.BUILDER_OPERATOR
                            .withProperties(TunnelAspectWriteBuilders.Fluid.PROPERTIES_RATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDPREDICATE)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDTARGET)
                            .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                            .appendKind("import").buildWrite();

        }

        public static final class World {

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> FLUID_BOOLEAN_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_BOOLEAN_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_EXPORT)
                                .appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUID_FLUIDSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_FLUIDSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_FLUID_UPDATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_EXPORT)
                                .appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> FLUID_LIST_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_FLUIDLIST_UPDATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACKLIST_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_EXPORT)
                                .appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> FLUID_PREDICATE_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_EXPORT)
                                .appendKind("fluid").appendKind("export").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> FLUID_BOOLEAN_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_BOOLEAN_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_IMPORT)
                                .appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> FLUID_FLUIDSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_FLUIDSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_FLUID)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_IMPORT)
                                .appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> FLUID_LIST_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_FLUIDLIST)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACKLIST_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_IMPORT)
                                .appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> FLUID_PREDICATE_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_FLUIDSTACK_IMPORT)
                                .appendKind("fluid").appendKind("import").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BLOCK_BOOLEAN_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PLACE_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_BOOLEAN_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("block").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> BLOCK_ITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PLACE)
                                .handle(TunnelAspectWriteBuilders.World.Item.<ItemStack>ignoreStackSize())
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("item").appendKind("block").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> BLOCK_LISTITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PLACELIST)
                                .handle(TunnelAspectWriteBuilders.World.Item.<ValueTypeList.ValueList>ignoreStackSize())
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("item").appendKind("block").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> BLOCK_PREDICATEITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PLACE_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.World.Item.<ValueTypeOperator.ValueOperator>ignoreStackSize())
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("item").appendKind("block").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock> BLOCK_BLOCK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BLOCK
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_BLOCK_PLACE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BLOCK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("block").appendKind("block").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> BLOCK_LISTBLOCK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_BLOCK_PLACELIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BLOCKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("block").appendKind("block").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> BLOCK_PREDICATEBLOCK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_BLOCK_PLACE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BLOCKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_EXPORT)
                                .appendKind("block").appendKind("block").appendKind("export").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> BLOCK_BOOLEAN_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PICK_UP_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_BOOLEAN_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("block").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> BLOCK_ITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PICK_UP)
                                .handle(TunnelAspectWriteBuilders.World.Item.<ItemStack>ignoreStackSize())
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("item").appendKind("block").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> BLOCK_LISTITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PICK_UPLIST)
                                .handle(TunnelAspectWriteBuilders.World.Item.<ValueTypeList.ValueList>ignoreStackSize())
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("item").appendKind("block").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> BLOCK_PREDICATEITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_ITEM_PICK_UP_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.World.Item.<ValueTypeOperator.ValueOperator>ignoreStackSize())
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("item").appendKind("block").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock> BLOCK_BLOCK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BLOCK
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_BLOCK_PICK_UP)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BLOCK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("block").appendKind("block").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> BLOCK_LISTBLOCK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_BLOCK_PICK_UPLIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BLOCKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("block").appendKind("block").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> BLOCK_PREDICATEBLOCK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Block.PROPERTIES_BLOCK_PICK_UP)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BLOCKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.World.Block.PROP_ITEMBLOCK_IMPORT)
                                .appendKind("block").appendKind("block").appendKind("import").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITYITEM_BOOLEAN_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PICK_UP_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_IMPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entityitem").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITYITEM_INTEGER_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PICK_UP_NORATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_IMPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entityitem").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ENTITYITEM_ITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PICK_UP)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_IMPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entityitem").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITYITEM_LISTITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PICK_UPLIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_IMPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entityitem").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITYITEM_PREDICATEITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PICK_UP_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_IMPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entityitem").appendKind("import").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITYITEM_BOOLEAN_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PLACE_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_EXPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entityitem").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITYITEM_INTEGER_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PLACE_NORATE_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_EXPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entityitem").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ENTITYITEM_ITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PLACE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_EXPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entityitem").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITYITEM_LISTITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PLACELIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_EXPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entityitem").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITYITEM_PREDICATEITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_ENTITYITEM_PLACE_NOCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITYITEM_ITEMTARGET_EXPORT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entityitem").appendKind("export").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_ITEM_BOOLEAN_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entity").appendKind("item").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITY_ITEM_INTEGER_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_SLOT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entity").appendKind("item").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ENTITY_ITEM_ITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOTCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entity").appendKind("item").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_ITEM_LISTITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOTCHECKSLIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entity").appendKind("item").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_ITEM_PREDICATEITEMSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOTCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_IMPORT)
                                .appendKind("entity").appendKind("item").appendKind("import").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_ITEM_BOOLEAN_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entity").appendKind("item").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITY_ITEM_INTEGER_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_SLOT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entity").appendKind("item").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> ENTITY_ITEM_ITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOTCHECKS)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entity").appendKind("item").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_ITEM_LISTITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOTCHECKSLIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entity").appendKind("item").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_ITEM_PREDICATEITEMSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Item.PROPERTIES_RATESLOT)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Item.PROP_ENTITY_ITEMTARGET)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("entity").appendKind("item").appendKind("export").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_FLUID_BOOLEAN_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATE)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_BOOLEAN_GETRATE)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITY_FLUID_INTEGER_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> ENTITY_FLUID_FLUIDSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_FLUIDSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATECHECKS)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACK_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_FLUID_LISTFLUIDSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATECHECKSLIST)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKLIST_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_FLUID_PREDICATEFLUIDSTACK_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATE)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_IMPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("import").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_FLUID_BOOLEAN_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATE)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_BOOLEAN_GETRATE)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITY_FLUID_INTEGER_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_INTEGER_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack> ENTITY_FLUID_FLUIDSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_FLUIDSTACK
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATECHECKS)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACK_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> ENTITY_FLUID_LISTFLUIDSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATECHECKSLIST)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKLIST_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> ENTITY_FLUID_PREDICATEFLUIDSTACK_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.World.Fluid.PROPERTIES_RATE)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_FLUIDSTACKPREDICATE_FLUIDPREDICATE)
                                .handle(TunnelAspectWriteBuilders.World.Fluid.PROP_ENTITY_FLUIDTARGET)
                                .handle(TunnelAspectWriteBuilders.Fluid.PROP_EXPORT)
                                .appendKind("entity").appendKind("fluid").appendKind("export").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_ENERGY_BOOLEAN_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Energy.PROPERTIES)
                                .handle(TunnelAspectWriteBuilders.Energy.PROP_GETRATE)
                                .handle(TunnelAspectWriteBuilders.World.Energy.PROP_ENTITY_ENERGYTARGET)
                                .handle(TunnelAspectWriteBuilders.Energy.PROP_EXPORT)
                                .appendKind("entity").appendKind("energy").appendKind("export").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITY_ENERGY_INTEGER_EXPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.World.Energy.PROPERTIES_ENTITY)
                                .handle(TunnelAspectWriteBuilders.World.Energy.PROP_ENTITY_ENERGYTARGET)
                                .handle(TunnelAspectWriteBuilders.Energy.PROP_EXPORT)
                                .appendKind("entity").appendKind("energy").appendKind("export").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> ENTITY_ENERGY_BOOLEAN_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.World.Energy.PROPERTIES)
                                .handle(TunnelAspectWriteBuilders.Energy.PROP_GETRATE)
                                .handle(TunnelAspectWriteBuilders.World.Energy.PROP_ENTITY_ENERGYTARGET)
                                .handle(TunnelAspectWriteBuilders.Energy.PROP_IMPORT)
                                .appendKind("entity").appendKind("energy").appendKind("import").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> ENTITY_ENERGY_INTEGER_IMPORT =
                        TunnelAspectWriteBuilders.World.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.World.Energy.PROPERTIES_ENTITY)
                                .handle(TunnelAspectWriteBuilders.World.Energy.PROP_ENTITY_ENERGYTARGET)
                                .handle(TunnelAspectWriteBuilders.Energy.PROP_IMPORT)
                                .appendKind("entity").appendKind("energy").appendKind("import").buildWrite();
        }

        public static final class Player {

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> CLICK_EMPTY_BOOLEAN =
                        TunnelAspectWriteBuilders.Player.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.Player.PROPERTIES_CLICK_EMPTY)
                                .handle(TunnelAspectWriteBuilders.Player.PROP_CLICK_EMPTY)
                                .appendKind("clickempty").buildWrite();

                public static final IAspectWrite<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean> CLICK_ITEM_BOOLEAN =
                        TunnelAspectWriteBuilders.Player.BUILDER_BOOLEAN
                                .withProperties(TunnelAspectWriteBuilders.Player.PROPERTIES_CLICK_SIMPLE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_BOOLEAN_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Player.PROP_ITEMTARGET_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("click").buildWrite();
                public static final IAspectWrite<ValueTypeInteger.ValueInteger, ValueTypeInteger> CLICK_ITEM_INTEGER =
                        TunnelAspectWriteBuilders.Player.BUILDER_INTEGER
                                .withProperties(TunnelAspectWriteBuilders.Player.PROPERTIES_CLICK_NORATE)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_INTEGER_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Player.PROP_ITEMTARGET_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("click").buildWrite();
                public static final IAspectWrite<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack> CLICK_ITEM_ITEMSTACK =
                        TunnelAspectWriteBuilders.Player.BUILDER_ITEMSTACK
                                .withProperties(TunnelAspectWriteBuilders.Player.PROPERTIES_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACK_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Player.PROP_ITEMTARGET_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("click").buildWrite();
                public static final IAspectWrite<ValueTypeList.ValueList, ValueTypeList> CLICK_ITEM_LISTITEMSTACK =
                        TunnelAspectWriteBuilders.Player.BUILDER_LIST
                                .withProperties(TunnelAspectWriteBuilders.Player.PROPERTIES_CLICKLIST)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKLIST_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Player.PROP_ITEMTARGET_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("click").buildWrite();
                public static final IAspectWrite<ValueTypeOperator.ValueOperator, ValueTypeOperator> CLICK_ITEM_PREDICATEITEMSTACK =
                        TunnelAspectWriteBuilders.Player.BUILDER_OPERATOR
                                .withProperties(TunnelAspectWriteBuilders.Player.PROPERTIES_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_ITEMSTACKPREDICATE_ITEMPREDICATE)
                                .handle(TunnelAspectWriteBuilders.Player.PROP_ITEMTARGET_CLICK)
                                .handle(TunnelAspectWriteBuilders.Item.PROP_EXPORT)
                                .appendKind("click").buildWrite();

        }

    }

}
