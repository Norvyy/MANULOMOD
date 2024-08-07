package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayeredDraw {
    public static final float Z_SEPARATION = 200.0F;
    private final List<LayeredDraw.Layer> layers = new ArrayList<>();

    public LayeredDraw add(LayeredDraw.Layer p_332264_) {
        this.layers.add(p_332264_);
        return this;
    }

    public LayeredDraw add(LayeredDraw p_328749_, BooleanSupplier p_332055_) {
        return this.add((p_331839_, p_333777_) -> {
            if (p_332055_.getAsBoolean()) {
                p_328749_.renderInner(p_331839_, p_333777_);
            }
        });
    }

    public void render(GuiGraphics p_335429_, float p_332136_) {
        p_335429_.pose().pushPose();
        this.renderInner(p_335429_, p_332136_);
        p_335429_.pose().popPose();
    }

    private void renderInner(GuiGraphics p_333655_, float p_331829_) {
        for (LayeredDraw.Layer layereddraw$layer : this.layers) {
            layereddraw$layer.render(p_333655_, p_331829_);
            p_333655_.pose().translate(0.0F, 0.0F, 200.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface Layer {
        void render(GuiGraphics p_328217_, float p_327998_);
    }
}