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

    public ExtendedFakePlayer(WorldServer world) {
        super(world, PROFILE);
        this.interactionManager.setGameType(GameType.SURVIVAL);
        this.connection = new FakeNetHandlerPlayServer(world.getMinecraftServer(), this);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.ticksSinceLastSwing = (int) (world.getTotalWorldTime() - lastUpdateTick);
        this.inventory.decrementAnimations();
    }

    @Override
    public void resetCooldown() {
        super.resetCooldown();
        lastUpdateTick = world.getTotalWorldTime();
    }
}
