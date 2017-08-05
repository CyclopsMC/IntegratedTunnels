package org.cyclops.integratedtunnels.api.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An interface for custom block placing actions.
 * @author rubensworks
 */
public interface IBlockPlaceHandler {

    /**
     * If this can handle the given item placement as block.
     * @param itemStack The item.
     * @param world The world.
     * @param pos The position.
     * @param side The side that this item is being placed at.
     * @param hitX The X position that is being targeted.
     * @param hitY The Y position that is being targeted.
     * @param hitZ The Z position that is being targeted.
     * @param player The placing player.
     * @return If this can handle the given item placement as block.
     */
    public boolean shouldApply(ItemStack itemStack, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player);

    /**
     * Place the given item as block.
     * @param itemStack The item.
     * @param world The world.
     * @param pos The position.
     * @param side The side that this item is being placed at.
     * @param hitX The X position that is being targeted.
     * @param hitY The Y position that is being targeted.
     * @param hitZ The Z position that is being targeted.
     * @param player The placing player.
     */
    public void placeBlock(ItemStack itemStack, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EntityPlayer player);

}
