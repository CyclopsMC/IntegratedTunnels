package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import energy.
 * @author rubensworks
 */
public class PartTypeImporterEnergy extends PartTypeTunnelAspects<PartTypeImporterEnergy, PartStateEnergy<PartTypeImporterEnergy>> {
    public PartTypeImporterEnergy(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Energy.BOOLEAN_IMPORT,
                TunnelAspects.Write.Energy.INTEGER_IMPORT
        ));
    }

    @Override
    protected PartStateEnergy<PartTypeImporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeImporterEnergy>(Aspects.REGISTRY.getWriteAspects(this).size(), true, false);
    }

    @Override
    public int getConsumptionRate(PartStateEnergy<PartTypeImporterEnergy> state) {
        return GeneralConfig.importerEnergyBaseConsumption;
    }
}
