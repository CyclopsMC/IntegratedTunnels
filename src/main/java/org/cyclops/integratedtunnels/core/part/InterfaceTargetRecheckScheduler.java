package org.cyclops.integratedtunnels.core.part;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;

import java.util.List;
import java.util.Map;

/**
 * Re-checks the target capabilities of interfaces whose target signalled a capability invalidation.
 *
 * Capabilities may appear or disappear without any neighbour change reaching the interface,
 * such as when a block entity changes its configuration or finishes initializing.
 * These invalidations are signalled while capabilities may not be queried yet,
 * so the re-check is deferred to the end of the server tick.
 *
 * @author rubensworks
 */
public final class InterfaceTargetRecheckScheduler {

    private static final Map<IPartTypeInterfacePositionedAddon.IState<?, ?, ?, ?>, IPartTypeInterfacePositionedAddon<?, ?, ?, ?>> PENDING = Maps.newIdentityHashMap();

    private InterfaceTargetRecheckScheduler() {

    }

    /**
     * Schedule a re-check of the target capability of the given interface at the end of the server tick.
     * @param partType The interface part type.
     * @param state The interface part state.
     */
    public static void schedule(IPartTypeInterfacePositionedAddon<?, ?, ?, ?> partType, IPartTypeInterfacePositionedAddon.IState<?, ?, ?, ?> state) {
        PENDING.put(state, partType);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (PENDING.isEmpty()) {
            return;
        }
        // Re-checks may trigger new invalidations, which are handled in the next tick.
        List<Map.Entry<IPartTypeInterfacePositionedAddon.IState<?, ?, ?, ?>, IPartTypeInterfacePositionedAddon<?, ?, ?, ?>>> entries = Lists.newArrayList(PENDING.entrySet());
        PENDING.clear();
        for (Map.Entry<IPartTypeInterfacePositionedAddon.IState<?, ?, ?, ?>, IPartTypeInterfacePositionedAddon<?, ?, ?, ?>> entry : entries) {
            recheck(entry.getValue(), entry.getKey());
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        PENDING.clear();
    }

    @SuppressWarnings("unchecked")
    private static <S extends IPartTypeInterfacePositionedAddon.IState> void recheck(IPartTypeInterfacePositionedAddon partType, S state) {
        // A neighbour change may have re-checked the target in the meantime.
        if (!state.isTargetCapabilityInvalidated()) {
            return;
        }
        INetwork network = state.getNetwork();
        PartPos center = state.getCenter();
        PartPos target = state.getPos();
        // Never load chunks for this, the re-check will be triggered again once they are loaded.
        if (network == null || center == null || !center.getPos().isLoaded()
                || (target != null && !target.getPos().isLoaded())) {
            return;
        }
        // Skip states of parts that were removed or replaced.
        PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(center);
        if (partStateHolder == null || partStateHolder.getState() != state) {
            return;
        }
        partType.updateTargetInNetwork(network, partType.getTarget(center, state), state.getPriority(), state.getChannelInterface(), state);
    }

}
