package org.cyclops.integratedtunnels.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
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

    public ExtendedFakePlayer(WorldServer world) {
        super(world, PROFILE);
        this.interactionManager.setGameType(GameType.SURVIVAL);
        this.connection = new FakeNetHandlerPlayServer(world.getMinecraftServer(), this);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        int toTick = (int) (world.getTotalWorldTime() - this.lastUpdateTick);
        if (toTick > 0) {
            this.ticksSinceLastTick = toTick;
        }
        this.lastUpdateTick = world.getTotalWorldTime();

        this.ticksSinceLastSwing = (int) (world.getTotalWorldTime() - lastSwingUpdateTick);
        this.inventory.decrementAnimations();
    }

    @Override
    public void resetCooldown() {
        super.resetCooldown();
        lastSwingUpdateTick = world.getTotalWorldTime();
    }

    public void updateActiveHandSimulated() {
        if (this.isHandActive()) {
            for (int i = 0; i < this.ticksSinceLastTick; i++) {
                if (this.isHandActive()) {
                    if (!this.activeItemStack.isEmpty()) {
                        activeItemStackUseCount = net.minecraftforge.event.ForgeEventFactory.onItemUseTick(this, activeItemStack, activeItemStackUseCount);
                        if (activeItemStackUseCount > 0)
                            activeItemStack.getItem().onUsingTick(activeItemStack, this, activeItemStackUseCount);
                    }

                    if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0) {
                        this.updateItemUse(this.activeItemStack, 5);
                    }

                    if (--this.activeItemStackUseCount <= 0 && !this.world.isRemote) {
                        this.onItemUseFinish();
                        break;
                    }
                } else {
                    this.resetActiveHand();
                    break;
                }
            }
        } else {
            this.resetActiveHand();
        }
    }
}
