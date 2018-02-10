package org.cyclops.integratedtunnels.core.part;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerPartSettings;

/**
 * @author rubensworks
 */
public class ContainerInterfaceSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceValueId;

    public ContainerInterfaceSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer, IPartType partType) {
        super(player, target, partContainer, partType);
        lastChannelInterfaceValueId = getNextValueId();
    }

    @Override
    protected int getPlayerInventoryOffsetY() {
        return 134;
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        ValueNotifierHelpers.setValue(this, lastChannelInterfaceValueId, ((PartTypeInterfacePositionedAddon.State) getPartState()).getChannelInterface());
    }

    public int getLastChannelInterfaceValueId() {
        return lastChannelInterfaceValueId;
    }

    public int getLastChannelInterfaceValue() {
        return ValueNotifierHelpers.getValueInt(this, lastChannelInterfaceValueId);
    }

    @Override
    protected void updatePartSettings() {
        super.updatePartSettings();
        ((PartTypeInterfacePositionedAddon.State) getPartState()).setChannelInterface(getLastChannelInterfaceValue());
    }
}
