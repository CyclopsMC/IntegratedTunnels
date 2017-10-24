package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export items.
 * @author rubensworks
 */
public class PartTypeExporterItem extends PartTypeTunnelAspects<PartTypeExporterItem, PartStateItem<PartTypeExporterItem>> {
    public PartTypeExporterItem(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Item.BOOLEAN_EXPORT,
                TunnelAspects.Write.Item.INTEGER_EXPORT,
                TunnelAspects.Write.Item.INTEGER_SLOT_EXPORT,
                TunnelAspects.Write.Item.ITEMSTACK_EXPORT,
                TunnelAspects.Write.Item.LIST_EXPORT,
                TunnelAspects.Write.Item.PREDICATE_EXPORT
        ));
    }

    @Override
    protected PartStateItem<PartTypeExporterItem> constructDefaultState() {
        return new PartStateItem<PartTypeExporterItem>(Aspects.REGISTRY.getWriteAspects(this).size(), false, true);
    }
}
