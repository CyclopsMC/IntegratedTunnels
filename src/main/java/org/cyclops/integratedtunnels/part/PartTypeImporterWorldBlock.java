package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeTunnelAspectsWorld;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * A part that can import blocks from world.
 * @author rubensworks
 */
public class PartTypeImporterWorldBlock extends PartTypeTunnelAspectsWorld<PartTypeImporterWorldBlock, PartStateWorld<PartTypeImporterWorldBlock>> {
    public PartTypeImporterWorldBlock(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT,
                TunnelAspects.Write.World.BLOCK_ITEMSTACK_IMPORT,
                TunnelAspects.Write.World.BLOCK_LISTITEMSTACK_IMPORT,
                TunnelAspects.Write.World.BLOCK_PREDICATEITEMSTACK_IMPORT,
                TunnelAspects.Write.World.BLOCK_NBTITEMSTACK_IMPORT,
                TunnelAspects.Write.World.BLOCK_BLOCK_IMPORT,
                TunnelAspects.Write.World.BLOCK_LISTBLOCK_IMPORT,
                TunnelAspects.Write.World.BLOCK_PREDICATEBLOCK_IMPORT
        ));
    }

    @Override
    protected PartStateWorld<PartTypeImporterWorldBlock> constructDefaultState() {
        return new PartStateWorld<PartTypeImporterWorldBlock>(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(PartStateWorld<PartTypeImporterWorldBlock> state) {
        return state.hasVariable() ? GeneralConfig.importerWorldBlockBaseConsumptionEnabled : GeneralConfig.importerWorldBlockBaseConsumptionDisabled;
    }
}
