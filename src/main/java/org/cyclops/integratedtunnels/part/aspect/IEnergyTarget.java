package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IEnergyTarget extends IChanneledTarget<IEnergyNetwork, Long> {

    public IIngredientComponentStorage<Long, Boolean> getEnergyChannel();

    public IIngredientComponentStorage<Long, Boolean> getStorage();

    public int getAmount();

    public boolean isExactAmount();

    public static IEnergyTarget ofTile(PartTarget partTarget, IAspectProperties properties, int amount) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos().getWorld(true).getTileEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new EnergyTargetCapabilityProvider(tile, target.getSide(), network, properties, amount, partState);
    }

    public static IEnergyTarget ofEntity(PartTarget partTarget, @Nullable Entity entity,
                                                          IAspectProperties properties, int amount) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new EnergyTargetCapabilityProvider(entity, target.getSide(), network, properties, amount, partState);
    }

}
