package org.cyclops.integratedtunnels.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.cyclopscore.helper.IModHelpersNeoForge;

import javax.annotation.Nonnull;

/**
 * A fluid handler that wraps around a fluid block for draining and filling it,
 * @author rubensworks
 */
public class FluidHandlerBlock implements ResourceHandler<FluidResource> {

    private final BlockState state;
    private final Level world;
    private final BlockPos blockPos;
    private final StackJournal stackJournal;

    private FluidStack uncommittedFilled = FluidStack.EMPTY;;
    private boolean uncomittedEmpty = false;

    public FluidHandlerBlock(BlockState state, Level world, BlockPos blockPos) {
        this.state = state;
        this.world = world;
        this.blockPos = blockPos;
        this.stackJournal = new StackJournal();
    }

    @Override
    public int size() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidResource getResource(int tank) {
        Block block = this.state.getBlock();
        if (block instanceof LiquidBlock && this.state.getValue(LiquidBlock.LEVEL) == 0) {
            return FluidResource.of(((LiquidBlock) block).fluid);
        } else {
            return FluidResource.EMPTY;
        }
    }

    @Override
    public long getAmountAsLong(int tank) {
        Block block = this.state.getBlock();
        if (block instanceof LiquidBlock && this.state.getValue(LiquidBlock.LEVEL) == 0) {
            return IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume();
        } else {
            return 0;
        }
    }

    @Override
    public long getCapacityAsLong(int tank, FluidResource resource) {
        return IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume();
    }

    @Override
    public boolean isValid(int tank, FluidResource resource) {
        return true;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (amount < IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume()) {
            return 0;
        }
        if (!this.uncommittedFilled.isEmpty()) {
            return 0;
        }
        Fluid fluid = resource.getFluid();
        BlockState block = fluid.getFluidType().getBlockForFluidState(world, blockPos, fluid.defaultFluidState());
        if (block.isAir()) {
            return 0;
        }
        this.stackJournal.updateSnapshots(transaction);
        this.uncommittedFilled = resource.toStack(IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume());
        return IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume();
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (this.uncomittedEmpty) {
            return 0;
        }
        Block block = this.state.getBlock();
        if (block instanceof LiquidBlock
                && this.state.getValue(LiquidBlock.LEVEL) == 0
                && amount >= IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume()) {
            this.stackJournal.updateSnapshots(transaction);
            this.uncomittedEmpty = true;
            return IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume();
        } else if (this.state.hasProperty(BlockStateProperties.WATERLOGGED)
                && this.state.getValue(BlockStateProperties.WATERLOGGED)
                && block instanceof SimpleWaterloggedBlock
                && amount >= IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume()) {
            this.stackJournal.updateSnapshots(transaction);
            this.uncomittedEmpty = true;
            return IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume();
        }
        return 0;
    }

    private class StackJournal extends SnapshotJournal<FluidResource> {

        @Override
        protected FluidResource createSnapshot() {
            return FluidResource.of(FluidHandlerBlock.this.getResource(0).getFluid());
        }

        @Override
        protected void revertToSnapshot(FluidResource snapshot) {
            FluidHandlerBlock.this.uncomittedEmpty = false;
            FluidHandlerBlock.this.uncommittedFilled = FluidStack.EMPTY;
        }

        @Override
        protected void onRootCommit(FluidResource originalState) {
            if (!FluidHandlerBlock.this.uncommittedFilled.isEmpty()) {
                // Inspired by NeoForge's old FluidUtil.destroyBlockOnFluidPlacement
                if (!FluidHandlerBlock.this.world.isClientSide()) {
                    BlockState destBlockState = FluidHandlerBlock.this.world.getBlockState(FluidHandlerBlock.this.blockPos);
                    boolean isDestNonSolid = !destBlockState.isSolid();
                    boolean isDestReplaceable = false;
                    if ((isDestNonSolid || isDestReplaceable) && !destBlockState.liquid()) {
                        FluidHandlerBlock.this.world.destroyBlock(FluidHandlerBlock.this.blockPos, true);
                    }
                }

                Fluid fluid = FluidHandlerBlock.this.uncommittedFilled.getFluid();
                BlockState block = fluid.getFluidType().getBlockForFluidState(world, blockPos, fluid.defaultFluidState());
                FluidHandlerBlock.this.world.setBlock(FluidHandlerBlock.this.blockPos, block, 11);
                FluidHandlerBlock.this.uncommittedFilled = FluidStack.EMPTY;
            }

            if (FluidHandlerBlock.this.uncomittedEmpty) {
                Block block = FluidHandlerBlock.this.state.getBlock();
                if (block instanceof LiquidBlock) {
                    FluidHandlerBlock.this.world.setBlock(FluidHandlerBlock.this.blockPos, Blocks.AIR.defaultBlockState(), 11);
                } else if (FluidHandlerBlock.this.state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    ((SimpleWaterloggedBlock) block).pickupBlock(null, world, blockPos, state);
                }
                FluidHandlerBlock.this.uncomittedEmpty = false;
            }
        }
    }

}
