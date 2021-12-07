package org.cyclops.integratedtunnels.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandlerRegistry;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An item storage for world block placement.
 * @author rubensworks
 */
public class FluidStorageBlockWrapper implements IIngredientComponentStorage<FluidStack, Integer> {

    private final WorldServer world;
    private final BlockPos pos;
    private final EnumFacing side;
    private final boolean blockUpdate;

    private final IIngredientComponentStorage<FluidStack, Integer> targetStorage;

    public FluidStorageBlockWrapper(WorldServer world, BlockPos pos, EnumFacing side, boolean blockUpdate) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.blockUpdate = blockUpdate;

        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(world, pos, side);
        this.targetStorage = fluidHandler != null ? getComponent()
                .getStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .wrapComponentStorage(fluidHandler) : null;
    }

    protected void sendBlockUpdate() {
        world.neighborChanged(pos, Blocks.AIR, pos);
    }

    protected IBlockBreakHandler getBlockBreakHandler(IBlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        return IntegratedTunnels._instance.getRegistryManager().getRegistry(IBlockBreakHandlerRegistry.class)
                .getHandler(blockState, world, pos, player);
    }

    protected void postInsert(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            SoundEvent soundevent = moved.getFluid().getEmptySound(moved);
            world.playSound(null, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    protected void postExtract(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            SoundEvent soundevent = moved.getFluid().getFillSound(moved);
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
        return Fluid.BUCKET_VOLUME;
    }

    @Override
    public FluidStack insert(@Nonnull FluidStack stack, boolean simulate) {
        if (targetStorage != null) {
            FluidStack exists = targetStorage.extract(1000, false);
            if(exists != null && exists.amount == 1000)
                return stack;
        }

        net.minecraftforge.fluids.Fluid fluid = stack.getFluid();
        if (world.provider.doesWaterVaporize() && fluid.doesVaporize(stack)) {
            return null;
        }

        Block block = fluid.getBlock();
        IFluidHandler handler;
        if (block == null) {
            return stack;
        } else if (block instanceof IFluidBlock) {
            handler = new FluidBlockWrapper((IFluidBlock) block, world, pos);
        } else if (block instanceof BlockLiquid) {
            handler = new BlockLiquidWrapper((BlockLiquid) block, world, pos);
        } else {
            handler = new BlockWrapper(block, world, pos);
        }

        int filled = handler.fill(stack, !simulate);
        int remaining = FluidHelpers.getAmount(stack) - filled;
        if (!simulate && filled > 0) {
            postInsert(stack);
        }

        if (remaining == 0) {
            return null;
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
