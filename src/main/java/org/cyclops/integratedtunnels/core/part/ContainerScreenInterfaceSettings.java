package org.cyclops.integratedtunnels.core.part;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonImage;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetNumberField;
import org.cyclops.cyclopscore.client.gui.image.IImage;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.integrateddynamics.core.client.gui.container.ContainerScreenPartSettings;
import org.cyclops.integrateddynamics.core.inventory.container.ContainerMultipartAspects;
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
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/part_interface_settings.png");
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
        clearWidgets();
        super.init();

        numberFieldChannelInterface = new WidgetNumberField(font, leftPos + 106, topPos + 109, 70, 14, true, Component.translatable("gui.integratedtunnels.partsettings.channel.interface"), true);
        numberFieldChannelInterface.setPositiveOnly(false);
        numberFieldChannelInterface.setMaxLength(15);
        numberFieldChannelInterface.setVisible(true);
        numberFieldChannelInterface.setTextColor(16777215);
        numberFieldChannelInterface.setCanLoseFocus(true);

        addRenderableWidget(new ButtonImage(this.leftPos - 20, this.topPos + 0, 18, 18,
                Component.translatable("gui.integrateddynamics.part_offsets"),
                createServerPressable(ContainerMultipartAspects.BUTTON_OFFSETS, (button) -> {
                }),
                new IImage[]{
                        org.cyclops.integrateddynamics.client.gui.image.Images.BUTTON_BACKGROUND_INACTIVE,
                        org.cyclops.integrateddynamics.client.gui.image.Images.BUTTON_MIDDLE_OFFSET
                },
                false, 0, 0));

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
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        font.drawInBatch(L10NHelpers.localize("gui.integratedtunnels.partsettings.channel.interface"),
                leftPos + 8, topPos + 112, Helpers.RGBToInt(0, 0, 0), false,
                guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        numberFieldChannelInterface.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        if (isHovering(-20, 0, 18, 18, mouseX, mouseY)) {
            drawTooltip(Lists.newArrayList(Component.translatable("gui.integrateddynamics.part_offsets")), guiGraphics.pose(), mouseX - leftPos, mouseY - topPos);
        }
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
