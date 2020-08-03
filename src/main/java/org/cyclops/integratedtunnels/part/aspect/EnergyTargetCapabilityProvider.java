package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class EnergyTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IEnergyNetwork, Integer, Boolean>
        implements IEnergyTarget {

    private final int amount;
    private final boolean exactAmount;

    public EnergyTargetCapabilityProvider(@Nullable ICapabilityProvider capabilityProvider, Direction side, INetwork network,
                                          IAspectProperties properties,
                                          int amount, @Nullable PartStateRoundRobin<?> partStateEnergy) {
        super(network, capabilityProvider, side, network.getCapability(Capabilities.NETWORK_ENERGY).orElse(null), partStateEnergy,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue());
        this.amount = amount;
        this.exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue();
    }

    @Override
    public IIngredientComponentStorage<Integer, Boolean> getEnergyChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isExactAmount() {
        return exactAmount;
    }

    @Override
    protected IngredientComponent<Integer, Boolean> getComponent() {
        return IngredientComponent.ENERGY;
    }
}
