package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IEnergyTarget extends IChanneledTarget<IEnergyNetwork> {

    public IIngredientComponentStorage<Integer, Boolean> getEnergyChannel();

    public IIngredientComponentStorage<Integer, Boolean> getEnergyStorage();

    public int getAmount();

    public boolean isExactAmount();

    public static EnergyTargetCapabilityProvider ofTile(PartTarget partTarget, IAspectProperties properties, int amount) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos().getWorld().getTileEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new EnergyTargetCapabilityProvider(tile, target.getSide(), network,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                amount,
                properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                partState);
    }

    public static EnergyTargetCapabilityProvider ofEntity(PartTarget partTarget, @Nullable Entity entity,
                                                          IAspectProperties properties, int amount) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new EnergyTargetCapabilityProvider(entity, target.getSide(), network,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                amount,
                properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                partState);
    }

}
