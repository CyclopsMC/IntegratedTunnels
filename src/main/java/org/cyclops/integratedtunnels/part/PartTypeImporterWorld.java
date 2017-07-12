package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import to the world.
 * @author rubensworks
 */
public class PartTypeImporterWorld extends PartTypeTunnelAspects<PartTypeImporterWorld, PartStateWriterBase<PartTypeImporterWorld>> {
    public PartTypeImporterWorld(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.FLUID_BOOLEAN_IMPORT,
                TunnelAspects.Write.World.FLUID_FLUIDSTACK_IMPORT,
                TunnelAspects.Write.World.FLUID_LIST_IMPORT,
                TunnelAspects.Write.World.FLUID_PREDICATE_IMPORT,

                TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT,
                TunnelAspects.Write.World.BLOCK_ITEMSTACK_IMPORT,
                TunnelAspects.Write.World.BLOCK_LISTITEMSTACK_IMPORT,
                TunnelAspects.Write.World.BLOCK_PREDICATEITEMSTACK_IMPORT
        ));
    }

    @Override
    protected PartStateWriterBase<PartTypeImporterWorld> constructDefaultState() {
        return new PartStateWriterBase<PartTypeImporterWorld>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeImporterWorld> state) {
        return state.hasVariable() ? 32 : super.getConsumptionRate(state);
    }
}
