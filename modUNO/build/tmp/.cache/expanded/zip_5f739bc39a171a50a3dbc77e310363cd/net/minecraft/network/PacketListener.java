package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;

/**
 * Describes how packets are handled. There are various implementations of this class for each possible protocol (e.g.
 * PLAY, CLIENTBOUND; PLAY, SERVERBOUND; etc.)
 */
public interface PacketListener {
    PacketFlow flow();

    ConnectionProtocol protocol();

    void onDisconnect(Component pReason);

    default void onPacketError(Packet pPacket, Exception pException) throws ReportedException {
        throw PacketUtils.makeReportedException(pException, pPacket, this);
    }

    boolean isAcceptingMessages();

    default boolean shouldHandleMessage(Packet<?> pPacket) {
        return this.isAcceptingMessages();
    }

    default void fillCrashReport(CrashReport pCrashReport) {
        CrashReportCategory crashreportcategory = pCrashReport.addCategory("Connection");
        crashreportcategory.setDetail("Protocol", () -> this.protocol().id());
        crashreportcategory.setDetail("Flow", () -> this.flow().toString());
        this.fillListenerSpecificCrashDetails(crashreportcategory);
    }

    default void fillListenerSpecificCrashDetails(CrashReportCategory pCrashReportCategory) {
    }
}