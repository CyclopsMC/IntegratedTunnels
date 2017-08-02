package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export items to the world.
 * @author rubensworks
 */
public class PartTypeExporterWorldItem extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldItem, PartStateWriterBase<PartTypeExporterWorldItem>> {
    public PartTypeExporterWorldItem(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT,
                TunnelAspects.Write.World.ENTITYITEM_ITEMSTACK_EXPORT,
                TunnelAspects.Write.World.ENTITYITEM_LISTITEMSTACK_EXPORT,
                TunnelAspects.Write.World.ENTITYITEM_PREDICATEITEMSTACK_EXPORT
        ));
    }

    @Override
    protected PartStateWriterBase<PartTypeExporterWorldItem> constructDefaultState() {
        return new PartStateWriterBase<PartTypeExporterWorldItem>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeExporterWorldItem> state) {
        return state.hasVariable() ? 32 : super.getConsumptionRate(state);
    }
}
