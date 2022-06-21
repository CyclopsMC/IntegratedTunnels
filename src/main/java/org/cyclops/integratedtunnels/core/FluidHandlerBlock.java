package org.cyclops.integratedtunnels.core;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import org.cyclops.cyclopscore.helper.FluidHelpers;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * A fluid handler that wraps around a fluid block for draining and filling it,
 * @author rubensworks
 */
public class FluidHandlerBlock implements IFluidHandler {

    private final BlockState state;
    private final Level world;
    private final BlockPos blockPos;

    public FluidHandlerBlock(BlockState state, Level world, BlockPos blockPos) {
        this.state = state;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        Block block = this.state.getBlock();
        if (block instanceof LiquidBlock && this.state.getValue(LiquidBlock.LEVEL) == 0) {
            return new FluidStack(((LiquidBlock) block).getFluid(), FluidHelpers.BUCKET_VOLUME);
        } else {
            return FluidStack.EMPTY;
        }
    }

    @Override
    public int getTankCapacity(int tank) {
        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        Fluid fluid = resource.getFluid();
        BlockState block = fluid.getFluidType().getBlockForFluidState(world, blockPos, fluid.defaultFluidState());
        return new BlockWrapper(block, world, blockPos).fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        Block block = this.state.getBlock();
        if (block instanceof LiquidBlock
                && ((LiquidBlock) block).getFluid() == resource.getFluid()) {
            return this.drain(resource.getAmount(), action);
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        Block block = this.state.getBlock();
        if (block instanceof LiquidBlock
                && this.state.getValue(LiquidBlock.LEVEL) == 0
                && maxDrain >= FluidHelpers.BUCKET_VOLUME) {
            if (action.execute()) {
                this.world.setBlock(this.blockPos, Blocks.AIR.defaultBlockState(), 11);
            }
            return new FluidStack(((LiquidBlock) block).getFluid(), FluidHelpers.BUCKET_VOLUME);
        }
        return FluidStack.EMPTY;
    }

}
