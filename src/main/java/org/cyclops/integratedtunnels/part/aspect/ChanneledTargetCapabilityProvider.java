package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.core.Direction;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.ICapabilityGetter;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * A helper class for movement targets with a certain network type and a capability provider as target.
 * @author rubensworks
 */
public abstract class ChanneledTargetCapabilityProvider<C, N extends IPositionedAddonsNetwork, T, M> extends ChanneledTarget<N, T> {

    private final Class<?> capabilityType;
    private final ICapabilityGetter<Direction> capabilityGetter;
    private final Direction side;

    private IIngredientComponentStorage<T, M> storage = null;

    public ChanneledTargetCapabilityProvider(INetwork network, Class<?> capabilityType, @Nullable ICapabilityGetter<Direction> capabilityGetter, Direction side,
                                             N channeledNetwork, @Nullable PartStateRoundRobin<?> partState, int channel,
                                             boolean roundRobin, boolean craftIfFailed, boolean passiveIO) {
        super(network, channeledNetwork, partState, channel, roundRobin, craftIfFailed, passiveIO);
        this.capabilityType = capabilityType;
        this.capabilityGetter = capabilityGetter;
        this.side = side;
    }

    @Override
    public boolean hasValidTarget() {
        return capabilityGetter != null && getPartState() != null;
    }

    protected abstract IngredientComponent<T, M> getComponent();

    public IIngredientComponentStorage<T, M> getStorage() {
        // Cache the storage
        if (storage == null) {
            storage = getComponent().getStorage(this.capabilityType, capabilityGetter, side);
        }
        return storage;
    }
}
