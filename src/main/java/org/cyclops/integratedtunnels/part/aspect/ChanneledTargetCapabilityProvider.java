package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * A helper class for movement targets with a certain network type and a capability provider as target.
 * @author rubensworks
 */
public abstract class ChanneledTargetCapabilityProvider<N extends IPositionedAddonsNetwork, T, M> extends ChanneledTarget<N> {

    private final ICapabilityProvider capabilityProvider;
    private final EnumFacing side;

    private IIngredientComponentStorage<T, M> storage = null;

    public ChanneledTargetCapabilityProvider(@Nullable ICapabilityProvider capabilityProvider, EnumFacing side,
                                             N channeledNetwork, PartStateRoundRobin<?> partState, int channel,
                                             boolean roundRobin) {
        super(channeledNetwork, partState, channel, roundRobin);
        this.capabilityProvider = capabilityProvider;
        this.side = side;
    }

    @Override
    public boolean hasValidTarget() {
        return capabilityProvider != null;
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
