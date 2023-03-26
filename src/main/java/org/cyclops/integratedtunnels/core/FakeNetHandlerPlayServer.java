package org.cyclops.integratedtunnels.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import java.net.SocketAddress;
import java.util.Set;

/**
 * A fake {@link ServerGamePacketListenerImpl}.
 * @author rubensworks
 */
public class FakeNetHandlerPlayServer extends ServerGamePacketListenerImpl {

    public FakeNetHandlerPlayServer(MinecraftServer server, ServerPlayer player) {
        super(server, new Connection(PacketFlow.CLIENTBOUND) {
            @Override
            public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {

            }

            @Override
            public void setProtocol(ConnectionProtocol newState) {

            }

            @Override
            public void channelInactive(ChannelHandlerContext p_channelInactive_1_) {

            }

            @Override
            public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {

            }

            @Override
            public void setListener(PacketListener handler) {

            }

            @Override
            public void send(Packet<?> packetIn) {

            }

            @Override
            public void send(Packet<?> p_243248_, @Nullable PacketSendListener p_243316_) {

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
            public PacketListener getPacketListener() {
                return null;
            }

            @Override
            public Component getDisconnectedReason() {
                return null;
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
    public void disconnect(Component textComponent) {

    }

    @Override
    public void handlePlayerInput(ServerboundPlayerInputPacket packetIn) {

    }

    @Override
    public void handleMoveVehicle(ServerboundMoveVehiclePacket packetIn) {

    }

    @Override
    public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packetIn) {

    }

    @Override
    public void handleMovePlayer(ServerboundMovePlayerPacket packetIn) {

    }

    @Override
    public void teleport(double x, double y, double z, float yaw, float pitch) {

    }

    @Override
    public void teleport(double p_9781_, double p_9782_, double p_9783_, float p_9784_, float p_9785_, Set<RelativeMovement> p_9786_) {

    }

    @Override
    public void handlePlayerAction(ServerboundPlayerActionPacket packetIn) {

    }

    @Override
    public void handleUseItemOn(ServerboundUseItemOnPacket packetIn) {

    }

    @Override
    public void handleUseItem(ServerboundUseItemPacket packetIn) {

    }

    @Override
    public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packetIn) {

    }

    @Override
    public void handleResourcePackResponse(ServerboundResourcePackPacket packetIn) {

    }

    @Override
    public void handlePaddleBoat(ServerboundPaddleBoatPacket packetIn) {

    }

    @Override
    public void onDisconnect(Component reason) {

    }

    @Override
    public void send(final Packet<?> packetIn) {

    }

    @Override
    public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packetIn) {

    }

    @Override
    public void handleChat(ServerboundChatPacket packetIn) {

    }

    @Override
    public void handleAnimate(ServerboundSwingPacket packetIn) {

    }

    @Override
    public void handlePlayerCommand(ServerboundPlayerCommandPacket packetIn) {

    }

    @Override
    public void handleInteract(ServerboundInteractPacket packetIn) {

    }

    @Override
    public void handleClientCommand(ServerboundClientCommandPacket packetIn) {

    }

    @Override
    public void handleContainerClose(ServerboundContainerClosePacket packetIn) {

    }

    @Override
    public void handleContainerClick(ServerboundContainerClickPacket packetIn) {

    }

    @Override
    public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packetIn) {

    }

    @Override
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packetIn) {

    }

    @Override
    public void handleSignUpdate(ServerboundSignUpdatePacket packetIn) {

    }

    @Override
    public void handleKeepAlive(ServerboundKeepAlivePacket packetIn) {

    }

    @Override
    public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packetIn) {

    }

    @Override
    public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packetIn) {

    }

    @Override
    public void handleClientInformation(ServerboundClientInformationPacket packetIn) {

    }

    @Override
    public void handleCustomPayload(ServerboundCustomPayloadPacket packetIn) {

    }
}
