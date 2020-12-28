package org.cyclops.integratedtunnels.core;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;

import javax.annotation.Nonnull;
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

    private final IIngredientComponentStorage<FluidStack, Integer> targetStorage;

    public FluidStorageBlockWrapper(ServerWorld world, BlockPos pos, Direction side, boolean blockUpdate) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.blockUpdate = blockUpdate;

        IFluidHandler fluidHandler = new FluidHandlerBlock(world.getBlockState(pos), world, pos);
        this.targetStorage = getComponent()
                .getStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .wrapComponentStorage(fluidHandler);
    }

    protected void sendBlockUpdate() {
        world.neighborChanged(pos, Blocks.AIR, pos);
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
        if (world.getDimensionType().isUltrawarm()
                && stack.getFluid().getAttributes().doesVaporize(world, pos, stack)) {
            return FluidStack.EMPTY;
        }

        FluidStack remaining = this.targetStorage.insert(stack, simulate);
        if (!simulate && stack.getAmount() != remaining.getAmount()) {
            postInsert(stack);
        }
        return remaining;
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
