package org.cyclops.integratedtunnels.core;

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

}
