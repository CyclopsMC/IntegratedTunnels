package org.cyclops.integratedtunnels.core.part;

import org.cyclops.integrateddynamics.api.part.PartRenderPosition;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;

/**
 * Base part for a tunnels with aspects.
 * @author rubensworks
 */
public abstract class PartTypeTunnelAspectsWorld<P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> extends PartTypeTunnelAspects<P, S> {

    public PartTypeTunnelAspectsWorld(String name) {
        super(name, new PartRenderPosition(0.1875F, 0.1875F, 0.625F, 0.625F));
    }

}
