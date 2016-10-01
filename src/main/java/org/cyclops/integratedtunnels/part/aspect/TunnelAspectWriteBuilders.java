package org.cyclops.integratedtunnels.part.aspect;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
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
import org.cyclops.integrateddynamics.part.aspect.write.AspectWriteBuilders;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.TunnelEnergyHelpers;
import org.cyclops.integratedtunnels.core.TunnelItemHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

/**
 * Collection of tunnel aspect write builders and value propagators.
 * @author rubensworks
 */
public class TunnelAspectWriteBuilders {

    public static final class Energy {

        public static final IAspectWriteActivator ACTIVATOR = createPositionedNetworkAddonActivator(Capabilities.NETWORK_ENERGY, CapabilityEnergy.ENERGY);
        public static final IAspectWriteDeactivator DEACTIVATOR = createPositionedNetworkAddonDeactivator(Capabilities.NETWORK_ENERGY, CapabilityEnergy.ENERGY);

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("energy").handle(AspectWriteBuilders.PROP_GET_BOOLEAN);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("energy").handle(AspectWriteBuilders.PROP_GET_INTEGER);

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RATE =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.energy.rate.name");
        public static final IAspectProperties PROPERTIES = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE
        ));
        static {
            PROPERTIES.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(Integer.MAX_VALUE));
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
                IEnergyStorage energyStorage = EnergyHelpers.getEnergyStorage(target.getPos().getWorld(), target.getPos().getBlockPos(), target.getSide());
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

        public static final IAspectWriteActivator ACTIVATOR = createPositionedNetworkAddonActivator(ItemNetworkConfig.CAPABILITY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        public static final IAspectWriteDeactivator DEACTIVATOR = createPositionedNetworkAddonDeactivator(ItemNetworkConfig.CAPABILITY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

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

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>
                PROP_BOOLEAN_GETRATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Integer> getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) {
                return Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? input.getMiddle().getValue(PROP_RATE).getRawValue() : 0);
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, ItemTarget>
                PROP_INTEGER_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, ItemTarget>() {
            @Override
            public ItemTarget getOutput(Triple<PartTarget, IAspectProperties, Integer> input) {
                return ItemTarget.of(input.getLeft(), input.getMiddle(), input.getRight(), TunnelItemHelpers.MATCH_ALL, input.getRight());
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ItemStack>, ItemTarget>
                PROP_ITEMSTACK_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ItemStack>, ItemTarget>() {
            @Override
            public ItemTarget getOutput(Triple<PartTarget, IAspectProperties, ItemStack> input) {
                IAspectProperties properties = input.getMiddle();
                int rate = properties.getValue(PROP_RATE).getRawValue();
                boolean checkStackSize = properties.getValue(PROP_CHECK_STACKSIZE).getRawValue();
                boolean checkDamage = properties.getValue(PROP_CHECK_DAMAGE).getRawValue();
                boolean checkNbt = properties.getValue(PROP_CHECK_NBT).getRawValue();
                return ItemTarget.of(input.getLeft(), input.getMiddle(), rate,
                        TunnelItemHelpers.matchItemStack(input.getRight(), checkStackSize, checkDamage, checkNbt),
                        TunnelItemHelpers.getItemStackHashCode(input.getRight()));
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, ItemTarget>
                PROP_ITEMSTACKLIST_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList>, ItemTarget>() {
            @Override
            public ItemTarget getOutput(Triple<PartTarget, IAspectProperties, ValueTypeList.ValueList> input) throws EvaluationException {
                ValueTypeList.ValueList list = input.getRight();
                if (list.getRawValue().getValueType() != ValueTypes.OBJECT_ITEMSTACK) {
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            ValueTypes.OBJECT_ITEMSTACK, list.getRawValue().getValueType()).localize());
                }
                IAspectProperties properties = input.getMiddle();
                int rate = properties.getValue(PROP_RATE).getRawValue();
                boolean checkStackSize = properties.getValue(PROP_CHECK_STACKSIZE).getRawValue();
                boolean checkDamage = properties.getValue(PROP_CHECK_DAMAGE).getRawValue();
                boolean checkNbt = properties.getValue(PROP_CHECK_NBT).getRawValue();
                return ItemTarget.of(input.getLeft(), input.getMiddle(), rate,
                        TunnelItemHelpers.matchItemStacks(list.getRawValue(), checkStackSize, checkDamage, checkNbt),
                        list.getRawValue().hashCode());
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, ItemTarget>
                PROP_ITEMSTACKPREDICATE_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator>, ItemTarget>() {
            @Override
            public ItemTarget getOutput(Triple<PartTarget, IAspectProperties, ValueTypeOperator.ValueOperator> input) throws EvaluationException {
                IOperator predicate = input.getRight().getRawValue();
                if (predicate.getInputTypes().length == 1 && ValueHelpers.correspondsTo(predicate.getInputTypes()[0], ValueTypes.OBJECT_ITEMSTACK)) {
                    IAspectProperties properties = input.getMiddle();
                    int rate = properties.getValue(PROP_RATE).getRawValue();
                    return ItemTarget.of(input.getLeft(), input.getMiddle(), rate,
                            TunnelItemHelpers.matchPredicate(input.getLeft(), predicate),
                            predicate.hashCode());
                } else {
                    String current = ValueTypeOperator.getSignature(predicate);
                    String expected = ValueTypeOperator.getSignature(new IValueType[]{ValueTypes.OBJECT_ITEMSTACK}, ValueTypes.BOOLEAN);
                    throw new EvaluationException(new L10NHelpers.UnlocalizedString(L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            expected, current).localize());
                }
            }
        };

        public static final IAspectValuePropagator<ItemTarget, Void>
                PROP_EXPORT = new IAspectValuePropagator<ItemTarget, Void>() {
            @Override
            public Void getOutput(ItemTarget input) {
                if (input.getItemNetwork() != null && input.getItemStorage() != null && input.getAmount() != 0) {
                    TunnelItemHelpers.moveItemsStateOptimized(
                            input.getConnectionHash(), input.getItemNetwork(), input.getInventoryStateNetwork(), -1,
                            input.getItemStorage(), input.getInventoryStateStorage(), input.getSlot(),
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
                            input.getConnectionHash(), input.getItemStorage(), input.getInventoryStateStorage(), input.getSlot(),
                            input.getItemNetwork(), input.getInventoryStateNetwork(), -1,
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
            private final int connectionHash;
            private final int slot;
            private final int amount;
            private final Predicate<ItemStack> itemStackMatcher;

            public static ItemTarget of(PartTarget partTarget, IAspectProperties properties, int amount,
                                        Predicate<ItemStack> itemStackMatcher, int transferHash) {
                PartPos center = partTarget.getCenter();
                PartPos target = partTarget.getTarget();
                INetwork network = NetworkHelpers.getNetwork(center.getPos().getWorld(), center.getPos().getBlockPos());
                IItemHandler itemHandler = TileHelpers.getCapability(target.getPos(), target.getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                int slot = properties.getValue(PROP_SLOT).getRawValue();
                return new ItemTarget(
                        network.getCapability(ItemNetworkConfig.CAPABILITY), itemHandler,
                        network.hasCapability(Capabilities.INVENTORY_STATE) ? network.getCapability(Capabilities.INVENTORY_STATE) : null,
                        TileHelpers.getCapability(target.getPos(), target.getSide(), Capabilities.INVENTORY_STATE),
                        target.hashCode(), slot, amount, itemStackMatcher, transferHash);
            }

            public ItemTarget(IItemNetwork itemNetwork, IItemHandler itemStorage,
                              IInventoryState inventoryStateNetwork, IInventoryState inventoryStateStorage,
                              int storagePosHash, int slot,
                              int amount, Predicate<ItemStack> itemStackMatcher, int transferHash) {
                this.itemNetwork = itemNetwork;
                this.itemStorage = itemStorage;
                this.inventoryStateNetwork = inventoryStateNetwork;
                this.inventoryStateStorage = inventoryStateStorage;
                this.connectionHash = transferHash << 4 + storagePosHash + itemNetwork.hashCode();
                this.slot = slot;
                this.amount = amount;
                this.itemStackMatcher = itemStackMatcher;
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

            public int getConnectionHash() {
                return connectionHash;
            }

            public int getSlot() {
                return slot;
            }

            public int getAmount() {
                return amount;
            }

            public Predicate<ItemStack> getItemStackMatcher() {
                return itemStackMatcher;
            }
        }

    }

    public static <N extends IPositionedAddonsNetwork, T> IAspectWriteActivator
    createPositionedNetworkAddonActivator(final Capability<N> networkCapability, final Capability<T> targetCapability) {
        return new IAspectWriteActivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType, PartTarget target, S state) {
                state.addVolatileCapability(targetCapability, (T) state);
                DimPos pos = target.getCenter().getPos();
                INetwork network = NetworkHelpers.getNetwork(pos.getWorld(), pos.getBlockPos());
                if (network != null && network.hasCapability(networkCapability)) {
                    ((PartStatePositionedAddon<?, N>) state).setPositionedAddonsNetwork(network.getCapability(networkCapability));
                }
            }
        };
    }

    public static <N extends IPositionedAddonsNetwork, T> IAspectWriteDeactivator
    createPositionedNetworkAddonDeactivator(final Capability<N> networkCapability, final Capability<T> targetCapability) {
        return new IAspectWriteDeactivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType, PartTarget target, S state) {
                state.removeVolatileCapability(targetCapability);
                DimPos pos = target.getCenter().getPos();
                INetwork network = NetworkHelpers.getNetwork(pos.getWorld(), pos.getBlockPos());
                if (network != null && network.hasCapability(networkCapability)) {
                    ((PartStatePositionedAddon<?, N>) state).setPositionedAddonsNetwork(network.getCapability(networkCapability));
                }
            }
        };
    }

}
