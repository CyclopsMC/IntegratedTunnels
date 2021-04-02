package org.cyclops.integratedtunnels.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
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

    public ExtendedFakePlayer(ServerWorld world) {
        super(world, PROFILE);
        this.interactionManager.setGameType(GameType.SURVIVAL);
        this.connection = new FakeNetHandlerPlayServer(world.getServer(), this);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        int toTick = (int) (world.getGameTime() - this.lastUpdateTick);
        if (toTick > 0) {
            this.ticksSinceLastTick = toTick;
        }
        this.lastUpdateTick = world.getGameTime();

        this.ticksSinceLastSwing = (int) (world.getGameTime() - lastSwingUpdateTick);
        this.inventory.tick();
    }

    @Override
    public void resetCooldown() {
        super.resetCooldown();
        lastSwingUpdateTick = world.getGameTime();
    }

    public void updateActiveHandSimulated() {
        if (this.isHandActive()) {
            for (int i = 0; i < this.ticksSinceLastTick; i++) {
                if (this.isHandActive()) {
                    ItemStack itemstack = this.getHeldItem(this.getActiveHand());
                    if (net.minecraftforge.common.ForgeHooks.canContinueUsing(this.activeItemStack, itemstack)) {
                        this.activeItemStack = itemstack;
                    }
                    // Based on LivingEntity#updateActiveHand
                    if (itemstack == this.activeItemStack) {
                        if (!this.activeItemStack.isEmpty()) {
                            activeItemStackUseCount = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
                            if (activeItemStackUseCount > 0)
                                activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
                        }

                        if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0) {
                            this.triggerItemUseEffects(this.activeItemStack, 5);
                        }

                        if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote() && !this.activeItemStack.isCrossbowStack()) {
                            this.onItemUseFinish();
                            break;
                        }
                    } else {
                        this.resetActiveHand();
                        break;
                    }
                }
            }
        } else {
            this.resetActiveHand();
        }
    }
}
