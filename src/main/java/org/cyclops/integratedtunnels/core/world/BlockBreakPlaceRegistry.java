package org.cyclops.integratedtunnels.core.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.cyclops.integratedtunnels.api.world.IBlockPlaceHandler;
import org.cyclops.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of {@link IBlockPlaceHandlerRegistry}.
 * @author rubensworks
 */
public class BlockBreakPlaceRegistry implements IBlockPlaceHandlerRegistry {

    private static BlockBreakPlaceRegistry INSTANCE = new BlockBreakPlaceRegistry();

    private final Multimap<Item, IBlockPlaceHandler> handlers = Multimaps.newSetMultimap(Maps.<Item, Collection<IBlockPlaceHandler>>newIdentityHashMap(), Sets::newIdentityHashSet);

    private BlockBreakPlaceRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static BlockBreakPlaceRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public IBlockPlaceHandler register(Item item, IBlockPlaceHandler placeAction) {
        handlers.put(item, placeAction);
        return placeAction;
    }

    @Override
    public Collection<IBlockPlaceHandler> getHandlers() {
        return Collections.unmodifiableCollection(handlers.values());
    }

    @Override
    public Collection<IBlockPlaceHandler> getHandlers(Item item) {
        return Collections.unmodifiableCollection(handlers.get(item));
    }

    @Nullable
    @Override
    public IBlockPlaceHandler getHandler(ItemStack itemStack, World world, BlockPos pos, EnumFacing side,
                                         float hitX, float hitY, float hitZ, EntityPlayer player) {
        for (IBlockPlaceHandler placeHandler : getHandlers(itemStack.getItem())) {
            if (placeHandler.shouldApply(itemStack, world, pos, side, hitX, hitY, hitZ, player)) {
                return placeHandler;
            }
        }
        return null;
    }
}
