package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.part.aspect.build.AspectBuilder;
import org.cyclops.integrateddynamics.core.part.aspect.build.IAspectValuePropagator;
import org.cyclops.integrateddynamics.part.aspect.read.AspectReadBuilders;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.part.aspect.listproxy.ValueTypeListProxyPositionedFluidNetwork;
import org.cyclops.integratedtunnels.part.aspect.listproxy.ValueTypeListProxyPositionedItemNetwork;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class TunnelAspectReadBuilders {

    public static final class Network {

        public static <T, M> Optional<IIngredientComponentStorage<T, M>> getChannel(NetworkCapability<? extends IPositionedAddonsNetworkIngredients<T, M>> networkCapability,
                                                                                    DimPos dimPos, Direction side, int channel) {
            INetwork network = NetworkHelpers.getNetwork(dimPos.getLevel(true), dimPos.getBlockPos(), side).orElse(null);
            return Optional.ofNullable(network != null ? network.getCapability(networkCapability)
                    .map(itemNetwork -> {
                        itemNetwork.scheduleObservation();
                        return itemNetwork.getChannel(channel);
                    })
                    .orElse(null) : null);
        }

        public static <T, M> Optional<IIngredientPositionsIndex<T, M>> getChannelIndex(NetworkCapability<? extends IPositionedAddonsNetworkIngredients<T, M>> networkCapability,
                                                                                       DimPos dimPos, Direction side, int channel) {
            INetwork network = NetworkHelpers.getNetwork(dimPos.getLevel(true), dimPos.getBlockPos(), side).orElse(null);
            return Optional.ofNullable(network != null ? network.getCapability(networkCapability)
                    .map(itemNetwork -> {
                        itemNetwork.scheduleObservation();
                        return itemNetwork.getChannelIndex(channel);
                    })
                    .orElse(null) : null);
        }

        public static final class Item {
            public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<PartTarget, IAspectProperties>>
                    BUILDER_LIST = AspectReadBuilders.BUILDER_LIST.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("itemnetwork");
            public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>>
                    BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("itemnetwork");
            public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>>
                    BUILDER_LONG = AspectReadBuilders.BUILDER_LONG.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("itemnetwork");
            public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Pair<PartTarget, IAspectProperties>>
                    BUILDER_OPERATOR = AspectReadBuilders.BUILDER_OPERATOR.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("itemnetwork");

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientComponentStorage<ItemStack, Integer>> PROP_GET_CHANNEL = input -> {
                int channel = input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue();
                return getChannel(Capabilities.ItemNetwork.NETWORK, input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide(), channel).orElse(null);
            };
            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientPositionsIndex<ItemStack, Integer>> PROP_GET_CHANNELINDEX = input -> {
                int channel = input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue();
                return getChannelIndex(Capabilities.ItemNetwork.NETWORK, input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide(), channel).orElse(null);
            };

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>
                    PROP_GET_LIST = input -> ValueTypeList.ValueList.ofFactory(new ValueTypeListProxyPositionedItemNetwork(
                    input.getLeft().getTarget().getPos(),
                    input.getLeft().getTarget().getSide(),
                    input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue()));
        }

        public static final class Fluid {
            public static final AspectBuilder<ValueTypeList.ValueList, ValueTypeList, Pair<PartTarget, IAspectProperties>>
                    BUILDER_LIST = AspectReadBuilders.BUILDER_LIST.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("fluidnetwork");
            public static final AspectBuilder<ValueTypeInteger.ValueInteger, ValueTypeInteger, Pair<PartTarget, IAspectProperties>>
                    BUILDER_INTEGER = AspectReadBuilders.BUILDER_INTEGER.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("fluidnetwork");
            public static final AspectBuilder<ValueTypeLong.ValueLong, ValueTypeLong, Pair<PartTarget, IAspectProperties>>
                    BUILDER_LONG = AspectReadBuilders.BUILDER_LONG.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("fluidnetwork");
            public static final AspectBuilder<ValueTypeOperator.ValueOperator, ValueTypeOperator, Pair<PartTarget, IAspectProperties>>
                    BUILDER_OPERATOR = AspectReadBuilders.BUILDER_OPERATOR.byMod(IntegratedTunnels._instance)
                    .withProperties(AspectReadBuilders.Network.PROPERTIES)
                    .appendKind("fluidnetwork");

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientComponentStorage<FluidStack, Integer>> PROP_GET_CHANNEL = input -> {
                int channel = input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue();
                return getChannel(Capabilities.FluidNetwork.NETWORK, input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide(), channel).orElse(null);
            };
            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, IIngredientPositionsIndex<FluidStack, Integer>> PROP_GET_CHANNELINDEX = input -> {
                int channel = input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue();
                return getChannelIndex(Capabilities.FluidNetwork.NETWORK, input.getLeft().getTarget().getPos(), input.getLeft().getTarget().getSide(), channel).orElse(null);
            };

            public static final IAspectValuePropagator<Pair<PartTarget, IAspectProperties>, ValueTypeList.ValueList>
                    PROP_GET_LIST = input -> ValueTypeList.ValueList.ofFactory(new ValueTypeListProxyPositionedFluidNetwork(
                    input.getLeft().getTarget().getPos(),
                    input.getLeft().getTarget().getSide(),
                    input.getRight().getValue(AspectReadBuilders.Network.PROPERTY_CHANNEL).getRawValue()));
        }
    }

}
