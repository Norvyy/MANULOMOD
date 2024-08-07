package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ReportType {
    CHAT("chat"),
    SKIN("skin"),
    USERNAME("username");

    private final String backendName;

    private ReportType(final String pName) {
        this.backendName = pName.toUpperCase(Locale.ROOT);
    }

    public String backendName() {
        return this.backendName;
    }
}