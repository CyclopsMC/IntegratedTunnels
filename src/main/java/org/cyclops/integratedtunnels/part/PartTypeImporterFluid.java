package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import fluids.
 * @author rubensworks
 */
public class PartTypeImporterFluid extends PartTypeTunnelAspects<PartTypeImporterFluid, PartStateItem<PartTypeImporterFluid>> {
    public PartTypeImporterFluid(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Fluid.BOOLEAN_IMPORT,
                TunnelAspects.Write.Fluid.INTEGER_IMPORT,
                TunnelAspects.Write.Fluid.FLUIDSTACK_IMPORT,
                TunnelAspects.Write.Fluid.LIST_IMPORT,
                TunnelAspects.Write.Fluid.PREDICATE_IMPORT
        ));
    }

    @Override
    protected PartStateItem<PartTypeImporterFluid> constructDefaultState() {
        return new PartStateItem<PartTypeImporterFluid>(Aspects.REGISTRY.getWriteAspects(this).size(), true, false);
    }
}
