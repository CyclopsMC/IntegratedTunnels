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
import javax.crypto.Cipher;
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
            public void setProtocol(ProtocolType newState) {

            }

            @Override
            public void channelInactive(ChannelHandlerContext p_channelInactive_1_) {

            }

            @Override
            public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {

            }

            @Override
            public void setListener(INetHandler handler) {

            }

            @Override
            public void send(IPacket<?> packetIn) {

            }

            @Override
            public void send(IPacket<?> packetIn, @Nullable GenericFutureListener<? extends Future<? super Void>> p_201058_2_) {

            }

            @Override
            public SocketAddress getRemoteAddress() {
                return null;
            }

            @Override
            public boolean isMemoryConnection() {
                return false;
            }

            @Override
            public void setEncryptionKey(Cipher p_244777_1_, Cipher p_244777_2_) {

            }

            @Override
            public boolean isConnected() {
                return false;
            }

            @Override
            public INetHandler getPacketListener() {
                return null;
            }

            @Override
            public ITextComponent getDisconnectedReason() {
                return null;
            }

            @Override
            public void setupCompression(int threshold) {

            }

            @Override
            public void setReadOnly() {

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
    public void handlePlayerInput(CInputPacket packetIn) {

    }

    @Override
    public void handleMoveVehicle(CMoveVehiclePacket packetIn) {

    }

    @Override
    public void handleAcceptTeleportPacket(CConfirmTeleportPacket packetIn) {

    }

    @Override
    public void handleMovePlayer(CPlayerPacket packetIn) {

    }

    @Override
    public void teleport(double x, double y, double z, float yaw, float pitch) {

    }

    @Override
    public void teleport(double x, double y, double z, float yaw, float pitch, Set<SPlayerPositionLookPacket.Flags> relativeSet) {

    }

    @Override
    public void handlePlayerAction(CPlayerDiggingPacket packetIn) {

    }

    @Override
    public void handleUseItemOn(CPlayerTryUseItemOnBlockPacket packetIn) {

    }

    @Override
    public void handleUseItem(CPlayerTryUseItemPacket packetIn) {

    }

    @Override
    public void handleTeleportToEntityPacket(CSpectatePacket packetIn) {

    }

    @Override
    public void handleResourcePackResponse(CResourcePackStatusPacket packetIn) {

    }

    @Override
    public void handlePaddleBoat(CSteerBoatPacket packetIn) {

    }

    @Override
    public void onDisconnect(ITextComponent reason) {

    }

    @Override
    public void send(final IPacket<?> packetIn) {

    }

    @Override
    public void handleSetCarriedItem(CHeldItemChangePacket packetIn) {

    }

    @Override
    public void handleChat(CChatMessagePacket packetIn) {

    }

    @Override
    public void handleAnimate(CAnimateHandPacket packetIn) {

    }

    @Override
    public void handlePlayerCommand(CEntityActionPacket packetIn) {

    }

    @Override
    public void handleInteract(CUseEntityPacket packetIn) {

    }

    @Override
    public void handleClientCommand(CClientStatusPacket packetIn) {

    }

    @Override
    public void handleContainerClose(CCloseWindowPacket packetIn) {

    }

    @Override
    public void handleContainerClick(CClickWindowPacket packetIn) {

    }

    @Override
    public void handleContainerButtonClick(CEnchantItemPacket packetIn) {

    }

    @Override
    public void handleSetCreativeModeSlot(CCreativeInventoryActionPacket packetIn) {

    }

    @Override
    public void handleContainerAck(CConfirmTransactionPacket packetIn) {

    }

    @Override
    public void handleSignUpdate(CUpdateSignPacket packetIn) {

    }

    @Override
    public void handleKeepAlive(CKeepAlivePacket packetIn) {

    }

    @Override
    public void handlePlayerAbilities(CPlayerAbilitiesPacket packetIn) {

    }

    @Override
    public void handleCustomCommandSuggestions(CTabCompletePacket packetIn) {

    }

    @Override
    public void handleClientInformation(CClientSettingsPacket packetIn) {

    }

    @Override
    public void handleCustomPayload(CCustomPayloadPacket packetIn) {

    }
}
