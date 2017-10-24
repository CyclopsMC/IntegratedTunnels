package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspects;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import items.
 * @author rubensworks
 */
public class PartTypeImporterItem extends PartTypeTunnelAspects<PartTypeImporterItem, PartStateItem<PartTypeImporterItem>> {
    public PartTypeImporterItem(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.Item.BOOLEAN_IMPORT,
                TunnelAspects.Write.Item.INTEGER_IMPORT,
                TunnelAspects.Write.Item.INTEGER_SLOT_IMPORT,
                TunnelAspects.Write.Item.ITEMSTACK_IMPORT,
                TunnelAspects.Write.Item.LIST_IMPORT,
                TunnelAspects.Write.Item.PREDICATE_IMPORT
        ));
    }

    @Override
    protected PartStateItem<PartTypeImporterItem> constructDefaultState() {
        return new PartStateItem<PartTypeImporterItem>(Aspects.REGISTRY.getWriteAspects(this).size(), true, false);
    }
}
