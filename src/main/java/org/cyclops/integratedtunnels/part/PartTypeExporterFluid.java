package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export fluids.
 * @author rubensworks
 */
public class PartTypeExporterFluid extends PartTypeTunnelAspects<PartTypeExporterFluid, PartStateItem<PartTypeExporterFluid>> {
    public PartTypeExporterFluid(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Fluid.BOOLEAN_EXPORT,
                TunnelAspects.Write.Fluid.INTEGER_EXPORT,
                TunnelAspects.Write.Fluid.FLUIDSTACK_EXPORT,
                TunnelAspects.Write.Fluid.LIST_EXPORT,
                TunnelAspects.Write.Fluid.PREDICATE_EXPORT
        ));
    }

    @Override
    protected PartStateItem<PartTypeExporterFluid> constructDefaultState() {
        return new PartStateItem<PartTypeExporterFluid>(Aspects.REGISTRY.getWriteAspects(this).size(), false, true);
    }
}
