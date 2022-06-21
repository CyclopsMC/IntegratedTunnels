package org.cyclops.integratedtunnels.core.part;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
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

    public ContainerScreenInterfaceSettings(ContainerInterfaceSettings container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/part_interface_settings.png");
    }

    @Override
    protected void onSave() {
        super.onSave();
        try {
            int channelInterface = numberFieldChannelInterface.getInt();
            ValueNotifierHelpers.setValue(getMenu(), ((ContainerInterfaceSettings) getMenu()).getLastChannelInterfaceValueId(), channelInterface);
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public void init() {
        super.init();

        numberFieldChannelInterface = new WidgetNumberField(font, leftPos + 106, topPos + 109, 70, 14, true, Component.translatable("gui.integratedtunnels.partsettings.channel.interface"), true);
        numberFieldChannelInterface.setPositiveOnly(false);
        numberFieldChannelInterface.setMaxLength(15);
        numberFieldChannelInterface.setVisible(true);
        numberFieldChannelInterface.setTextColor(16777215);
        numberFieldChannelInterface.setCanLoseFocus(true);

        this.refreshValues();
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (!this.numberFieldChannelInterface.charTyped(typedChar, keyCode)) {
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
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        font.draw(matrixStack, L10NHelpers.localize("gui.integratedtunnels.partsettings.channel.interface"),
                leftPos + 8, topPos + 112, Helpers.RGBToInt(0, 0, 0));
        numberFieldChannelInterface.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected int getBaseYSize() {
        return 216;
    }

    @Override
    public void onUpdate(int valueId, CompoundTag value) {
        super.onUpdate(valueId, value);
        if (valueId == getMenu().getLastChannelInterfaceValueId()) {
            numberFieldChannelInterface.setValue(Integer.toString(getMenu().getLastChannelInterfaceValue()));
        }
    }
}
