package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatOptionsScreen extends SimpleOptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.chat.title");

    public ChatOptionsScreen(Screen pLastScreen, Options pOptions) {
        super(
            pLastScreen,
            pOptions,
            TITLE,
            new OptionInstance[]{
                pOptions.chatVisibility(),
                pOptions.chatColors(),
                pOptions.chatLinks(),
                pOptions.chatLinksPrompt(),
                pOptions.chatOpacity(),
                pOptions.textBackgroundOpacity(),
                pOptions.chatScale(),
                pOptions.chatLineSpacing(),
                pOptions.chatDelay(),
                pOptions.chatWidth(),
                pOptions.chatHeightFocused(),
                pOptions.chatHeightUnfocused(),
                pOptions.narrator(),
                pOptions.autoSuggestions(),
                pOptions.hideMatchedNames(),
                pOptions.reducedDebugInfo(),
                pOptions.onlyShowSecureChat()
            }
        );
    }
}