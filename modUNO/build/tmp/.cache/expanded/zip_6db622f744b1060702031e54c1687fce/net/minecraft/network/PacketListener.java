package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;

public interface PacketListener {
    PacketFlow flow();

    ConnectionProtocol protocol();

    void onDisconnect(Component p_130552_);

    default void onPacketError(Packet p_330857_, Exception p_328275_) throws ReportedException {
        throw PacketUtils.makeReportedException(p_328275_, p_330857_, this);
    }

    boolean isAcceptingMessages();

    default boolean shouldHandleMessage(Packet<?> p_299735_) {
        return this.isAcceptingMessages();
    }

    default void fillCrashReport(CrashReport p_311292_) {
        CrashReportCategory crashreportcategory = p_311292_.addCategory("Connection");
        crashreportcategory.setDetail("Protocol", () -> this.protocol().id());
        crashreportcategory.setDetail("Flow", () -> this.flow().toString());
        this.fillListenerSpecificCrashDetails(crashreportcategory);
    }

    default void fillListenerSpecificCrashDetails(CrashReportCategory p_310872_) {
    }
}