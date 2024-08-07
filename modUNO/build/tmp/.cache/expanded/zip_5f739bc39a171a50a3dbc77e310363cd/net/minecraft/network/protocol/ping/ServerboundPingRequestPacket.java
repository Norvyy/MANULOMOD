package net.minecraft.network.protocol.ping;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPingRequestPacket implements Packet<ServerPingPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPingRequestPacket> STREAM_CODEC = Packet.codec(
        ServerboundPingRequestPacket::write, ServerboundPingRequestPacket::new
    );
    private final long time;

    public ServerboundPingRequestPacket(long pTime) {
        this.time = pTime;
    }

    private ServerboundPingRequestPacket(FriendlyByteBuf p_335917_) {
        this.time = p_335917_.readLong();
    }

    private void write(FriendlyByteBuf p_334813_) {
        p_334813_.writeLong(this.time);
    }

    @Override
    public PacketType<ServerboundPingRequestPacket> type() {
        return PingPacketTypes.SERVERBOUND_PING_REQUEST;
    }

    public void handle(ServerPingPacketListener pHandler) {
        pHandler.handlePingRequest(this);
    }

    public long getTime() {
        return this.time;
    }
}