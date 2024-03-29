package org.cyclops.integratedtunnels.api.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.init.IRegistry;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A registry for block placing actions.
 * @author rubensworks
 */
public interface IBlockPlaceHandlerRegistry extends IRegistry {

    /**
     * Add an item placement breaking handler.
     * Multiple handlers can exist for an item.
     * @param item An item.
     * @param placeAction A handler.
     * @return The registered handler.
     */
    public IBlockPlaceHandler register(Item item, IBlockPlaceHandler placeAction);

    /**
     * @return All registered block breaking handlers.
     */
    public Collection<IBlockPlaceHandler> getHandlers();

    /**
     * @param item An item.
     * @return All registered block placement handlers for the given item.
     */
    public Collection<IBlockPlaceHandler> getHandlers(Item item);

    /**
     * Get the first possible block breaking handler for the given item.
     * @param itemStack The item.
     * @param world The world.
     * @param pos The position.
     * @param side The side that this item is being placed at.
     * @param hitX The X position that is being targeted.
     * @param hitY The Y position that is being targeted.
     * @param hitZ The Z position that is being targeted.
     * @param player The placing player.
     * @return A block placement handler or null.
     */
    @Nullable
    public IBlockPlaceHandler getHandler(ItemStack itemStack, Level world, BlockPos pos, Direction side,
                                         float hitX, float hitY, float hitZ, Player player);

}
