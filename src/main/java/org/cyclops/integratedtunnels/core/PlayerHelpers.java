package org.cyclops.integratedtunnels.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author rubensworks
 */
public class PlayerHelpers {

    private static final Map<WorldServer, FakePlayer> FAKE_PLAYERS = new WeakHashMap<WorldServer, FakePlayer>();

    public static FakePlayer getFakePlayer(WorldServer world) {
        FakePlayer fakePlayer = FAKE_PLAYERS.get(world);
        if (fakePlayer == null) {
            fakePlayer = FakePlayerFactory.getMinecraft(world);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    public static void setPlayerState(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing side) {
        BlockPos playerPos = side == EnumFacing.UP || side == EnumFacing.DOWN ? pos.offset(side, 2) : pos;
        player.setActiveHand(hand);
        player.setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        player.rotationYaw = side.getOpposite().getHorizontalAngle();
        player.rotationPitch = side == EnumFacing.UP ? 90F : (side == EnumFacing.DOWN ? -90F : 0F);
    }

}
