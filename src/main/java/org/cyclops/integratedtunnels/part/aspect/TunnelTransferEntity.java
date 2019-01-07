package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;

/**
 * @author rubensworks
 */
public class TunnelTransferEntity implements ITunnelTransfer {

    private final Entity entity;

    public TunnelTransferEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TunnelTransferEntity)) {
            return false;
        }
        TunnelTransferEntity that = (TunnelTransferEntity) obj;
        return this.entity.getEntityId() == that.entity.getEntityId();
    }

    @Override
    public int hashCode() {
        return entity.getEntityId();
    }
}
