package org.cyclops.integratedtunnels.core.part;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.cyclops.cyclopscore.client.gui.component.input.GuiNumberField;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.ValueNotifierHelpers;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.client.gui.container.GuiPartSettings;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.Reference;

import java.io.IOException;

/**
 * @author rubensworks
 */
public class GuiInterfaceSettings extends GuiPartSettings {

    private GuiNumberField numberFieldChannelInterface = null;

    public GuiInterfaceSettings(EntityPlayer player, PartTarget target, IPartContainer partContainer, IPartType partType) {
        super(new ContainerInterfaceSettings(player, target, partContainer, partType), player, target, partContainer, partType);
    }

    protected ResourceLocation constructResourceLocation() {
        return new ResourceLocation(Reference.MOD_ID, getGuiTexture());
    }

    @Override
    public String getGuiTexture() {
        return IntegratedTunnels._instance.getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
                + "part_interface_settings.png";
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
    public void initGui() {
        super.initGui();

        numberFieldChannelInterface = new GuiNumberField(0, Minecraft.getMinecraft().fontRenderer, guiLeft + 106, guiTop + 109, 70, 14, true, true);
        numberFieldChannelInterface.setPositiveOnly(false);
        numberFieldChannelInterface.setMaxStringLength(15);
        numberFieldChannelInterface.setVisible(true);
        numberFieldChannelInterface.setTextColor(16777215);
        numberFieldChannelInterface.setCanLoseFocus(true);

        this.refreshValues();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.checkHotbarKeys(keyCode)) {
            if (!this.numberFieldChannelInterface.textboxKeyTyped(typedChar, keyCode)) {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.numberFieldChannelInterface.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        numberFieldChannelInterface.drawTextBox(Minecraft.getMinecraft(), mouseX, mouseY);
        fontRenderer.drawString(L10NHelpers.localize("gui.integratedtunnels.partsettings.channel.interface"),
                guiLeft + 8, guiTop + 112, Helpers.RGBToInt(0, 0, 0));
    }

    @Override
    protected int getBaseYSize() {
        return 216;
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {
        super.onUpdate(valueId, value);
        if (valueId == ((ContainerInterfaceSettings) getContainer()).getLastChannelInterfaceValueId()) {
            numberFieldChannelInterface.setText(Integer.toString(((ContainerInterfaceSettings) getContainer()).getLastChannelInterfaceValue()));
        }
    }
}
