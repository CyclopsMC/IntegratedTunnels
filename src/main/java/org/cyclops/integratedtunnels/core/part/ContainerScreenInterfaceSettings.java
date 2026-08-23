package org.cyclops.integratedtunnels.core.part;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonImage;
import org.cyclops.cyclopscore.client.gui.component.input.WidgetNumberField;
import org.cyclops.cyclopscore.client.gui.image.IImage;
import org.cyclops.cyclopscore.helper.IModHelpers;
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
    protected Identifier constructGuiTexture() {
        return Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/part_interface_settings.png");
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
        numberFieldChannelInterface.setTextColor(ARGB.opaque(16777215));
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
    public boolean charTyped(CharacterEvent evt) {
        if (!this.numberFieldChannelInterface.charTyped(evt)) {
            return super.charTyped(evt);
        }
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent evt) {
        if (evt.key() != GLFW.GLFW_KEY_ESCAPE) {
            if (this.numberFieldChannelInterface.keyPressed(evt)) {
                return true;
            }
        }
        return super.keyPressed(evt);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent evt, boolean isDoubleClick) {
        if (this.numberFieldChannelInterface.mouseClicked(evt, isDoubleClick)) {
            return true;
        }
        return super.mouseClicked(evt, isDoubleClick);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.text(font, IModHelpers.get().getL10NHelpers().localize("gui.integratedtunnels.partsettings.channel.interface"),
                leftPos + 8, topPos + 112, IModHelpers.get().getBaseHelpers().RGBAToInt(0, 0, 0, 255), false);
        numberFieldChannelInterface.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractLabels(guiGraphics, mouseX, mouseY);

        if (isHovering(-20, 0, 18, 18, mouseX, mouseY)) {
            drawTooltip(Lists.newArrayList(Component.translatable("gui.integrateddynamics.part_offsets")), guiGraphics, mouseX, mouseY);
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
