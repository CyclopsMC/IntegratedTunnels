package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * @author rubensworks
 */
public class TunnelTransferEntities extends TunnelTransferComposite {

    public TunnelTransferEntities(List<? extends Entity> entities) {
        super(entities.stream().map(TunnelTransferEntity::new).toArray(TunnelTransferEntity[]::new));
    }
}
