package org.cyclops.integratedtunnels.core.part;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketBuffer;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerPartSettings;
import org.cyclops.integratedtunnels.RegistryEntries;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class ContainerInterfaceSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceValueId;

    public ContainerInterfaceSettings(int id, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        this(id, playerInventory, new Inventory(0), PartHelpers.readPartTarget(packetBuffer), Optional.empty(), PartHelpers.readPart(packetBuffer));
    }

    public ContainerInterfaceSettings(int id, PlayerInventory playerInventory, IInventory inventory,
                                      PartTarget target, Optional<IPartContainer> partContainer, IPartType partType) {
        super(RegistryEntries.CONTAINER_INTERFACE_SETTINGS, id, playerInventory, inventory, target, partContainer, partType);
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
