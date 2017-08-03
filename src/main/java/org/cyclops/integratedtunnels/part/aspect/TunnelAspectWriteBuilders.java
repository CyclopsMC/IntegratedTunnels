package org.cyclops.integratedtunnels.part.aspect;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integrateddynamics.core.helper.L10NValues;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.part.aspect.build.AspectBuilder;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectWriteActivator;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectWriteDeactivator;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectProperties;
import org.cyclops.integrateddynamics.core.part.aspect.property.AspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integrateddynamics.part.aspect.write.AspectWriteBuilders;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.*;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

/**
 * Collection of tunnel aspect write builders and value propagators.
 * @author rubensworks
 */
public class TunnelAspectWriteBuilders {

    public static final class Energy {

        public static final IAspectWriteActivator ACTIVATOR = createPositionedNetworkAddonActivator(new Function<Void, Capability<IEnergyNetwork>>() {
            @Nullable
            @Override
            public Capability<IEnergyNetwork> apply(Void input) {
                return Capabilities.NETWORK_ENERGY;
            }
        }, CapabilityEnergy.ENERGY);
        public static final IAspectWriteDeactivator DEACTIVATOR = createPositionedNetworkAddonDeactivator(new Function<Void, Capability<IEnergyNetwork>>() {
            @Nullable
            @Override
            public Capability<IEnergyNetwork> apply(Void input) {
                return Capabilities.NETWORK_ENERGY;
            }
        }, CapabilityEnergy.ENERGY);

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("energy").handle(AspectWriteBuilders.PROP_GET_BOOLEAN);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("energy").handle(AspectWriteBuilders.PROP_GET_INTEGER);

        public static final Predicate<ValueTypeInteger.ValueInteger> VALIDATOR_INTEGER_MAXRATE = new Predicate<ValueTypeInteger.ValueInteger>() {
            @Override
            public boolean apply(ValueTypeInteger.ValueInteger input) {
                return input.getRawValue() <= org.cyclops.integrateddynamics.GeneralConfig.energyRateLimit;
            }
        };

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RATE =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.energy.rate.name",
                        Predicates.and(AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE, VALIDATOR_INTEGER_MAXRATE));
        public static final IAspectProperties PROPERTIES = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE
        ));
        static {
            PROPERTIES.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(1000));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>
                PROP_GETRATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Integer> getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) {
                return Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? input.getMiddle().getValue(PROP_RATE).getRawValue() : 0);
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, EnergyTarget>
                PROP_ENERGYTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, EnergyTarget>() {
            @Override
            public EnergyTarget getOutput(Triple<PartTarget, IAspectProperties, Integer> input) {
                PartPos center = input.getLeft().getCenter();
                PartPos target = input.getLeft().getTarget();
                INetwork network = NetworkHelpers.getNetwork(center.getPos().getWorld(), center.getPos().getBlockPos());
                IEnergyStorage energyStorage = EnergyHelpers.getEnergyStorage(target);
                return new EnergyTarget(network.getCapability(Capabilities.NETWORK_ENERGY), energyStorage, input.getRight());
            }
        };
        public static final IAspectValuePropagator<EnergyTarget, Void>
                PROP_EXPORT = new IAspectValuePropagator<EnergyTarget, Void>() {
            @Override
            public Void getOutput(EnergyTarget input) {
                if (input.getEnergyNetwork() != null && input.getEnergyStorage() != null && input.getAmount() != 0) {
                    TunnelEnergyHelpers.moveEnergy(input.getEnergyNetwork(), input.getEnergyStorage(), input.getAmount());
                }
                return null;
            }
        };
        public static final IAspectValuePropagator<EnergyTarget, Void>
                PROP_IMPORT = new IAspectValuePropagator<EnergyTarget, Void>() {
            @Override
            public Void getOutput(EnergyTarget input) {
                if (input.getEnergyNetwork() != null && input.getEnergyStorage() != null && input.getAmount() != 0) {
                    TunnelEnergyHelpers.moveEnergy(input.getEnergyStorage(), input.getEnergyNetwork(), input.getAmount());
                }
                return null;
            }
        };

        public static class EnergyTarget {

            private final IEnergyNetwork energyNetwork;
            private final IEnergyStorage energyStorage;
            private final int amount;

            public EnergyTarget(IEnergyNetwork energyNetwork, IEnergyStorage energyStorage, int amount) {
                this.energyNetwork = energyNetwork;
                this.energyStorage = energyStorage;
                this.amount = amount;
            }

            public IEnergyNetwork getEnergyNetwork() {
                return energyNetwork;
            }

            public IEnergyStorage getEnergyStorage() {
                return energyStorage;
            }

            public int getAmount() {
                return amount;
            }
        }

    }

    public static final class Item {

        public static final IAspectWriteActivator ACTIVATOR = createPositionedNetworkAddonActivator(new Function<Void, Capability<IItemNetwork>>() {
            @Nullable
            @Override
            public Capability<IItemNetwork> apply(Void input) {
                return ItemNetworkConfig.CAPABILITY;
            }
        }, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        public static final IAspectWriteDeactivator DEACTIVATOR = createPositionedNetworkAddonDeactivator(new Function<Void, Capability<IItemNetwork>>() {
            @Nullable
            @Override
            public Capability<IItemNetwork> apply(Void input) {
                return ItemNetworkConfig.CAPABILITY;
            }
        }, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("item").handle(AspectWriteBuilders.PROP_GET_BOOLEAN);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("item").handle(AspectWriteBuilders.PROP_GET_INTEGER);
        public static final AspectBuilder<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack, Triple<PartTarget, IAspectProperties, ItemStack>>
                BUILDER_ITEMSTACK = AspectWriteBuilders.BUILDER_ITEMSTACK.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("item").handle(AspectWriteBuilders.PROP_GET_ITEMSTACK);
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>>
                BUILDER_LIST = AspectWriteBuilders.BUILDER_LIST.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("item");
        public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>>
                BUILDER_OPERATOR = AspectWriteBuilders.BUILDER_OPERATOR.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("item");

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RATE =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.item.rate.name");
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_SLOT =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.item.slot.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_CHECK_STACKSIZE =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.item.checkstacksize.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_CHECK_DAMAGE =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.item.checkdamage.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_CHECK_NBT =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.item.checknbt.name");
        public static final IAspectProperties PROPERTIES_RATESLOT = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE,
                PROP_SLOT
        ));
        public static final IAspectProperties PROPERTIES_SLOT = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_SLOT
        ));
        public static final IAspectProperties PROPERTIES_RATESLOTCHECKS = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE,
                PROP_SLOT,
                PROP_CHECK_STACKSIZE,
                PROP_CHECK_DAMAGE,
                PROP_CHECK_NBT
        ));
        static {
            PROPERTIES_RATESLOT.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(64));
            PROPERTIES_RATESLOT.setValue(PROP_SLOT, ValueTypeInteger.ValueInteger.of(-1));

            PROPERTIES_SLOT.setValue(PROP_SLOT, ValueTypeInteger.ValueInteger.of(-1));

            PROPERTIES_RATESLOTCHECKS.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(64));
            PROPERTIES_RATESLOTCHECKS.setValue(PROP_SLOT, ValueTypeInteger.ValueInteger.of(-1));
            PROPERTIES_RATESLOTCHECKS.setValue(PROP_CHECK_STACKSIZE, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATESLOTCHECKS.setValue(PROP_CHECK_DAMAGE, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_RATESLOTCHECKS.setValue(PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>
                PROP_BOOLEAN_ITEMPREDICATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) throws EvaluationException {
                ItemStackPredicate itemStackMatcher = input.getRight() ? TunnelItemHelpers.MATCH_BLOCK : TunnelItemHelpers.MATCH_NONE;
                int amount = input.getRight() ? 1 : 0;
                int transferHash = input.getRight() ? 1 : 0;
                return Triple.of(input.getLeft(), input.getMiddle(), Triple.of(itemStackMatcher, amount, transferHash));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>
                PROP_INTEGER_ITEMPREDICATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> getOutput(Triple<PartTarget, IAspectProperties, Integer> input) throws EvaluationException {
                ItemStackPredicate itemStackMatcher = TunnelItemHelpers.MATCH_ALL;
                int amount = input.getRight();
                int transferHash = input.getRight();
                return Triple.of(input.getLeft(), input.getMiddle(), Triple.of(itemStackMatcher, amount, transferHash));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ItemStack>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>
                PROP_ITEMSTACK_ITEMPREDICATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ItemStack>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> getOutput(Triple<PartTarget, IAspectProperties, ItemStack> input) throws EvaluationException {
                IAspectProperties properties = input.getMiddle();
                boolean checkStackSize = properties.getValue(PROP_CHECK_STACKSIZE).getRawValue();
                boolean checkDamage = properties.getValue(PROP_CHECK_DAMAGE).getRawValue();
                boolean checkNbt = properties.getValue(PROP_CHECK_NBT).getRawValue();

                ItemStackPredicate itemStackMatcher = TunnelItemHelpers.matchItemStack(input.getRight(), checkStackSize, checkDamage, checkNbt);
                int amount = properties.getValue(PROP_RATE).getRawValue();
                int transferHash = TunnelItemHelpers.getItemStackHashCode(input.getRight());
                return Triple.of(input.getLeft(), input.getMiddle(), Triple.of(itemStackMatcher, amount, transferHash));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>
                PROP_ITEMSTACKLIST_ITEMPREDICATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> getOutput(Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList> input) throws EvaluationException {
                ValueTypeList.ValueList list = input.getRight();
                if (list.getRawValue().getValueType() != ValueTypes.OBJECT_ITEMSTACK) {
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDLISTVALUETYPE,
                            ValueTypes.OBJECT_ITEMSTACK, list.getRawValue().getValueType()).localize());
                }
                IAspectProperties properties = input.getMiddle();
                boolean checkStackSize = properties.getValue(PROP_CHECK_STACKSIZE).getRawValue();
                boolean checkDamage = properties.getValue(PROP_CHECK_DAMAGE).getRawValue();
                boolean checkNbt = properties.getValue(PROP_CHECK_NBT).getRawValue();

                ItemStackPredicate itemStackMatcher = TunnelItemHelpers.matchItemStacks(list.getRawValue(), checkStackSize, checkDamage, checkNbt);
                int amount = properties.getValue(PROP_RATE).getRawValue();
                int transferHash = list.getRawValue().hashCode();
                return Triple.of(input.getLeft(), input.getMiddle(), Triple.of(itemStackMatcher, amount, transferHash));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>
                PROP_ITEMSTACKPREDICATE_ITEMPREDICATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> getOutput(Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator> input) throws EvaluationException {
                IOperator predicate = input.getRight().getRawValue();
                if (predicate.getInputTypes().length == 1 && ValueHelpers.correspondsTo(predicate.getInputTypes()[0], ValueTypes.OBJECT_ITEMSTACK)) {
                    IAspectProperties properties = input.getMiddle();
                    ItemStackPredicate itemStackMatcher = TunnelItemHelpers.matchPredicate(input.getLeft(), predicate);
                    int amount = properties.getValue(PROP_RATE).getRawValue();
                    int transferHash = predicate.hashCode();
                    return Triple.of(input.getLeft(), input.getMiddle(), Triple.of(itemStackMatcher, amount, transferHash));
                } else {
                    String current = ValueTypeOperator.getSignature(predicate);
                    String expected = ValueTypeOperator.getSignature(new IValueType[]{ValueTypes.OBJECT_ITEMSTACK}, ValueTypes.BOOLEAN);
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            expected, current).localize());
                }
            }
        };

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>, ItemTarget>
                PROP_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>, ItemTarget>() {
            @Override
            public ItemTarget getOutput(Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> input) throws EvaluationException {
                return ItemTarget.of(input.getLeft(), input.getMiddle(), input.getRight().getMiddle(),
                        input.getRight().getLeft(), input.getRight().getRight());
            }
        };

        public static final IAspectValuePropagator<ItemTarget, Void>
                PROP_EXPORT = new IAspectValuePropagator<ItemTarget, Void>() {
            @Override
            public Void getOutput(ItemTarget input) {
                if (input.getItemNetwork() != null && input.getItemStorage() != null && input.getAmount() != 0) {
                    TunnelItemHelpers.moveItemsStateOptimized(
                            input.getConnectionHash(), input.getItemNetwork(), input.getInventoryStateNetwork(), -1, input.getItemNetworkSlotless(),
                            input.getItemStorage(), input.getInventoryStateStorage(), input.getSlot(), input.getItemStorageSlotless(),
                            input.getAmount(), input.getItemStackMatcher());
                }
                return null;
            }
        };
        public static final IAspectValuePropagator<ItemTarget, Void>
                PROP_IMPORT = new IAspectValuePropagator<ItemTarget, Void>() {
            @Override
            public Void getOutput(ItemTarget input) {
                if (input.getItemNetwork() != null && input.getItemStorage() != null && input.getAmount() != 0) {
                    TunnelItemHelpers.moveItemsStateOptimized(
                            input.getConnectionHash(), input.getItemStorage(), input.getInventoryStateStorage(), input.getSlot(), input.getItemStorageSlotless(),
                            input.getItemNetwork(), input.getInventoryStateNetwork(), -1, input.getItemNetworkSlotless(),
                            input.getAmount(), input.getItemStackMatcher());
                }
                return null;
            }
        };

        public static class ItemTarget {

            private final IItemNetwork itemNetwork;
            private final IItemHandler itemStorage;
            private final IInventoryState inventoryStateNetwork;
            private final IInventoryState inventoryStateStorage;
            private final ISlotlessItemHandler itemNetworkSlotless;
            private final ISlotlessItemHandler itemStorageSlotless;
            private final int connectionHash;
            private final int slot;
            private final int amount;
            private final ItemStackPredicate itemStackMatcher;
            private final PartTarget partTarget;
            private final IAspectProperties properties;

            public static ItemTarget of(PartTarget partTarget, IAspectProperties properties, int amount,
                                        ItemStackPredicate itemStackMatcher, int transferHash) {
                PartPos center = partTarget.getCenter();
                PartPos target = partTarget.getTarget();
                INetwork network = NetworkHelpers.getNetwork(center.getPos().getWorld(), center.getPos().getBlockPos());
                IItemHandler itemHandler = TileHelpers.getCapability(target.getPos(), target.getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                int slot = properties.getValue(PROP_SLOT).getRawValue();
                return new ItemTarget(
                        network, itemHandler,
                        TileHelpers.getCapability(target.getPos(), target.getSide(), Capabilities.INVENTORY_STATE),
                        TileHelpers.getCapability(target.getPos(), target.getSide(), Capabilities.SLOTLESS_ITEMHANDLER),
                        target.hashCode(), slot, amount, itemStackMatcher, transferHash, partTarget, properties);
            }

            public ItemTarget(INetwork network, IItemHandler itemStorage,
                              IInventoryState inventoryStateStorage,
                              ISlotlessItemHandler itemStorageSlotless,
                              int storagePosHash, int slot,
                              int amount, ItemStackPredicate itemStackMatcher, int transferHash, PartTarget partTarget,
                              IAspectProperties properties) {
                this.itemNetwork = network.getCapability(ItemNetworkConfig.CAPABILITY);
                this.itemStorage = itemStorage;
                this.inventoryStateNetwork = network.hasCapability(Capabilities.INVENTORY_STATE)
                        ? network.getCapability(Capabilities.INVENTORY_STATE) : null;
                this.inventoryStateStorage = inventoryStateStorage;
                this.itemNetworkSlotless = network.hasCapability(Capabilities.SLOTLESS_ITEMHANDLER)
                        ? network.getCapability(Capabilities.SLOTLESS_ITEMHANDLER) : null;
                this.itemStorageSlotless = itemStorageSlotless;
                this.connectionHash = transferHash << 4 + storagePosHash + itemNetwork.hashCode();
                this.slot = slot;
                this.amount = amount;
                this.itemStackMatcher = itemStackMatcher;
                this.partTarget = partTarget;
                this.properties = properties;
            }

            public IItemNetwork getItemNetwork() {
                return itemNetwork;
            }

            public IItemHandler getItemStorage() {
                return itemStorage;
            }

            public IInventoryState getInventoryStateNetwork() {
                return inventoryStateNetwork;
            }

            public IInventoryState getInventoryStateStorage() {
                return inventoryStateStorage;
            }

            public ISlotlessItemHandler getItemNetworkSlotless() {
                return itemNetworkSlotless;
            }

            public ISlotlessItemHandler getItemStorageSlotless() {
                return itemStorageSlotless;
            }

            public int getConnectionHash() {
                return connectionHash;
            }

            public int getSlot() {
                return slot;
            }

            public int getAmount() {
                return amount;
            }

            public ItemStackPredicate getItemStackMatcher() {
                return itemStackMatcher;
            }

            public PartTarget getPartTarget() {
                return partTarget;
            }

            public IAspectProperties getProperties() {
                return properties;
            }
        }

    }

    public static final class Fluid {

        public static final IAspectWriteActivator ACTIVATOR = createPositionedNetworkAddonActivator(new Function<Void, Capability<IFluidNetwork>>() {
            @Nullable
            @Override
            public Capability<IFluidNetwork> apply(Void input) {
                return FluidNetworkConfig.CAPABILITY;
            }
        }, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        public static final IAspectWriteDeactivator DEACTIVATOR = createPositionedNetworkAddonDeactivator(new Function<Void, Capability<IFluidNetwork>>() {
            @Nullable
            @Override
            public Capability<IFluidNetwork> apply(Void input) {
                return FluidNetworkConfig.CAPABILITY;
            }
        }, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("fluid").handle(AspectWriteBuilders.PROP_GET_BOOLEAN);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("fluid").handle(AspectWriteBuilders.PROP_GET_INTEGER);
        public static final AspectBuilder<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack, Triple<PartTarget, IAspectProperties, FluidStack>>
                BUILDER_FLUIDSTACK = AspectWriteBuilders.BUILDER_FLUIDSTACK.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("fluid").handle(AspectWriteBuilders.PROP_GET_FLUIDSTACK);
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>>
                BUILDER_LIST = AspectWriteBuilders.BUILDER_LIST.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("fluid");
        public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>>
                BUILDER_OPERATOR = AspectWriteBuilders.BUILDER_OPERATOR.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("fluid");

        public static final Predicate<ValueTypeInteger.ValueInteger> VALIDATOR_INTEGER_MAXRATE = new Predicate<ValueTypeInteger.ValueInteger>() {
            @Override
            public boolean apply(ValueTypeInteger.ValueInteger input) {
                return input.getRawValue() <= GeneralConfig.fluidRateLimit;
            }
        };

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RATE =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.fluid.rate.name",
                        Predicates.and(AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE, VALIDATOR_INTEGER_MAXRATE));
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_CHECK_AMOUNT =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.fluid.checkamount.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_CHECK_NBT =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.fluid.checknbt.name");

        public static final IAspectProperties PROPERTIES_RATE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE
        ));
        public static final IAspectProperties PROPERTIES_RATECHECKS = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE,
                PROP_CHECK_AMOUNT,
                PROP_CHECK_NBT
        ));
        static {
            PROPERTIES_RATE.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(1000));

            PROPERTIES_RATECHECKS.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(1000));
            PROPERTIES_RATECHECKS.setValue(PROP_CHECK_AMOUNT, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_RATECHECKS.setValue(PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>
                PROP_BOOLEAN_GETRATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Integer> getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) {
                return Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? input.getMiddle().getValue(PROP_RATE).getRawValue() : 0);
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, FluidTarget>
                PROP_INTEGER_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, FluidTarget>() {
            @Override
            public FluidTarget getOutput(Triple<PartTarget, IAspectProperties, Integer> input) {
                return FluidTarget.of(input.getLeft(), input.getMiddle(), input.getRight(), TunnelFluidHelpers.MATCH_ALL);
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, FluidStack>, FluidTarget>
                PROP_FLUIDSTACK_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, FluidStack>, FluidTarget>() {
            @Override
            public FluidTarget getOutput(Triple<PartTarget, IAspectProperties, FluidStack> input) {
                IAspectProperties properties = input.getMiddle();
                int rate = properties.getValue(PROP_RATE).getRawValue();
                boolean checkAmount = properties.getValue(PROP_CHECK_AMOUNT).getRawValue();
                boolean checkNbt = properties.getValue(PROP_CHECK_NBT).getRawValue();
                return FluidTarget.of(input.getLeft(), input.getMiddle(), rate,
                        TunnelFluidHelpers.matchFluidStack(input.getRight(), checkAmount, checkNbt));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, FluidTarget>
                PROP_FLUIDSTACKLIST_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, FluidTarget>() {
            @Override
            public FluidTarget getOutput(Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList> input) throws EvaluationException {
                ValueTypeList.ValueList list = input.getRight();
                if (list.getRawValue().getValueType() != ValueTypes.OBJECT_FLUIDSTACK) {
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDLISTVALUETYPE,
                            ValueTypes.OBJECT_FLUIDSTACK, list.getRawValue().getValueType()).localize());
                }
                IAspectProperties properties = input.getMiddle();
                int rate = properties.getValue(PROP_RATE).getRawValue();
                boolean checkAmount = properties.getValue(PROP_CHECK_AMOUNT).getRawValue();
                boolean checkNbt = properties.getValue(PROP_CHECK_NBT).getRawValue();
                return FluidTarget.of(input.getLeft(), input.getMiddle(), rate,
                        TunnelFluidHelpers.matchFluidStacks(list.getRawValue(), checkAmount, checkNbt));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, FluidTarget>
                PROP_FLUIDSTACKPREDICATE_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, FluidTarget>() {
            @Override
            public FluidTarget getOutput(Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator> input) throws EvaluationException {
                IOperator predicate = input.getRight().getRawValue();
                if (predicate.getInputTypes().length == 1 && ValueHelpers.correspondsTo(predicate.getInputTypes()[0], ValueTypes.OBJECT_FLUIDSTACK)) {
                    IAspectProperties properties = input.getMiddle();
                    int rate = properties.getValue(PROP_RATE).getRawValue();
                    return FluidTarget.of(input.getLeft(), input.getMiddle(), rate,
                            TunnelFluidHelpers.matchPredicate(input.getLeft(), predicate));
                } else {
                    String current = ValueTypeOperator.getSignature(predicate);
                    String expected = ValueTypeOperator.getSignature(new IValueType[]{ValueTypes.OBJECT_FLUIDSTACK}, ValueTypes.BOOLEAN);
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            expected, current).localize());
                }
            }
        };

        public static final IAspectValuePropagator<FluidTarget, Void>
                PROP_EXPORT = new IAspectValuePropagator<FluidTarget, Void>() {
            @Override
            public Void getOutput(FluidTarget input) {
                if (input.getFluidNetwork() != null && input.getFluidHandler() != null && input.getAmount() != 0) {
                    TunnelFluidHelpers.moveFluids(input.getFluidNetwork(), input.getFluidHandler(), input.getAmount(), true, input.getFluidStackMatcher());
                }
                return null;
            }
        };
        public static final IAspectValuePropagator<FluidTarget, Void>
                PROP_IMPORT = new IAspectValuePropagator<FluidTarget, Void>() {
            @Override
            public Void getOutput(FluidTarget input) {
                if (input.getFluidNetwork() != null && input.getFluidHandler() != null && input.getAmount() != 0) {
                    TunnelFluidHelpers.moveFluids(input.getFluidHandler(), input.getFluidNetwork(), input.getAmount(), true, input.getFluidStackMatcher());
                }
                return null;
            }
        };

        public static class FluidTarget {

            private final PartTarget partTarget;
            private final IFluidNetwork fluidNetwork;
            private final IFluidHandler fluidHandler;
            private final int amount;
            private final Predicate<FluidStack> fluidStackMatcher;
            private final IAspectProperties properties;

            public static FluidTarget of(PartTarget partTarget, IAspectProperties properties, int amount,
                                         Predicate<FluidStack> fluidStackMatcher) {
                PartPos center = partTarget.getCenter();
                PartPos target = partTarget.getTarget();
                INetwork network = NetworkHelpers.getNetwork(center.getPos().getWorld(), center.getPos().getBlockPos());
                IFluidHandler fluidHandler = FluidUtil.getFluidHandler(target.getPos().getWorld(), target.getPos().getBlockPos(), target.getSide());
                return new FluidTarget(partTarget, network.getCapability(FluidNetworkConfig.CAPABILITY), fluidHandler,
                        amount, fluidStackMatcher, properties);
            }

            public FluidTarget(PartTarget partTarget, IFluidNetwork fluidNetwork, IFluidHandler fluidHandler,
                               int amount, Predicate<FluidStack> fluidStackMatcher, IAspectProperties properties) {
                this.partTarget = partTarget;
                this.fluidNetwork = fluidNetwork;
                this.fluidHandler = fluidHandler;
                this.amount = amount;
                this.fluidStackMatcher = fluidStackMatcher;
                this.properties = properties;
            }

            public PartTarget getPartTarget() {
                return partTarget;
            }

            public IFluidNetwork getFluidNetwork() {
                return fluidNetwork;
            }

            public IFluidHandler getFluidHandler() {
                return fluidHandler;
            }

            public int getAmount() {
                return amount;
            }

            public Predicate<FluidStack> getFluidStackMatcher() {
                return fluidStackMatcher;
            }

            public IAspectProperties getProperties() {
                return properties;
            }
        }

    }

    public static final class World {

        public static final AspectBuilder<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock, Triple<PartTarget, IAspectProperties, ValueObjectTypeBlock.ValueBlock>>
                BUILDER_BLOCK_BASE = AspectWriteBuilders.getValue(AspectBuilder.forWriteType(ValueTypes.OBJECT_BLOCK));

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedTunnels._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_BOOLEAN);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_INTEGER);
        public static final AspectBuilder<ValueObjectTypeItemStack.ValueItemStack, ValueObjectTypeItemStack, Triple<PartTarget, IAspectProperties, ItemStack>>
                BUILDER_ITEMSTACK = AspectWriteBuilders.BUILDER_ITEMSTACK.byMod(IntegratedTunnels._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_ITEMSTACK);
        public static final AspectBuilder<ValueObjectTypeBlock.ValueBlock, ValueObjectTypeBlock, Triple<PartTarget, IAspectProperties, IBlockState>>
                BUILDER_BLOCK = BUILDER_BLOCK_BASE.byMod(IntegratedTunnels._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_BLOCK);
        public static final AspectBuilder<ValueObjectTypeFluidStack.ValueFluidStack, ValueObjectTypeFluidStack, Triple<PartTarget, IAspectProperties, FluidStack>>
                BUILDER_FLUIDSTACK = AspectWriteBuilders.BUILDER_FLUIDSTACK.byMod(IntegratedTunnels._instance)
                .appendKind("world").handle(AspectWriteBuilders.PROP_GET_FLUIDSTACK);
        public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>>
                BUILDER_LIST = AspectWriteBuilders.BUILDER_LIST.byMod(IntegratedTunnels._instance)
                .appendKind("world");
        public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>>
                BUILDER_OPERATOR = AspectWriteBuilders.BUILDER_OPERATOR.byMod(IntegratedTunnels._instance)
                .appendKind("world");

        public static final Predicate<ValueTypeDouble.ValueDouble> VALIDATOR_DOUBLE_ANGLE = new Predicate<ValueTypeDouble.ValueDouble>() {
            @Override
            public boolean apply(ValueTypeDouble.ValueDouble input) {
                return input.getRawValue() >= -180D && input.getRawValue() <= 180F;
            }
        };
        public static final Predicate<ValueTypeDouble.ValueDouble> VALIDATOR_DOUBLE_OFFSET = new Predicate<ValueTypeDouble.ValueDouble>() {
            @Override
            public boolean apply(ValueTypeDouble.ValueDouble input) {
                return input.getRawValue() >= 0D && input.getRawValue() <= 1F;
            }
        };

        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_BLOCK_UPDATE =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.blockupdate.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_HAND_LEFT =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.lefthand.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_SILK_TOUCH =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.silktouch.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_IGNORE_REPLACABLE =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.ignorereplacable.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_BREAK_ON_NO_DROPS =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.breaknodrops.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_IGNORE_PICK_UP_DELAY =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.ignorepickupdelay.name");
        public static final IAspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean> PROP_DISPENSE =
                new AspectPropertyTypeInstance<ValueTypeBoolean, ValueTypeBoolean.ValueBoolean>(ValueTypes.BOOLEAN, "aspect.aspecttypes.integratedtunnels.boolean.world.dispense.name");
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_OFFSET_X =
                new AspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble>(ValueTypes.DOUBLE, "aspect.aspecttypes.integratedtunnels.double.world.offsetx.name", VALIDATOR_DOUBLE_OFFSET);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_OFFSET_Y =
                new AspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble>(ValueTypes.DOUBLE, "aspect.aspecttypes.integratedtunnels.double.world.offsety.name", VALIDATOR_DOUBLE_OFFSET);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_OFFSET_Z =
                new AspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble>(ValueTypes.DOUBLE, "aspect.aspecttypes.integratedtunnels.double.world.offsetz.name", VALIDATOR_DOUBLE_OFFSET);
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_LIFESPAN =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.boolean.world.lifespan.name", AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_DELAY_BEFORE_PICKUP =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.boolean.world.delaybeforepickup.name", AspectReadBuilders.VALIDATOR_INTEGER_POSITIVE);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_VELOCITY =
                new AspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble>(ValueTypes.DOUBLE, "aspect.aspecttypes.integratedtunnels.double.world.velocity.name", new Predicate<ValueTypeDouble.ValueDouble>() {
                    @Override
                    public boolean apply(ValueTypeDouble.ValueDouble input) {
                        return input.getRawValue() >= 0 && input.getRawValue() <= 25D;
                    }
                });
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_YAW =
                new AspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble>(ValueTypes.DOUBLE, "aspect.aspecttypes.integratedtunnels.double.world.yaw.name", VALIDATOR_DOUBLE_ANGLE);
        public static final IAspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble> PROP_PITCH =
                new AspectPropertyTypeInstance<ValueTypeDouble, ValueTypeDouble.ValueDouble>(ValueTypes.DOUBLE, "aspect.aspecttypes.integratedtunnels.double.world.pitch.name", VALIDATOR_DOUBLE_ANGLE);

        public static final IAspectProperties PROPERTIES_FLUID_UPDATE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                Fluid.PROP_CHECK_NBT,
                PROP_BLOCK_UPDATE,
                PROP_IGNORE_REPLACABLE
        ));
        public static final IAspectProperties PROPERTIES_FLUID = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                Fluid.PROP_CHECK_NBT
        ));
        public static final IAspectProperties PROPERTIES_BLOCK_PLACE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                Item.PROP_CHECK_DAMAGE,
                Item.PROP_CHECK_NBT,
                PROP_BLOCK_UPDATE,
                PROP_HAND_LEFT,
                PROP_IGNORE_REPLACABLE
        ));
        public static final IAspectProperties PROPERTIES_BLOCK_PICK_UP = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                Item.PROP_CHECK_DAMAGE,
                Item.PROP_CHECK_NBT,
                PROP_BLOCK_UPDATE,
                PROP_HAND_LEFT,
                PROP_SILK_TOUCH,
                PROP_IGNORE_REPLACABLE,
                PROP_BREAK_ON_NO_DROPS
        ));
        public static final IAspectProperties PROPERTIES_ENTITYITEM_PICK_UP = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                Item.PROP_RATE,
                Item.PROP_CHECK_DAMAGE,
                Item.PROP_CHECK_NBT,
                PROP_IGNORE_PICK_UP_DELAY
        ));
        public static final IAspectProperties PROPERTIES_ENTITYITEM_PLACE = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                Item.PROP_RATE,
                Item.PROP_CHECK_DAMAGE,
                Item.PROP_CHECK_NBT,
                PROP_DISPENSE,
                PROP_OFFSET_X,
                PROP_OFFSET_Y,
                PROP_OFFSET_Z,
                PROP_LIFESPAN,
                PROP_DELAY_BEFORE_PICKUP,
                PROP_VELOCITY,
                PROP_YAW,
                PROP_PITCH
        ));
        static {
            PROPERTIES_FLUID_UPDATE.setValue(Fluid.PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_FLUID_UPDATE.setValue(PROP_BLOCK_UPDATE, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_FLUID_UPDATE.setValue(PROP_IGNORE_REPLACABLE, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_FLUID.setValue(Fluid.PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));

            PROPERTIES_BLOCK_PLACE.setValue(Item.PROP_CHECK_DAMAGE, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_BLOCK_PLACE.setValue(Item.PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_BLOCK_PLACE.setValue(PROP_BLOCK_UPDATE, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_BLOCK_PLACE.setValue(PROP_HAND_LEFT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_BLOCK_PLACE.setValue(PROP_IGNORE_REPLACABLE, ValueTypeBoolean.ValueBoolean.of(false));

            PROPERTIES_BLOCK_PICK_UP.setValue(Item.PROP_CHECK_DAMAGE, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_BLOCK_PICK_UP.setValue(Item.PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_BLOCK_PICK_UP.setValue(PROP_BLOCK_UPDATE, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_BLOCK_PICK_UP.setValue(PROP_HAND_LEFT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_BLOCK_PICK_UP.setValue(PROP_SILK_TOUCH, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_BLOCK_PICK_UP.setValue(PROP_IGNORE_REPLACABLE, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_BLOCK_PICK_UP.setValue(PROP_BREAK_ON_NO_DROPS, ValueTypeBoolean.ValueBoolean.of(true));

            PROPERTIES_ENTITYITEM_PICK_UP.setValue(Item.PROP_RATE, ValueTypeInteger.ValueInteger.of(64));
            PROPERTIES_ENTITYITEM_PICK_UP.setValue(Item.PROP_CHECK_DAMAGE, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_ENTITYITEM_PICK_UP.setValue(Item.PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_ENTITYITEM_PICK_UP.setValue(PROP_IGNORE_PICK_UP_DELAY, ValueTypeBoolean.ValueBoolean.of(true));

            PROPERTIES_ENTITYITEM_PLACE.setValue(Item.PROP_RATE, ValueTypeInteger.ValueInteger.of(64));
            PROPERTIES_ENTITYITEM_PLACE.setValue(Item.PROP_CHECK_DAMAGE, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_ENTITYITEM_PLACE.setValue(Item.PROP_CHECK_NBT, ValueTypeBoolean.ValueBoolean.of(true));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_DISPENSE, ValueTypeBoolean.ValueBoolean.of(false));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_OFFSET_X, ValueTypeDouble.ValueDouble.of(0.5D));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_OFFSET_Y, ValueTypeDouble.ValueDouble.of(0.5D));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_OFFSET_Z, ValueTypeDouble.ValueDouble.of(0.5D));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_LIFESPAN, ValueTypeInteger.ValueInteger.of(6000));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_DELAY_BEFORE_PICKUP, ValueTypeInteger.ValueInteger.of(10));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_VELOCITY, ValueTypeDouble.ValueDouble.of(0.1D));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_YAW, ValueTypeDouble.ValueDouble.of(0D));
            PROPERTIES_ENTITYITEM_PLACE.setValue(PROP_PITCH, ValueTypeDouble.ValueDouble.of(0D));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Fluid.FluidTarget>
                PROP_BOOLEAN_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Fluid.FluidTarget>() {
            @Override
            public Fluid.FluidTarget getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) {
                return Fluid.FluidTarget.of(input.getLeft(), input.getMiddle(), net.minecraftforge.fluids.Fluid.BUCKET_VOLUME,
                        input.getRight() ? TunnelFluidHelpers.MATCH_ALL : TunnelFluidHelpers.MATCH_NONE);
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, FluidStack>, Fluid.FluidTarget>
                PROP_FLUIDSTACK_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, FluidStack>, Fluid.FluidTarget>() {
            @Override
            public Fluid.FluidTarget getOutput(Triple<PartTarget, IAspectProperties, FluidStack> input) {
                IAspectProperties properties = input.getMiddle();
                boolean checkNbt = properties.getValue(Fluid.PROP_CHECK_NBT).getRawValue();
                return Fluid.FluidTarget.of(input.getLeft(), input.getMiddle(), net.minecraftforge.fluids.Fluid.BUCKET_VOLUME,
                        TunnelFluidHelpers.matchFluidStack(input.getRight(), false, checkNbt));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, Fluid.FluidTarget>
                PROP_FLUIDSTACKLIST_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, Fluid.FluidTarget>() {
            @Override
            public Fluid.FluidTarget getOutput(Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList> input) throws EvaluationException {
                ValueTypeList.ValueList list = input.getRight();
                if (list.getRawValue().getValueType() != ValueTypes.OBJECT_FLUIDSTACK) {
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.VALUETYPE_ERROR_INVALIDLISTVALUETYPE,
                            ValueTypes.OBJECT_FLUIDSTACK, list.getRawValue().getValueType()).localize());
                }
                IAspectProperties properties = input.getMiddle();
                boolean checkNbt = properties.getValue(Fluid.PROP_CHECK_NBT).getRawValue();
                return Fluid.FluidTarget.of(input.getLeft(), input.getMiddle(), net.minecraftforge.fluids.Fluid.BUCKET_VOLUME,
                        TunnelFluidHelpers.matchFluidStacks(list.getRawValue(), false, checkNbt));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, Fluid.FluidTarget>
                PROP_FLUIDSTACKPREDICATE_FLUIDTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, Fluid.FluidTarget>() {
            @Override
            public Fluid.FluidTarget getOutput(Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator> input) throws EvaluationException {
                IOperator predicate = input.getRight().getRawValue();
                if (predicate.getInputTypes().length == 1 && ValueHelpers.correspondsTo(predicate.getInputTypes()[0], ValueTypes.OBJECT_FLUIDSTACK)) {
                    return Fluid.FluidTarget.of(input.getLeft(), input.getMiddle(), net.minecraftforge.fluids.Fluid.BUCKET_VOLUME,
                            TunnelFluidHelpers.matchPredicate(input.getLeft(), predicate));
                } else {
                    String current = ValueTypeOperator.getSignature(predicate);
                    String expected = ValueTypeOperator.getSignature(new IValueType[]{ValueTypes.OBJECT_FLUIDSTACK}, ValueTypes.BOOLEAN);
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            expected, current).localize());
                }
            }
        };

        public static final IAspectValuePropagator<Fluid.FluidTarget, Void>
                PROP_FLUIDSTACK_EXPORT = new IAspectValuePropagator<Fluid.FluidTarget, Void>() {
            @Override
            public Void getOutput(Fluid.FluidTarget input) {
                PartPos target = input.getPartTarget().getTarget();
                IFluidNetwork fluidNetwork = input.getFluidNetwork();
                final DimPos pos = target.getPos();
                if (pos.isLoaded() && input.getFluidNetwork() != null) {
                    TunnelFluidHelpers.placeFluids(fluidNetwork, pos.getWorld(), pos.getBlockPos(),
                            input.getFluidStackMatcher(), input.getProperties().getValue(PROP_BLOCK_UPDATE).getRawValue(),
                            input.getProperties().getValue(PROP_IGNORE_REPLACABLE).getRawValue());
                }
                return null;

            }
        };

        public static final IAspectValuePropagator<Fluid.FluidTarget, Void>
                PROP_FLUIDSTACK_IMPORT = new IAspectValuePropagator<Fluid.FluidTarget, Void>() {
            @Override
            public Void getOutput(Fluid.FluidTarget input) {
                PartPos target = input.getPartTarget().getTarget();
                IFluidNetwork fluidNetwork = input.getFluidNetwork();
                final DimPos pos = target.getPos();
                if (pos.isLoaded() && input.getFluidNetwork() != null) {
                    TunnelFluidHelpers.pickUpFluids(target.getPos().getWorld(), target.getPos().getBlockPos(),
                            target.getSide(), fluidNetwork, input.getFluidStackMatcher());
                }
                return null;
            }
        };

        public static <T> IAspectValuePropagator<Triple<PartTarget, IAspectProperties, T>, Triple<PartTarget, IAspectProperties, T>>
            ignoreStackSize() {
            return new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, T>, Triple<PartTarget, IAspectProperties, T>>() {
                @Override
                public Triple<PartTarget, IAspectProperties, T> getOutput(Triple<PartTarget, IAspectProperties, T> input) {
                    IAspectProperties aspectProperties = input.getMiddle().clone();
                    aspectProperties.setValue(Item.PROP_CHECK_STACKSIZE, ValueTypeBoolean.ValueBoolean.of(false));
                    aspectProperties.setValue(Item.PROP_RATE, ValueTypeInteger.ValueInteger.of(1));
                    return Triple.of(input.getLeft(), aspectProperties, input.getRight());
                }
            };
        }
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Item.ItemTarget>
                PROP_BOOLEAN_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Item.ItemTarget>() {
            @Override
            public Item.ItemTarget getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) {
                return Item.ItemTarget.of(input.getLeft(), input.getMiddle(), 1,
                        input.getRight() ? TunnelItemHelpers.MATCH_BLOCK : TunnelItemHelpers.MATCH_NONE,
                        input.getRight() ? 1 : 0);
            }
        };

        public static final IAspectValuePropagator<Item.ItemTarget, Void>
                PROP_ITEMBLOCK_EXPORT = new IAspectValuePropagator<Item.ItemTarget, Void>() {
            @Override
            public Void getOutput(Item.ItemTarget input) {
                PartPos target = input.getPartTarget().getTarget();
                IItemNetwork itemNetwork = input.getItemNetwork();
                if (target.getPos().isLoaded() && itemNetwork != null) {
                    EnumHand hand = input.getProperties().getValue(PROP_HAND_LEFT).getRawValue()
                            ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    boolean blockUpdate = input.getProperties().getValue(PROP_BLOCK_UPDATE).getRawValue();
                    boolean ignoreReplacable = input.getProperties().getValue(PROP_IGNORE_REPLACABLE).getRawValue();
                    TunnelItemHelpers.placeItems(input.getConnectionHash(), itemNetwork,
                            input.getInventoryStateNetwork(), input.getItemNetworkSlotless(),
                            target.getPos().getWorld(), target.getPos().getBlockPos(), target.getSide(),
                            input.getItemStackMatcher(), hand, blockUpdate, ignoreReplacable);
                }
                return null;

            }
        };

        public static final IAspectValuePropagator<Item.ItemTarget, Void>
                PROP_ITEMBLOCK_IMPORT = new IAspectValuePropagator<Item.ItemTarget, Void>() {
            @Override
            public Void getOutput(Item.ItemTarget input) {
                PartPos target = input.getPartTarget().getTarget();
                IItemNetwork itemNetwork = input.getItemNetwork();
                if (target.getPos().isLoaded() && itemNetwork != null) {
                    EnumHand hand = input.getProperties().getValue(PROP_HAND_LEFT).getRawValue()
                            ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
                    boolean blockUpdate = input.getProperties().getValue(PROP_BLOCK_UPDATE).getRawValue();
                    boolean ignoreReplacable = input.getProperties().getValue(PROP_IGNORE_REPLACABLE).getRawValue();
                    int fortune = 0;
                    boolean silkTouch = input.getProperties().getValue(PROP_SILK_TOUCH).getRawValue();
                    boolean breakOnNoDrops = input.getProperties().getValue(PROP_BREAK_ON_NO_DROPS).getRawValue();
                    TunnelItemHelpers.pickUpItems(input.getConnectionHash(),
                            target.getPos().getWorld(), target.getPos().getBlockPos(), target.getSide(),
                            itemNetwork, input.getInventoryStateNetwork(), input.getItemNetworkSlotless(),
                            input.getItemStackMatcher(), hand, blockUpdate, ignoreReplacable,
                            fortune, silkTouch, breakOnNoDrops);
                }
                return null;

            }
        };

        public static IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>, Item.ItemTarget>
        newPropEntityItemItemTarget(final boolean doImport) {
            return new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>, Item.ItemTarget>() {
                @Override
                public Item.ItemTarget getOutput(Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>> input) {
                    PartTarget partTarget = input.getLeft();
                    IAspectProperties properties = input.getMiddle();
                    int amount = input.getRight().getMiddle();
                    int transferHash = input.getRight().getRight();
                    ItemStackPredicate itemStackMatcher = input.getRight().getLeft();

                    PartPos center = partTarget.getCenter();
                    PartPos target = partTarget.getTarget();
                    INetwork network = NetworkHelpers.getNetwork(center.getPos().getWorld(), center.getPos().getBlockPos());
                    IItemHandler itemHandler;
                    if (doImport) {
                        boolean ignorePickupDelay = properties.getValue(PROP_IGNORE_PICK_UP_DELAY).getRawValue();
                        itemHandler = new ItemHandlerWorldEntityImportWrapper((WorldServer) target.getPos().getWorld(),
                                target.getPos().getBlockPos(), ignorePickupDelay
                        );
                    } else {
                        double offsetX = properties.getValue(PROP_OFFSET_X).getRawValue();
                        double offsetY = properties.getValue(PROP_OFFSET_Y).getRawValue();
                        double offsetZ = properties.getValue(PROP_OFFSET_Z).getRawValue();
                        int lifespan = properties.getValue(PROP_LIFESPAN).getRawValue();
                        int delayBeforePickup = properties.getValue(PROP_DELAY_BEFORE_PICKUP).getRawValue();
                        EnumFacing facing = center.getSide();
                        double velocity = properties.getValue(PROP_VELOCITY).getRawValue();
                        double yaw = properties.getValue(PROP_YAW).getRawValue();
                        double pitch = properties.getValue(PROP_PITCH).getRawValue();
                        boolean dispense = properties.getValue(PROP_DISPENSE).getRawValue();
                        itemHandler = new ItemHandlerWorldEntityExportWrapper(
                                (WorldServer) target.getPos().getWorld(),
                                target.getPos().getBlockPos(), offsetX, offsetY, offsetZ,
                                lifespan, delayBeforePickup, facing, velocity, yaw, pitch,
                                dispense, network.getCapability(Capabilities.SLOTLESS_ITEMHANDLER)
                        );
                    }
                    int slot = properties.getValue(Item.PROP_SLOT).getRawValue();
                    return new Item.ItemTarget(network, itemHandler, null, null, target.hashCode(), slot, amount,
                            itemStackMatcher, transferHash, partTarget, properties);
                }
            };
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>, Item.ItemTarget>
                PROP_ENTITYITEM_ITEMTARGET_IMPORT = newPropEntityItemItemTarget(true);
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Triple<ItemStackPredicate, Integer, Integer>>, Item.ItemTarget>
                PROP_ENTITYITEM_ITEMTARGET_EXPORT = newPropEntityItemItemTarget(false);

    }

    public static <N extends IPositionedAddonsNetwork, T> IAspectWriteActivator
    createPositionedNetworkAddonActivator(final Function<Void, Capability<N>> networkCapability, final Capability<T> targetCapability) {
        return new IAspectWriteActivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType, PartTarget target, S state) {
                state.addVolatileCapability(targetCapability, (T) state);
                DimPos pos = target.getCenter().getPos();
                INetwork network = NetworkHelpers.getNetwork(pos.getWorld(), pos.getBlockPos());
                if (network != null && network.hasCapability(networkCapability.apply(null))) {
                    ((PartStatePositionedAddon<?, N>) state).setPositionedAddonsNetwork(network.getCapability(networkCapability.apply(null)));
                }
            }
        };
    }

    public static <N extends IPositionedAddonsNetwork, T> IAspectWriteDeactivator
    createPositionedNetworkAddonDeactivator(final Function<Void, Capability<N>> networkCapability, final Capability<T> targetCapability) {
        return new IAspectWriteDeactivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType, PartTarget target, S state) {
                state.removeVolatileCapability(targetCapability);
                DimPos pos = target.getCenter().getPos();
                INetwork network = NetworkHelpers.getNetwork(pos.getWorld(), pos.getBlockPos());
                if (network != null && network.hasCapability(networkCapability.apply(null))) {
                    ((PartStatePositionedAddon<?, N>) state).setPositionedAddonsNetwork(network.getCapability(networkCapability.apply(null)));
                }
            }
        };
    }

}
