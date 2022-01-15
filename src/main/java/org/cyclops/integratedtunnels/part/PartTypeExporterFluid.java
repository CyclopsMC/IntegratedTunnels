package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export fluids.
 * @author rubensworks
 */
public class PartTypeExporterFluid extends PartTypeTunnelAspects<PartTypeExporterFluid, PartStateFluid<PartTypeExporterFluid>> {
    public PartTypeExporterFluid(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Fluid.BOOLEAN_EXPORT,
                TunnelAspects.Write.Fluid.INTEGER_EXPORT,
                TunnelAspects.Write.Fluid.FLUIDSTACK_EXPORT,
                TunnelAspects.Write.Fluid.LIST_EXPORT,
                TunnelAspects.Write.Fluid.PREDICATE_EXPORT,
                TunnelAspects.Write.Fluid.NBT_EXPORT
        ));
    }

    @Override
    protected PartStateFluid<PartTypeExporterFluid> constructDefaultState() {
        return new PartStateFluid<PartTypeExporterFluid>(Aspects.REGISTRY.getWriteAspects(this).size(), false, true);
    }

    @Override
    public int getConsumptionRate(PartStateFluid<PartTypeExporterFluid> state) {
        return GeneralConfig.exporterFluidBaseConsumption;
    }
}
