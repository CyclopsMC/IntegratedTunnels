package org.cyclops.integratedtunnels.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author rubensworks
 */
public class PlayerHelpers {

    private static final Map<ServerWorld, FakePlayer> FAKE_PLAYERS = new WeakHashMap<ServerWorld, FakePlayer>();

    public static FakePlayer getFakePlayer(ServerWorld world) {
        FakePlayer fakePlayer = FAKE_PLAYERS.get(world);
        if (fakePlayer == null) {
            fakePlayer = new ExtendedFakePlayer(world);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    public static void setPlayerState(PlayerEntity player, Hand hand, BlockPos pos,
                                      double offsetX, double offsetY, double offsetZ, Direction side, boolean sneaking) {
        offsetY = side == Direction.DOWN ? -offsetY : offsetY;
        player.setPosition(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ);
        player.prevPosX = player.getPosX();
        player.prevPosY = player.getPosY();
        player.prevPosZ = player.getPosZ();
        player.rotationYaw = side.getOpposite().getHorizontalAngle();
        player.rotationPitch = side == Direction.UP ? 90F : (side == Direction.DOWN ? -90F : 0F);
        player.eyeHeight = 0F;
        player.setSneaking(sneaking);
        setHeldItemSilent(player, hand, ItemStack.EMPTY);
        player.tick();
        player.setOnGround(true);
    }

    public static void setHeldItemSilent(PlayerEntity player, Hand hand, ItemStack itemStack) {
        if (hand == Hand.MAIN_HAND) {
            player.inventory.mainInventory.set(player.inventory.currentItem, itemStack);
        } else if (hand == Hand.OFF_HAND) {
            player.inventory.offHandInventory.set(0, itemStack);
        } else {
            // Could happen if some mod messes with the hand types.
            player.setHeldItem(hand, itemStack);
        }
    }

}
