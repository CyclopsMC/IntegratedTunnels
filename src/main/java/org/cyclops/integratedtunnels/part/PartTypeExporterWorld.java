package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export to the world.
 * @author rubensworks
 */
public class PartTypeExporterWorld extends PartTypeTunnelAspects<PartTypeExporterWorld, PartStateWriterBase<PartTypeExporterWorld>> {
    public PartTypeExporterWorld(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.FLUID_BOOLEAN_EXPORT,
                TunnelAspects.Write.World.FLUID_FLUIDSTACK_EXPORT,
                TunnelAspects.Write.World.FLUID_LIST_EXPORT,
                TunnelAspects.Write.World.FLUID_PREDICATE_EXPORT
        ));
    }

    @Override
    protected PartStateWriterBase<PartTypeExporterWorld> constructDefaultState() {
        return new PartStateWriterBase<PartTypeExporterWorld>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeExporterWorld> state) {
        return state.hasVariable() ? 32 : super.getConsumptionRate(state);
    }
}
