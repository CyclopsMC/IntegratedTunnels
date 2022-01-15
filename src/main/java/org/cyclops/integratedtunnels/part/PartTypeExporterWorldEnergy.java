package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export energy to the world.
 * @author rubensworks
 */
public class PartTypeExporterWorldEnergy extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldEnergy, PartStateWorld<PartTypeExporterWorldEnergy>> {
    public PartTypeExporterWorldEnergy(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.ENTITY_ENERGY_BOOLEAN_EXPORT,
                TunnelAspects.Write.World.ENTITY_ENERGY_INTEGER_EXPORT
        ));
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldEnergy> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldEnergy>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldEnergy> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldEnergyBaseConsumptionEnabled : GeneralConfig.exporterWorldEnergyBaseConsumptionDisabled;
    }
}
