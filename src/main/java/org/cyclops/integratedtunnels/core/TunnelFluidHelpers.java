package org.cyclops.integratedtunnels.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import org.cyclops.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicateFluidStackList;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicateFluidStackNbt;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicateFluidStackOperator;
import org.cyclops.integratedtunnels.part.aspect.ITunnelConnection;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class TunnelFluidHelpers {

    public static final IngredientPredicate<FluidStack, Integer> MATCH_NONE = new IngredientPredicate<FluidStack, Integer>(IngredientComponent.FLUIDSTACK, null, FluidMatch.EXACT, false, true, 0, false) {
        @Override
        public boolean test(FluidStack input) {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == TunnelFluidHelpers.MATCH_NONE;
        }

        @Override
        public int hashCode() {
            return 9991029;
        }
    };

    public static IngredientPredicate<FluidStack, Integer> matchAll(final int amount, final boolean exactAmount) {
        return new IngredientPredicate<FluidStack, Integer>(IngredientComponent.FLUIDSTACK, new FluidStack(FluidRegistry.WATER, amount), exactAmount ? FluidMatch.AMOUNT : FluidMatch.ANY, false, false, amount, exactAmount) {
            @Override
            public boolean test(FluidStack input) {
                return true;
            }
        };
    }

    public static IngredientPredicate<FluidStack, Integer> matchFluidStack(final FluidStack fluidStack, final boolean checkFluid,
                                                                           final boolean checkAmount, final boolean checkNbt,
                                                                           final boolean blacklist, final boolean exactAmount) {
        int matchFlags = FluidMatch.ANY;
        if (checkFluid)  matchFlags = matchFlags | FluidMatch.FLUID;
        if (checkNbt)    matchFlags = matchFlags | FluidMatch.NBT;
        if (checkAmount) matchFlags = matchFlags | FluidMatch.AMOUNT;
        return new IngredientPredicate<FluidStack, Integer>(IngredientComponent.FLUIDSTACK, fluidStack != null ? fluidStack.copy() : null, matchFlags, blacklist, fluidStack == null && !blacklist,
                FluidHelpers.getAmount(fluidStack), exactAmount) {
            @Override
            public boolean test(@Nullable FluidStack input) {
                boolean result = areFluidStackEqual(input, fluidStack, checkFluid, checkAmount, checkNbt);
                if (blacklist) {
                    result = !result;
                }
                return result;
            }
        };
    }

    public static IngredientPredicate<FluidStack, Integer> matchFluidStacks(final IValueTypeListProxy<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack> fluidStacks,
                                                                            final boolean checkFluid, final boolean checkAmount, final boolean checkNbt,
                                                                            final boolean blacklist, final int amount, final boolean exactAmount) {
        return new IngredientPredicateFluidStackList(blacklist, amount, exactAmount, fluidStacks, checkFluid, checkAmount, checkNbt);
    }

    public static IngredientPredicate<FluidStack, Integer> matchPredicate(final PartTarget partTarget, final IOperator predicate,
                                                                          final int amount, final boolean exactAmount) {
        return new IngredientPredicateFluidStackOperator(amount, exactAmount, predicate, partTarget);
    }

    public static IngredientPredicate<FluidStack, Integer> matchNbt(final NBTTagCompound tag, final boolean subset, final boolean superset, final boolean requireNbt, final boolean recursive,
                                                                    final boolean blacklist,
                                                                    final int amount, final boolean exactAmount) {
        return new IngredientPredicateFluidStackNbt(blacklist, amount, exactAmount, requireNbt, subset, tag, recursive, superset);
    }

    public static boolean areFluidStackEqual(FluidStack stackA, FluidStack stackB,
                                             boolean checkFluid, boolean checkAmount, boolean checkNbt) {
        if (stackA == null && stackB == null) return true;
        if (stackA != null && stackB != null) {
            if (checkAmount && stackA.amount != stackB.amount) return false;
            if (checkFluid && stackA.getFluid() != stackB.getFluid()) return false;
            if (checkNbt && !FluidStack.areFluidStackTagsEqual(stackA, stackB)) return false;
            return true;
        }
        return false;
    }

    /**
     * Place fluids from the given source in the world.
     * @param network The network in which the movement is happening.
     * @param ingredientsNetwork The network in which the movement is happening.
     * @param channel The channel.
     * @param connection The connection object.
     * @param source The source fluid handler.
     * @param world The target world.
     * @param pos The target position.
     * @param fluidStackMatcher The fluidstack match predicate.
     * @param blockUpdate If a block update should occur after placement.
     * @param ignoreReplacable If replacable blocks should be overriden when placing blocks.
     * @param craftIfFailed If the exact ingredient from ingredientPredicate should be crafted if transfer failed.
     * @return The placed fluid.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    public static FluidStack placeFluids(INetwork network, IPositionedAddonsNetworkIngredients<FluidStack, Integer> ingredientsNetwork,
                                         int channel, ITunnelConnection connection,
                                         IIngredientComponentStorage<FluidStack, Integer> source, final World world, final BlockPos pos,
                                         IngredientPredicate<FluidStack, Integer> fluidStackMatcher, boolean blockUpdate,
                                         boolean ignoreReplacable, boolean craftIfFailed) throws EvaluationException {
        IBlockState destBlockState = world.getBlockState(pos);
        final Material destMaterial = destBlockState.getMaterial();
        final boolean isDestNonSolid = !destMaterial.isSolid();
        final boolean isDestReplaceable = destBlockState.getBlock().isReplaceable(world, pos);
        if (!world.isAirBlock(pos)
                && (!isDestNonSolid || !(ignoreReplacable && isDestReplaceable) || destMaterial.isLiquid())) {
            return null;
        }

        IIngredientComponentStorage<FluidStack, Integer> destination = new FluidStorageBlockWrapper((WorldServer) world, pos, null, blockUpdate);
        return TunnelHelpers.moveSingleStateOptimized(network, ingredientsNetwork, channel, connection, source,
                -1, destination, -1, fluidStackMatcher, PartPos.of(world, pos, null), craftIfFailed);
    }

    /**
     * Place fluids from the given source in the world.
     * @param network The network in which the movement is happening.
     * @param ingredientsNetwork The ingredients network in which the movement is happening.
     * @param channel The channel.
     * @param connection The connection object.
     * @param world The source world.
     * @param pos The source position.
     * @param side The source side.
     * @param destination The target fluid handler.
     * @param fluidStackMatcher The fluidstack match predicate.
     * @return The picked-up fluid.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    public static FluidStack pickUpFluids(INetwork network, IPositionedAddonsNetworkIngredients<FluidStack, Integer> ingredientsNetwork,
                                          int channel, ITunnelConnection connection, World world, BlockPos pos, EnumFacing side,
                                          IIngredientComponentStorage<FluidStack, Integer> destination,
                                          IngredientPredicate<FluidStack, Integer> fluidStackMatcher) throws EvaluationException {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
            IIngredientComponentStorage<FluidStack, Integer> source = new FluidStorageBlockWrapper((WorldServer) world, pos, side, false);
            return TunnelHelpers.moveSingleStateOptimized(network, ingredientsNetwork, channel, connection, source,
                    -1, destination, -1, fluidStackMatcher, PartPos.of(world, pos, side), false);
        }
        return null;
    }

    /**
     * Helper function to get a copy of the given fluidstack with the given amount.
     * @param prototype A prototype fluidstack.
     * @param count A new amount.
     * @return A copy of the given fluidstack with the given count.
     */
    public static FluidStack prototypeWithCount(FluidStack prototype, int count) {
        if (prototype == null || prototype.amount != count) {
            if (prototype == null) {
                return count == 0 ? null : new FluidStack(FluidRegistry.WATER, count);
            } else {
                prototype = new FluidStack(prototype, count);
            }
        }
        return prototype;
    }

}
