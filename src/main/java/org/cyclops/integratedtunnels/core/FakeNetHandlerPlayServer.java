package org.cyclops.integratedtunnels.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import java.net.SocketAddress;
import java.util.Set;

/**
 * A fake {@link ServerPlayNetHandler}.
 * @author rubensworks
 */
public class FakeNetHandlerPlayServer extends ServerPlayNetHandler {

    public FakeNetHandlerPlayServer(MinecraftServer server, ServerPlayerEntity player) {
        super(server, new NetworkManager(PacketDirection.CLIENTBOUND) {
            @Override
            public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {

            }

            @Override
            public void setConnectionState(ProtocolType newState) {

            }

            @Override
            public void channelInactive(ChannelHandlerContext p_channelInactive_1_) {

            }

            @Override
            public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {

            }

            @Override
            public void setNetHandler(INetHandler handler) {

            }

            @Override
            public void sendPacket(IPacket<?> packetIn) {

            }

            @Override
            public void sendPacket(IPacket<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>> p_201058_2_) {

            }

            @Override
            public SocketAddress getRemoteAddress() {
                return null;
            }

            @Override
            public boolean isLocalChannel() {
                return false;
            }

            @Override
            public void enableEncryption(SecretKey key) {

            }

            @Override
            public boolean isChannelOpen() {
                return false;
            }

            @Override
            public INetHandler getNetHandler() {
                return null;
            }

            @Override
            public ITextComponent getExitMessage() {
                return null;
            }

            @Override
            public void setCompressionThreshold(int threshold) {

            }

            @Override
            public void disableAutoRead() {

            }

            @Override
            public void handleDisconnection() {

            }

            @Override
            public Channel channel() {
                return super.channel();
            }
        }, player);
    }

    @Override
    public void tick() {

    }

    @Override
    public void disconnect(ITextComponent textComponent) {

    }

    @Override
    public void processInput(CInputPacket packetIn) {

    }

    @Override
    public void processVehicleMove(CMoveVehiclePacket packetIn) {

    }

    @Override
    public void processConfirmTeleport(CConfirmTeleportPacket packetIn) {

    }

    @Override
    public void processPlayer(CPlayerPacket packetIn) {

    }

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {

    }

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<SPlayerPositionLookPacket.Flags> relativeSet) {

    }

    @Override
    public void processPlayerDigging(CPlayerDiggingPacket packetIn) {

    }

    @Override
    public void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket packetIn) {

    }

    @Override
    public void processTryUseItem(CPlayerTryUseItemPacket packetIn) {

    }

    @Override
    public void handleSpectate(CSpectatePacket packetIn) {

    }

    @Override
    public void handleResourcePackStatus(CResourcePackStatusPacket packetIn) {

    }

    @Override
    public void processSteerBoat(CSteerBoatPacket packetIn) {

    }

    @Override
    public void onDisconnect(ITextComponent reason) {

    }

    @Override
    public void sendPacket(final IPacket<?> packetIn) {

    }

    @Override
    public void processHeldItemChange(CHeldItemChangePacket packetIn) {

    }

    @Override
    public void processChatMessage(CChatMessagePacket packetIn) {

    }

    @Override
    public void handleAnimation(CAnimateHandPacket packetIn) {

    }

    @Override
    public void processEntityAction(CEntityActionPacket packetIn) {

    }

    @Override
    public void processUseEntity(CUseEntityPacket packetIn) {

    }

    @Override
    public void processClientStatus(CClientStatusPacket packetIn) {

    }

    @Override
    public void processCloseWindow(CCloseWindowPacket packetIn) {

    }

    @Override
    public void processClickWindow(CClickWindowPacket packetIn) {

    }

    @Override
    public void processEnchantItem(CEnchantItemPacket packetIn) {

    }

    @Override
    public void processCreativeInventoryAction(CCreativeInventoryActionPacket packetIn) {

    }

    @Override
    public void processConfirmTransaction(CConfirmTransactionPacket packetIn) {

    }

    @Override
    public void processUpdateSign(CUpdateSignPacket packetIn) {

    }

    @Override
    public void processKeepAlive(CKeepAlivePacket packetIn) {

    }

    @Override
    public void processPlayerAbilities(CPlayerAbilitiesPacket packetIn) {

    }

    @Override
    public void processTabComplete(CTabCompletePacket packetIn) {

    }

    @Override
    public void processClientSettings(CClientSettingsPacket packetIn) {

    }

    @Override
    public void processCustomPayload(CCustomPayloadPacket packetIn) {

    }
}
