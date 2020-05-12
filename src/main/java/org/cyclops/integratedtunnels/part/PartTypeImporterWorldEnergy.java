package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import energy from the world.
 * @author rubensworks
 */
public class PartTypeImporterWorldEnergy extends PartTypeTunnelAspectsWorld<PartTypeImporterWorldEnergy, PartStateWorld<PartTypeImporterWorldEnergy>> {
    public PartTypeImporterWorldEnergy(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.ENTITY_ENERGY_BOOLEAN_IMPORT,
                TunnelAspects.Write.World.ENTITY_ENERGY_INTEGER_IMPORT
        ));
    }

    @Override
    protected PartStateWorld<PartTypeImporterWorldEnergy> constructDefaultState() {
        return new PartStateWorld<PartTypeImporterWorldEnergy>(Aspects.REGISTRY.getWriteAspects(this).size());
    }
    
    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeImporterWorldEnergy> state) {
        return state.hasVariable() ? GeneralConfig.importerWorldEnergyBaseConsumptionEnabled : GeneralConfig.importerWorldEnergyBaseConsumptionDisabled;
    }
}
