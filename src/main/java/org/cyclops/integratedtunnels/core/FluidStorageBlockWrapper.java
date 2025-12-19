package org.cyclops.integratedtunnels.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.IModHelpersNeoForge;
import org.cyclops.integratedtunnels.GeneralConfig;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An item storage for world block placement.
 * @author rubensworks
 */
public class FluidStorageBlockWrapper implements IIngredientComponentStorage<FluidStack, Integer> {

    private final ServerLevel world;
    private final BlockPos pos;
    private final Direction side;
    private final boolean blockUpdate;
    private final FluidStorageBlockWrapper.Journal journal;

    private final IIngredientComponentStorage<FluidStack, Integer> targetStorage;

    private FluidStack inserted;
    private FluidStack extracted;

    public FluidStorageBlockWrapper(ServerLevel world, BlockPos pos, Direction side, boolean blockUpdate) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.blockUpdate = blockUpdate;
        this.journal = new FluidStorageBlockWrapper.Journal();

        ResourceHandler<FluidResource> fluidHandler = new FluidHandlerBlock(world.getBlockState(pos), world, pos);
        this.targetStorage = getComponent()
                .getStorageWrapperHandler(Capabilities.Fluid.BLOCK)
                .wrapComponentStorage(fluidHandler);
    }

    protected void sendBlockUpdate() {
        world.neighborChanged(pos, Blocks.AIR, null);
    }

    protected void postInsert(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            SoundEvent soundevent = moved.getFluid().getFluidType().getSound(moved, SoundActions.BUCKET_EMPTY);
            if (soundevent != null) {
                world.playSound(null, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        if (blockUpdate) {
            sendBlockUpdate();
        }
    }

    protected void postExtract(FluidStack moved) {
        if (moved != null && GeneralConfig.worldInteractionEvents) {
            SoundEvent soundevent = moved.getFluid().getFluidType().getSound(moved, SoundActions.BUCKET_FILL);
            if (soundevent != null) {
                world.playSound(null, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
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
        return IModHelpersNeoForge.get().getFluidHelpers().getBucketVolume();
    }

    @Override
    public FluidStack insert(@Nonnull FluidStack stack, TransactionContext transaction) {
        if (world.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)
                && stack.getFluid().getFluidType().isVaporizedOnPlacement(world, pos, stack)) {
            return FluidStack.EMPTY;
        }

        this.journal.updateSnapshots(transaction);
        FluidStack remaining = this.targetStorage.insert(stack, transaction);
        if (stack.getAmount() != remaining.getAmount()) {
            this.inserted = stack;
        }
        return remaining;
    }

    @Override
    public FluidStack extract(@Nonnull FluidStack prototype, Integer matchCondition, TransactionContext transaction) {
        this.journal.updateSnapshots(transaction);
        FluidStack extracted = targetStorage.extract(prototype, matchCondition, transaction);
        this.extracted = extracted;
        return extracted;
    }

    @Override
    public FluidStack extract(long maxQuantity, TransactionContext transaction) {
        this.journal.updateSnapshots(transaction);
        FluidStack extracted = targetStorage.extract(maxQuantity, transaction);
        this.extracted = extracted;
        return extracted;
    }

    private class Journal extends SnapshotJournal<Void> {

        @Override
        protected Void createSnapshot() {
            return null;
        }

        @Override
        protected void revertToSnapshot(Void unused) {

        }

        @Override
        protected void onRootCommit(Void originalState) {
            super.onRootCommit(originalState);
            if (inserted != null) {
                postInsert(inserted);
            }
            if (extracted != null) {
                postExtract(extracted);
            }
            inserted = null;
            extracted = null;
        }
    }

}
