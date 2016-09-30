package org.cyclops.integratedtunnels.part.aspect;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
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

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RATE =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.item.rate.name");
        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_SLOT =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.item.slot.name");
        public static final IAspectProperties PROPERTIES = new AspectProperties(ImmutableList.<IAspectPropertyTypeInstance>of(
                PROP_RATE,
                PROP_SLOT
        ));
        static {
            PROPERTIES.setValue(PROP_RATE, ValueTypeInteger.ValueInteger.of(64));
            PROPERTIES.setValue(PROP_SLOT, ValueTypeInteger.ValueInteger.of(-1));
        }

        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>
                PROP_GETRATE = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Boolean>, Triple<PartTarget, IAspectProperties, Integer>>() {
            @Override
            public Triple<PartTarget, IAspectProperties, Integer> getOutput(Triple<PartTarget, IAspectProperties, Boolean> input) {
                return Triple.of(input.getLeft(), input.getMiddle(), input.getRight() ? input.getMiddle().getValue(PROP_RATE).getRawValue() : 0);
            }
        };
        public static final IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, ItemTarget>
                PROP_ITEMTARGET = new IAspectValuePropagator<Triple<PartTarget, IAspectProperties, Integer>, ItemTarget>() {
            @Override
            public ItemTarget getOutput(Triple<PartTarget, IAspectProperties, Integer> input) {
                PartPos center = input.getLeft().getCenter();
                PartPos target = input.getLeft().getTarget();
                INetwork network = NetworkHelpers.getNetwork(center.getPos().getWorld(), center.getPos().getBlockPos());
                IItemHandler itemHandler = TileHelpers.getCapability(target.getPos(), target.getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                int slot = input.getMiddle().getValue(PROP_SLOT).getRawValue();
                return new ItemTarget(
                        network.getCapability(ItemNetworkConfig.CAPABILITY), itemHandler,
                        network.hasCapability(Capabilities.INVENTORY_STATE) ? network.getCapability(Capabilities.INVENTORY_STATE) : null, TileHelpers.getCapability(target.getPos(), target.getSide(), Capabilities.INVENTORY_STATE),
                        target.hashCode(), slot, input.getRight());
            }
        };
        public static final IAspectValuePropagator<ItemTarget, Void>
                PROP_EXPORT = new IAspectValuePropagator<ItemTarget, Void>() {
            @Override
            public Void getOutput(ItemTarget input) {
                if (input.getItemNetwork() != null && input.getItemStorage() != null && input.getAmount() != 0) {
                    TunnelItemHelpers.moveItemsStateOptimized(
                            input.getNetworkHash(), input.getItemNetwork(), input.getInventoryStateNetwork(), -1,
                            input.getStoragePosHash(), input.getItemStorage(), input.getInventoryStateStorage(), input.getSlot(),
                            input.getAmount());
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
                            input.getStoragePosHash(), input.getItemStorage(), input.getInventoryStateStorage(), input.getSlot(),
                            input.getNetworkHash(), input.getItemNetwork(), input.getInventoryStateNetwork(), -1,
                            input.getAmount());
                }
                return null;
            }
        };

        public static class ItemTarget {

            private final IItemNetwork itemNetwork;
            private final IItemHandler itemStorage;
            private final IInventoryState inventoryStateNetwork;
            private final IInventoryState inventoryStateStorage;
            private final int networkHash;
            private final int storagePosHash;
            private final int slot;
            private final int amount;

            public ItemTarget(IItemNetwork itemNetwork, IItemHandler itemStorage,
                              IInventoryState inventoryStateNetwork, IInventoryState inventoryStateStorage,
                              int storagePosHash, int slot, int amount) {
                this.itemNetwork = itemNetwork;
                this.itemStorage = itemStorage;
                this.inventoryStateNetwork = inventoryStateNetwork;
                this.inventoryStateStorage = inventoryStateStorage;
                this.networkHash = itemNetwork.hashCode();
                this.storagePosHash = storagePosHash;
                this.slot = slot;
                this.amount = amount;
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

            public int getNetworkHash() {
                return networkHash;
            }

            public int getStoragePosHash() {
                return storagePosHash;
            }

            public int getSlot() {
                return slot;
            }

            public int getAmount() {
                return amount;
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
