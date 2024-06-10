package org.cyclops.integratedtunnels.core.part;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.network.FriendlyByteBuf;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerPartSettings;
import org.cyclops.integratedtunnels.RegistryEntries;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class ContainerInterfaceSettings extends ContainerPartSettings {

    private final int lastChannelInterfaceValueId;

    public ContainerInterfaceSettings(int id, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        this(id, playerInventory, new SimpleContainer(0), PartHelpers.readPartTarget(packetBuffer), Optional.empty(), PartHelpers.readPart(packetBuffer));
    }

    public ContainerInterfaceSettings(int id, Inventory playerInventory, Container inventory,
                                      PartTarget target, Optional<IPartContainer> partContainer, IPartType partType) {
        super(RegistryEntries.CONTAINER_INTERFACE_SETTINGS.get(), id, playerInventory, inventory, target, partContainer, partType);
        lastChannelInterfaceValueId = getNextValueId();

        putButtonAction(ContainerMultipartAspects.BUTTON_OFFSETS, (s, containerExtended) -> {
            if (!player.getCommandSenderWorld().isClientSide()) {
                PartHelpers.openContainerPartOffsets((ServerPlayer) player, target.getCenter(), partType);
            }
        });
    }

    @Override
    protected int getPlayerInventoryOffsetY() {
        return 134;
    }

    @Override
    protected void initializeValues() {
        super.initializeValues();
        ValueNotifierHelpers.setValue(this, lastChannelInterfaceValueId, ((IPartTypeInterfacePositionedAddon.IState) getPartState()).getChannelInterface());
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
        ((IPartTypeInterfacePositionedAddon.IState) getPartState()).setChannelInterface(getLastChannelInterfaceValue());
    }
}
