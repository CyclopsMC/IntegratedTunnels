package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import items from the world.
 * @author rubensworks
 */
public class PartTypeImporterWorldItem extends PartTypeTunnelAspectsWorld<PartTypeImporterWorldItem, PartStateWriterBase<PartTypeImporterWorldItem>> {
    public PartTypeImporterWorldItem(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_IMPORT,
                TunnelAspects.Write.World.ENTITYITEM_INTEGER_IMPORT,
                TunnelAspects.Write.World.ENTITYITEM_ITEMSTACK_IMPORT,
                TunnelAspects.Write.World.ENTITYITEM_LISTITEMSTACK_IMPORT,
                TunnelAspects.Write.World.ENTITYITEM_PREDICATEITEMSTACK_IMPORT
        ));
    }

    @Override
    protected PartStateWriterBase<PartTypeImporterWorldItem> constructDefaultState() {
        return new PartStateWriterBase<PartTypeImporterWorldItem>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWriterBase<PartTypeImporterWorldItem> state) {
        return state.hasVariable() ? 32 : super.getConsumptionRate(state);
    }
}
