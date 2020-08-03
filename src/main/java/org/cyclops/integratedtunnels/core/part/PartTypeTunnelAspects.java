package org.cyclops.integratedtunnels.core.part;

import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.PartRenderPosition;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.part.write.PartTypeWriteBase;
import org.cyclops.integratedtunnels.IntegratedTunnels;

/**
 * Base part for a tunnels with aspects.
 * @author rubensworks
 */
public abstract class PartTypeTunnelAspects<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> extends PartTypeWriteBase<P, S> {

    public PartTypeTunnelAspects(String name) {
        this(name, new PartRenderPosition(0.25F, 0.25F, 0.375F, 0.375F));
    }

    protected PartTypeTunnelAspects(String name, PartRenderPosition partRenderPosition) {
        super(name, partRenderPosition);
    }

    @Override
    public ModBase getMod() {
        return IntegratedTunnels._instance;
    }

}
