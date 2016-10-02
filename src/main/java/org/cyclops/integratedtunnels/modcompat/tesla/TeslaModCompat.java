package org.cyclops.integratedtunnels.modcompat.tesla;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cyclops.cyclopscore.modcompat.IModCompat;
import org.cyclops.cyclopscore.modcompat.capabilities.MultipleCapabilityProvider;
import org.cyclops.integrateddynamics.api.part.AttachCapabilitiesEventPart;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartStateEnergy;

/**
 * Mod compat for the Tesla API.
 * @author rubensworks
 *
 */
public class TeslaModCompat implements IModCompat {

	@Override
	public void onInit(final Step initStep) {
		if(initStep == Step.PREINIT) {
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	@SubscribeEvent
	public void onPartStateEnergyLoad(AttachCapabilitiesEventPart event) {
		if (event.getPartState() instanceof PartStateEnergy) {
			PartStateEnergy partState = (PartStateEnergy) event.getPartState();
			event.addCapability(new ResourceLocation(Reference.MOD_ID + ":teslaPartStateEnergy"), new MultipleCapabilityProvider(
					new Capability[]{
							Capabilities.TESLA_HOLDER,
							Capabilities.TESLA_PRODUCER,
							Capabilities.TESLA_CONSUMER
					},
					new Object[]{
							new PartStateEnergyHolder(partState),
							new PartStateEnergyProducer(partState),
							new PartStateEnergyConsumer(partState)
					}
			));
		}
	}

	@Override
	public String getModID() {
		return Reference.MOD_TESLA;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getComment() {
		return "Tesla readers aspects and operators.";
	}

}
