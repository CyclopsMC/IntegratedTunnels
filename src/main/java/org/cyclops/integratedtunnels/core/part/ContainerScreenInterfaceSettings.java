package org.cyclops.integratedtunnels.core.part;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetNumberField;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.core.client.gui.container.ContainerScreenPartSettings;
import org.cyclops.integratedtunnels.Reference;
import org.lwjgl.glfw.GLFW;

/**
 * @author rubensworks
 */
public class ContainerScreenInterfaceSettings extends ContainerScreenPartSettings<ContainerInterfaceSettings> {

    private WidgetNumberField numberFieldChannelInterface = null;

    public ContainerScreenInterfaceSettings(ContainerInterfaceSettings container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
    }

    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/part_interface_settings.png");
    }

    @Override
    protected void onSave() {
        super.onSave();
        try {
            int channelInterface = numberFieldChannelInterface.getInt();
            ValueNotifierHelpers.setValue(getContainer(), ((ContainerInterfaceSettings) getContainer()).getLastChannelInterfaceValueId(), channelInterface);
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public void init() {
        super.init();

        numberFieldChannelInterface = new WidgetNumberField(font, guiLeft + 106, guiTop + 109, 70, 14, true, L10NHelpers.localize("gui.integratedtunnels.partsettings.channel.interface"), true);
        numberFieldChannelInterface.setPositiveOnly(false);
        numberFieldChannelInterface.setMaxStringLength(15);
        numberFieldChannelInterface.setVisible(true);
        numberFieldChannelInterface.setTextColor(16777215);
        numberFieldChannelInterface.setCanLoseFocus(true);

        this.refreshValues();
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!(isFieldUpdateIntervalEnabled() && this.numberFieldChannelInterface.charTyped(typedChar, keyCode))) {
            return super.charTyped(typedChar, keyCode);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (typedChar != GLFW.GLFW_KEY_ESCAPE) {
            if (this.numberFieldChannelInterface.keyPressed(typedChar, keyCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(typedChar, keyCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.numberFieldChannelInterface.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        font.drawString(L10NHelpers.localize("gui.integratedtunnels.partsettings.channel.interface"),
                guiLeft + 8, guiTop + 112, Helpers.RGBToInt(0, 0, 0));
        numberFieldChannelInterface.render(mouseX, mouseY, partialTicks);
    }

    @Override
    protected int getBaseYSize() {
        return 216;
    }

    @Override
    public void onUpdate(int valueId, CompoundNBT value) {
        super.onUpdate(valueId, value);
        if (valueId == ((ContainerInterfaceSettings) getContainer()).getLastChannelInterfaceValueId()) {
            numberFieldChannelInterface.setText(Integer.toString(((ContainerInterfaceSettings) getContainer()).getLastChannelInterfaceValue()));
        }
    }
}
