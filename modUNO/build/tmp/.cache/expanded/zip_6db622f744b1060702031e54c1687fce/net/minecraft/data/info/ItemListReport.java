package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public class ItemListReport implements DataProvider {
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public ItemListReport(PackOutput p_333960_, CompletableFuture<HolderLookup.Provider> p_331732_) {
        this.output = p_333960_;
        this.registries = p_331732_;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_328088_) {
        Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("items.json");
        return this.registries.thenCompose(p_334112_ -> {
            JsonObject jsonobject = new JsonObject();
            RegistryOps<JsonElement> registryops = p_334112_.createSerializationContext(JsonOps.INSTANCE);
            p_334112_.lookupOrThrow(Registries.ITEM).listElements().forEach(p_331822_ -> {
                JsonObject jsonobject1 = new JsonObject();
                JsonArray jsonarray = new JsonArray();
                p_331822_.value().components().forEach(p_328173_ -> jsonarray.add(dumpComponent((TypedDataComponent<?>)p_328173_, registryops)));
                jsonobject1.add("components", jsonarray);
                jsonobject.add(p_331822_.getRegisteredName(), jsonobject1);
            });
            return DataProvider.saveStable(p_328088_, jsonobject, path);
        });
    }

    private static <T> JsonElement dumpComponent(TypedDataComponent<T> p_330714_, DynamicOps<JsonElement> p_328487_) {
        ResourceLocation resourcelocation = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(p_330714_.type());
        JsonElement jsonelement = p_330714_.encodeValue(p_328487_)
            .getOrThrow(p_329163_ -> new IllegalStateException("Failed to serialize component " + resourcelocation + ": " + p_329163_));
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", resourcelocation.toString());
        jsonobject.add("value", jsonelement);
        return jsonobject;
    }

    @Override
    public final String getName() {
        return "Item List";
    }
}