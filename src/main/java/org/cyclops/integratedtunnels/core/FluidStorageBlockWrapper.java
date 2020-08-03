package org.cyclops.integratedtunnels.core;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandlerRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * An item storage for world block placement.
 * @author rubensworks
 */
public class FluidStorageBlockWrapper implements IIngredientComponentStorage<FluidStack, Integer> {

    private final ServerWorld world;
    private final BlockPos pos;
    private final Direction side;
    private final boolean blockUpdate;

    @Nullable
    private final IIngredientComponentStorage<FluidStack, Integer> targetStorage;

    public FluidStorageBlockWrapper(ServerWorld world, BlockPos pos, Direction side, boolean blockUpdate) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.blockUpdate = blockUpdate;

        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(world, pos, side).orElse(null); // TODO: does this work? copy FluidHandlerBlock from Flopper?
        this.targetStorage = fluidHandler != null ? getComponent()
                .getStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .wrapComponentStorage(fluidHandler) : null;
    }

    protected void sendBlockUpdate() {
        world.neighborChanged(pos, Blocks.AIR, pos);
    }

    protected IBlockBreakHandler getBlockBreakHandler(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        return IntegratedTunnels._instance.getRegistryManager().getRegistry(IBlockBreakHandlerRegistry.class)
                .getHandler(blockState, world, pos, player);
    }

    protected void postInsert(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            SoundEvent soundevent = moved.getFluid().getAttributes().getEmptySound(moved);
            world.playSound(null, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    protected void postExtract(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            SoundEvent soundevent = moved.getFluid().getAttributes().getFillSound(moved);
            world.playSound(null, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public IngredientComponent<FluidStack, Integer> getComponent() {
        return IngredientComponent.FLUIDSTACK;
    }

    @Override
    public Iterator<FluidStack> iterator() {
        return this.targetStorage.iterator();
    }

    @Override
    public Iterator<FluidStack> iterator(@Nonnull FluidStack prototype, Integer matchCondition) {
        return this.targetStorage.iterator(prototype, matchCondition);
    }

    @Override
    public long getMaxQuantity() {
        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack insert(@Nonnull FluidStack stack, boolean simulate) {
        if (targetStorage != null) {
            return stack;
        }

        Fluid fluid = stack.getFluid();
        if (world.getDimension().doesWaterVaporize() && fluid.getAttributes().doesVaporize(world, pos, stack)) {
            return null;
        }

        BlockState block = fluid.getAttributes().getBlock(world, pos, fluid.getDefaultState());
        IFluidHandler handler = new BlockWrapper(block, world, pos);

        int filled = handler.fill(stack, FluidHelpers.simulateBooleanToAction(simulate));
        int remaining = FluidHelpers.getAmount(stack) - filled;
        if (!simulate && filled > 0) {
            postInsert(stack);
        }

        if (remaining == 0) {
            return FluidStack.EMPTY;
        } else {
            return new FluidStack(stack, remaining);
        }
    }

    @Override
    public FluidStack extract(@Nonnull FluidStack prototype, Integer matchCondition, boolean simulate) {
        FluidStack extracted = targetStorage.extract(prototype, matchCondition, simulate);
        if (!simulate) {
            postExtract(extracted);
        }
        return extracted;
    }

    @Override
    public FluidStack extract(long maxQuantity, boolean simulate) {
        FluidStack extracted = targetStorage.extract(maxQuantity, simulate);
        if (!simulate) {
            postExtract(extracted);
        }
        return extracted;
    }

}
