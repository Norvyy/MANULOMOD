package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontOptionsScreen extends SimpleOptionsSubScreen {
    private static OptionInstance<?>[] options(Options pOptions) {
        return new OptionInstance[]{pOptions.forceUnicodeFont(), pOptions.japaneseGlyphVariants()};
    }

    public FontOptionsScreen(Screen pLastScreen, Options pOptions) {
        super(pLastScreen, pOptions, Component.translatable("options.font.title"), options(pOptions));
    }
}