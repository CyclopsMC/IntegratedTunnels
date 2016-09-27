package org.cyclops.integratedtunnels.part.aspect;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
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
import org.cyclops.integratedtunnels.core.TunnelEnergyHelpers;
import org.cyclops.integratedtunnels.part.PartStateEnergy;

/**
 * Collection of tunnel aspect write builders and value propagators.
 * @author rubensworks
 */
public class TunnelAspectWriteBuilders {

    public static final class Energy {

        public static final IAspectWriteActivator ACTIVATOR = new IAspectWriteActivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType, PartTarget target, S state) {
                state.addVolatileCapability(CapabilityEnergy.ENERGY, (IEnergyStorage) state);
                DimPos pos = target.getCenter().getPos();
                INetwork network = NetworkHelpers.getNetwork(pos.getWorld(), pos.getBlockPos());
                if (network != null && network.hasCapability(Capabilities.NETWORK_ENERGY)) {
                    ((PartStateEnergy) state).setEnergyNetwork(network.getCapability(Capabilities.NETWORK_ENERGY));
                }
            }
        };
        public static final IAspectWriteDeactivator DEACTIVATOR = new IAspectWriteDeactivator() {
            @Override
            public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType, PartTarget target, S state) {
                state.removeVolatileCapability(CapabilityEnergy.ENERGY);
                DimPos pos = target.getCenter().getPos();
                INetwork network = NetworkHelpers.getNetwork(pos.getWorld(), pos.getBlockPos());
                if (network != null && network.hasCapability(Capabilities.NETWORK_ENERGY)) {
                    ((PartStateEnergy) state).setEnergyNetwork(network.getCapability(Capabilities.NETWORK_ENERGY));
                }
            }
        };

        public static final AspectBuilder<ValueTypeBoolean.ValueBoolean, ValueTypeBoolean, Triple<PartTarget, IAspectProperties, Boolean>>
                BUILDER_BOOLEAN = AspectWriteBuilders.BUILDER_BOOLEAN.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("energy").handle(AspectWriteBuilders.PROP_GET_BOOLEAN);
        public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Triple<PartTarget, IAspectProperties, Integer>>
                BUILDER_INTEGER = AspectWriteBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                .appendActivator(ACTIVATOR).appendDeactivator(DEACTIVATOR)
                .appendKind("energy").handle(AspectWriteBuilders.PROP_GET_INTEGER);

        public static final IAspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger> PROP_RATE =
                new AspectPropertyTypeInstance<ValueTypeInteger, ValueTypeInteger.ValueInteger>(ValueTypes.INTEGER, "aspect.aspecttypes.integratedtunnels.integer.rate.name");
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

}
