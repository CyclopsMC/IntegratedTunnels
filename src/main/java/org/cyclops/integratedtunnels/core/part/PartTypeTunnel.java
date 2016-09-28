package org.cyclops.integratedtunnels.core.part;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartRenderPosition;
import org.cyclops.integrateddynamics.core.client.gui.container.GuiPartSettings;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerPartSettings;
import org.cyclops.integrateddynamics.core.part.PartTypeBase;
import org.cyclops.integratedtunnels.IntegratedTunnels;

/**
 * Base part for a tunnel.
 * @author rubensworks
 */
public abstract class PartTypeTunnel<P extends IPartType<P, S>, S extends IPartState<P>> extends PartTypeBase<P, S> {

    public PartTypeTunnel(String name) {
        super(name, new PartRenderPosition(0.25F, 0.25F, 0.375F, 0.375F));
    }

    @Override
    public ModBase getMod() {
        return IntegratedTunnels._instance;
    }

    @Override
    public ModBase getModGui() {
        return IntegratedDynamics._instance;
    }

    @Override
    public Class<? super P> getPartTypeClass() {
        return IPartType.class;
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerPartSettings.class;
    }

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiPartSettings.class;
    }
}
