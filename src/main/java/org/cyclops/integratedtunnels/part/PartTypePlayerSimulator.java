package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can simulate player interactions.
 * @author rubensworks
 */
public class PartTypePlayerSimulator extends PartTypeTunnelAspectsWorld<PartTypePlayerSimulator, PartStatePlayerSimulator> {
    public PartTypePlayerSimulator(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Player.CLICK_EMPTY_BOOLEAN,
                TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN,
                TunnelAspects.Write.Player.CLICK_ITEM_INTEGER,
                TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK,
                TunnelAspects.Write.Player.CLICK_ITEM_LISTITEMSTACK,
                TunnelAspects.Write.Player.CLICK_ITEM_PREDICATEITEMSTACK,
                TunnelAspects.Write.Player.CLICK_ITEM_NBT
        ));
    }

    @Override
    protected PartStatePlayerSimulator constructDefaultState() {
        return new PartStatePlayerSimulator(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStatePlayerSimulator state) {
        return state.hasVariable() ? GeneralConfig.playerSimulatorBaseConsumptionEnabled : GeneralConfig.playerSimulatorBaseConsumptionDisabled;
    }

    @Override
    protected void onVariableContentsUpdated(IPartNetwork network, PartTarget target, PartStatePlayerSimulator state) {
        state.update(target);
        super.onVariableContentsUpdated(network, target, state);
    }

    @Override
    public void updateActivation(PartTarget target, PartStatePlayerSimulator state, EntityPlayer player) {
        state.update(target);
        super.updateActivation(target, state, player);
    }
}
