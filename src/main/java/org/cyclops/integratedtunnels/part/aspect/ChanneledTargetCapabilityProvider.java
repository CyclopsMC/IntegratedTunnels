package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * A helper class for movement targets with a certain network type and a capability provider as target.
 * @author rubensworks
 */
public abstract class ChanneledTargetCapabilityProvider<N extends IPositionedAddonsNetwork, T, M> extends ChanneledTarget<N, T> {

    private final ICapabilityProvider capabilityProvider;
    private final Direction side;

    private IIngredientComponentStorage<T, M> storage = null;

    public ChanneledTargetCapabilityProvider(INetwork network, @Nullable ICapabilityProvider capabilityProvider, Direction side,
                                             N channeledNetwork, @Nullable PartStateRoundRobin<?> partState, int channel,
                                             boolean roundRobin, boolean craftIfFailed, boolean passiveIO) {
        super(network, channeledNetwork, partState, channel, roundRobin, craftIfFailed, passiveIO);
        this.capabilityProvider = capabilityProvider;
        this.side = side;
    }

    @Override
    public boolean hasValidTarget() {
        return capabilityProvider != null && getPartState() != null;
    }

    protected abstract IngredientComponent<T, M> getComponent();

    public IIngredientComponentStorage<T, M> getStorage() {
        // Cache the storage
        if (storage == null) {
            storage = getComponent().getStorage(capabilityProvider, side);
        }
        return storage;
    }
}
