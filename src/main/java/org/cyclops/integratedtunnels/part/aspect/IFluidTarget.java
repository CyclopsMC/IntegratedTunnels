package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IFluidTarget extends IChanneledTarget<IFluidNetwork, FluidStack> {

    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel();

    public IIngredientComponentStorage<FluidStack, Integer> getStorage();

    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static IFluidTarget ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                                     IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        BlockEntity tile = target.getPos().getLevel(true).getBlockEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetCapabilityProvider(transfer, network, tile, target.getSide(),
                fluidStackMatcher, partTarget, properties, partState);
    }

    public static IFluidTarget ofEntity(ITunnelTransfer transfer, PartTarget partTarget, @Nullable Entity entity,
                                                         IAspectProperties properties,
                                                         IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetCapabilityProvider(transfer, network, entity, target.getSide(),
                fluidStackMatcher, partTarget, properties, partState);
    }

    public static IFluidTarget ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                        IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetCapabilityProvider(transfer, network, null, target.getSide(),
                fluidStackMatcher, partTarget, properties, partState);
    }

    public static IFluidTarget ofStorage(ITunnelTransfer transfer, INetwork network, PartTarget partTarget,
                                               IAspectProperties properties,
                                               IngredientPredicate<FluidStack, Integer> fluidStackMatcher,
                                               IIngredientComponentStorage<FluidStack, Integer> storage) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new FluidTargetStorage(transfer, network, storage,
                fluidStackMatcher, partTarget, properties, partState);
    }

}
