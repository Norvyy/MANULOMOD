package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundProjectilePowerPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundProjectilePowerPacket> STREAM_CODEC = Packet.codec(
        ClientboundProjectilePowerPacket::write, ClientboundProjectilePowerPacket::new
    );
    private final int id;
    private final double xPower;
    private final double yPower;
    private final double zPower;

    public ClientboundProjectilePowerPacket(int p_336104_, double p_330780_, double p_328679_, double p_327674_) {
        this.id = p_336104_;
        this.xPower = p_330780_;
        this.yPower = p_328679_;
        this.zPower = p_327674_;
    }

    private ClientboundProjectilePowerPacket(FriendlyByteBuf p_328922_) {
        this.id = p_328922_.readVarInt();
        this.xPower = p_328922_.readDouble();
        this.yPower = p_328922_.readDouble();
        this.zPower = p_328922_.readDouble();
    }

    private void write(FriendlyByteBuf p_331545_) {
        p_331545_.writeVarInt(this.id);
        p_331545_.writeDouble(this.xPower);
        p_331545_.writeDouble(this.yPower);
        p_331545_.writeDouble(this.zPower);
    }

    @Override
    public PacketType<ClientboundProjectilePowerPacket> type() {
        return GamePacketTypes.CLIENTBOUND_PROJECTILE_POWER;
    }

    public void handle(ClientGamePacketListener p_329858_) {
        p_329858_.handleProjectilePowerPacket(this);
    }

    public int getId() {
        return this.id;
    }

    public double getXPower() {
        return this.xPower;
    }

    public double getYPower() {
        return this.yPower;
    }

    public double getZPower() {
        return this.zPower;
    }
}