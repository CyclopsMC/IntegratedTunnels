package org.cyclops.integratedtunnels.core.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartRenderPosition;
import org.cyclops.integrateddynamics.core.part.PartTypeConfigurable;
import org.cyclops.integratedtunnels.IntegratedTunnels;

/**
 * Base part for a tunnel.
 * @author rubensworks
 */
public abstract class PartTypeTunnel<P extends IPartType<P, S>, S extends IPartState<P>> extends PartTypeConfigurable<P, S> {

    public PartTypeTunnel(String name) {
        super(name, new PartRenderPosition(0.25F, 0.25F, 0.375F, 0.375F));
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartType.class; // TODO
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null; // TODO
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return null; // TODO
    }

    @Override
    public ModBase getMod() {
        return IntegratedTunnels._instance;
    }
}
