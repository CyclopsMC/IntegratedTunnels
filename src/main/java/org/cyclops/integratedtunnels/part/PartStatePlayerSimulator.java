package org.cyclops.integratedtunnels.part;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integratedtunnels.core.ExtendedFakePlayer;
import org.cyclops.integratedtunnels.core.ItemStoragePlayerWrapper;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * A part state for holding a temporary player inventory.
 * @author rubensworks
 */
public class PartStatePlayerSimulator extends PartStateRoundRobin<PartTypePlayerSimulator> {

    private ExtendedFakePlayer player = null;

    public PartStatePlayerSimulator(int inventorySize) {
        super(inventorySize);
    }

    public @Nullable ExtendedFakePlayer getPlayer() {
        return player;
    }

    public void update(PartTarget target) {
        World world = target.getTarget().getPos().getWorld();
        if (!world.isRemote) {
            if (player == null) {
                player = new ExtendedFakePlayer((WorldServer) world);
            }
            ItemStoragePlayerWrapper.cancelDestroyingBlock(player);
        }
    }

    @Override
    protected int getDefaultUpdateInterval() {
        return 10;
    }
}
