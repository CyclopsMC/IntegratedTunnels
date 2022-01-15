package org.cyclops.integratedtunnels.core;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author rubensworks
 */
public class PlayerHelpers {

    private static final Map<ServerLevel, FakePlayer> FAKE_PLAYERS = new WeakHashMap<ServerLevel, FakePlayer>();

    public static FakePlayer getFakePlayer(ServerLevel world) {
        FakePlayer fakePlayer = FAKE_PLAYERS.get(world);
        if (fakePlayer == null) {
            fakePlayer = new ExtendedFakePlayer(world);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    public static void setPlayerState(Player player, InteractionHand hand, BlockPos pos,
                                      double offsetX, double offsetY, double offsetZ, Direction side, boolean sneaking) {
        offsetY = side == Direction.DOWN ? -offsetY : offsetY;
        player.setPos(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ);
        player.xo = player.getX();
        player.yo = player.getY();
        player.zo = player.getZ();
        player.yRotO = side.getOpposite().toYRot();
        player.xRotO = side == Direction.UP ? 90F : (side == Direction.DOWN ? -90F : 0F);
        player.eyeHeight = 0F;
        player.setShiftKeyDown(sneaking);
        setHeldItemSilent(player, hand, ItemStack.EMPTY);
        player.tick();
        player.setOnGround(true);
    }

    public static void setHeldItemSilent(Player player, InteractionHand hand, ItemStack itemStack) {
        if (hand == InteractionHand.MAIN_HAND) {
            player.getInventory().items.set(player.getInventory().selected, itemStack);
        } else if (hand == InteractionHand.OFF_HAND) {
            player.getInventory().offhand.set(0, itemStack);
        } else {
            // Could happen if some mod messes with the hand types.
            player.setItemInHand(hand, itemStack);
        }
    }

}
