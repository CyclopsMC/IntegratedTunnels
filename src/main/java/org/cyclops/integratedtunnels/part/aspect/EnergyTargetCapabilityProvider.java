package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.ICapabilityGetter;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.Capabilities;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class EnergyTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IEnergyStorage, IEnergyNetwork, Long, Boolean>
        implements IEnergyTarget {

    private final long amount;
    private final boolean exactAmount;

    public EnergyTargetCapabilityProvider(Class<?> capabilityType, @Nullable ICapabilityGetter<Direction> capabilityGetter, Direction side, INetwork network,
                                          IAspectProperties properties,
                                          long amount, @Nullable PartStateRoundRobin<?> partStateEnergy) {
        super(network, capabilityType, capabilityGetter, side, network.getCapability(Capabilities.EnergyNetwork.NETWORK).orElse(null), partStateEnergy,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.amount = amount;
        this.exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue() || properties.getValue(TunnelAspectWriteBuilders.Energy.PROP_CHECK_AMOUNT).getRawValue(); // TODO: restore exact amount
    }

    @Override
    public IIngredientComponentStorage<Long, Boolean> getEnergyChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public boolean isExactAmount() {
        return exactAmount;
    }

    @Override
    protected IngredientComponent<Long, Boolean> getComponent() {
        return IngredientComponent.ENERGY;
    }
}
