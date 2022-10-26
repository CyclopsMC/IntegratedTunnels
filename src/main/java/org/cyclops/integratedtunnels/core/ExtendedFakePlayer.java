package org.cyclops.integratedtunnels.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

/**
 * An extended fake player with more capabilities.
 * @author rubensworks
 */
public class ExtendedFakePlayer extends FakePlayer {

    private static GameProfile PROFILE = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BB57-13D2C99CAE77"), "[IntegratedTunnels]");

    private long lastUpdateTick = 0;
    private long lastSwingUpdateTick = 0;
    private int ticksSinceLastTick = 0;

    public ExtendedFakePlayer(ServerLevel world) {
        super(world, PROFILE);
        this.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        this.connection = new FakeNetHandlerPlayServer(world.getServer(), this);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        int toTick = (int) (level.getGameTime() - this.lastUpdateTick);
        if (toTick > 0) {
            this.ticksSinceLastTick = toTick;
        }
        this.lastUpdateTick = level.getGameTime();

        this.attackStrengthTicker = (int) (level.getGameTime() - lastSwingUpdateTick);
        this.getInventory().tick();
        this.getCooldowns().tick();
    }

    @Override
    public void resetAttackStrengthTicker() {
        super.resetAttackStrengthTicker();
        lastSwingUpdateTick = level.getGameTime();
    }

    public void updateActiveHandSimulated() {
        if (this.isUsingItem()) {
            for (int i = 0; i < this.ticksSinceLastTick; i++) {
                if (this.isUsingItem()) {
                    ItemStack itemstack = this.getItemInHand(this.getUsedItemHand());
                    if (net.minecraftforge.common.ForgeHooks.canContinueUsing(this.useItem, itemstack)) {
                        this.useItem = itemstack;
                    }
                    // Based on LivingEntity#updateActiveHand
                    if (itemstack == this.useItem) {
                        if (!this.useItem.isEmpty()) {
                            useItemRemaining = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, useItem, useItemRemaining);
                            if (useItemRemaining > 0)
                                useItem.getItem().onUsingTick(useItem, this, useItemRemaining);
                        }

                        if (this.getUseItemRemainingTicks() <= 25 && this.getUseItemRemainingTicks() % 4 == 0) {
                            this.triggerItemUseEffects(this.useItem, 5);
                        }

                        if (--this.useItemRemaining <= 0 && !this.level.isClientSide() && !this.useItem.useOnRelease()) {
                            this.completeUsingItem();
                            break;
                        }
                    } else {
                        this.stopUsingItem();
                        break;
                    }
                }
            }
        } else {
            this.stopUsingItem();
        }
    }
}
