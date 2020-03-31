package org.cyclops.integratedtunnels.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

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
            fakePlayer = new ExtendedFakePlayer(world);
            FAKE_PLAYERS.put(world, fakePlayer);
        }
        return fakePlayer;
    }

    public static void setPlayerState(EntityPlayer player, EnumHand hand, BlockPos pos,
                                      float offsetX, float offsetY, float offsetZ, EnumFacing side, boolean sneaking) {
        BlockPos playerPos = side == EnumFacing.UP || side == EnumFacing.DOWN ? pos.offset(side, 2) : pos;
        player.setPosition(playerPos.getX() + offsetX, playerPos.getY() + offsetY, playerPos.getZ() + offsetZ);
        player.prevPosX = player.posX;
        player.prevPosY = player.posY;
        player.prevPosZ = player.posZ;
        player.rotationYaw = side.getOpposite().getHorizontalAngle();
        player.rotationPitch = side == EnumFacing.UP ? 90F : (side == EnumFacing.DOWN ? -90F : 0F);
        player.eyeHeight = 0F;
        player.setSneaking(sneaking);
        setHeldItemSilent(player, hand, ItemStack.EMPTY);
        player.onUpdate();
        player.onGround = true;
    }

    public static void setHeldItemSilent(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
        if (hand == EnumHand.MAIN_HAND) {
            player.inventory.mainInventory.set(player.inventory.currentItem, itemStack);
        } else if (hand == EnumHand.OFF_HAND) {
            player.inventory.offHandInventory.set(0, itemStack);
        } else {
            // Could happen if some mod messes with the hand types.
            player.setHeldItem(hand, itemStack);
        }
    }

}
