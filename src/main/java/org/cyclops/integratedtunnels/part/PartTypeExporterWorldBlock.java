package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can export block to the world.
 * @author rubensworks
 */
public class PartTypeExporterWorldBlock extends PartTypeTunnelAspectsWorld<PartTypeExporterWorldBlock, PartStateWorld<PartTypeExporterWorldBlock>> {
    public PartTypeExporterWorldBlock(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT,
                TunnelAspects.Write.World.BLOCK_ITEMSTACK_EXPORT,
                TunnelAspects.Write.World.BLOCK_LISTITEMSTACK_EXPORT,
                TunnelAspects.Write.World.BLOCK_PREDICATEITEMSTACK_EXPORT,
                TunnelAspects.Write.World.BLOCK_NBTITEMSTACK_EXPORT,
                TunnelAspects.Write.World.BLOCK_BLOCK_EXPORT,
                TunnelAspects.Write.World.BLOCK_LISTBLOCK_EXPORT,
                TunnelAspects.Write.World.BLOCK_PREDICATEBLOCK_EXPORT
        ));
    }

    @Override
    protected PartStateWorld<PartTypeExporterWorldBlock> constructDefaultState() {
        return new PartStateWorld<PartTypeExporterWorldBlock>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeExporterWorldBlock> state) {
        return state.hasVariable() ? GeneralConfig.exporterWorldBlockBaseConsumptionEnabled : GeneralConfig.exporterWorldBlockBaseConsumptionDisabled;
    }
}
