package org.cyclops.integratedtunnels.part;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;
import org.cyclops.integratedtunnels.core.ExtendedFakePlayer;
import org.cyclops.integratedtunnels.core.ItemHandlerPlayerWrapper;

/**
 * A part state for holding a temporary player inventory.
 * @author rubensworks
 */
public class PartStatePlayerSimulator extends PartStateWriterBase<PartTypePlayerSimulator> {

    private ExtendedFakePlayer player = null;

    public PartStatePlayerSimulator(int inventorySize) {
        super(inventorySize);
    }

    public ExtendedFakePlayer getPlayer() {
        return player;
    }

    public void update(PartTarget target) {
        World world = target.getTarget().getPos().getWorld();
        if (!world.isRemote) {
            if (player == null) {
                player = new ExtendedFakePlayer((WorldServer) world);
            }
            ItemHandlerPlayerWrapper.cancelDestroyingBlock(player);
        }
    }

    @Override
    protected int getDefaultUpdateInterval() {
        return 10;
    }
}
