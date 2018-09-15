package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class EnergyTargetCapabilityProvider extends ChanneledTarget<IEnergyNetwork> implements IEnergyTarget {

    private final ICapabilityProvider capabilityProvider;
    private final EnumFacing side;
    private final int amount;
    private final boolean exactAmount;

    public EnergyTargetCapabilityProvider(@Nullable ICapabilityProvider capabilityProvider, EnumFacing side, INetwork network, int channel,
                                          int amount, boolean exactAmount,
                                          boolean roundRobin, PartStateRoundRobin<?> partStateEnergy) {
        super(network.getCapability(Capabilities.NETWORK_ENERGY), partStateEnergy, channel, roundRobin);
        this.capabilityProvider = capabilityProvider;
        this.side = side;
        this.amount = amount;
        this.exactAmount = exactAmount;
    }

    @Override
    public boolean hasValidTarget() {
        return capabilityProvider != null;
    }

    @Override
    public IIngredientComponentStorage<Integer, Boolean> getEnergyChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IIngredientComponentStorage<Integer, Boolean> getEnergyStorage() {
        return IngredientComponent.ENERGY.getStorage(capabilityProvider, side);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isExactAmount() {
        return exactAmount;
    }
}
