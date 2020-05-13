package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export energy.
 * @author rubensworks
 */
public class PartTypeExporterEnergy extends PartTypeTunnelAspects<PartTypeExporterEnergy, PartStateEnergy<PartTypeExporterEnergy>> {
    public PartTypeExporterEnergy(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Energy.BOOLEAN_EXPORT,
                TunnelAspects.Write.Energy.INTEGER_EXPORT
        ));
    }

    @Override
    protected PartStateEnergy<PartTypeExporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeExporterEnergy>(Aspects.REGISTRY.getWriteAspects(this).size(), false, true);
    }
    
    @Override
    public int getConsumptionRate(PartStateEnergy<PartTypeExporterEnergy> state) {
        return GeneralConfig.exporterEnergyBaseConsumption;
    }
}
