package net.minecraft.world.level.block.entity.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record VaultConfig(
    ResourceKey<LootTable> lootTable,
    double activationRange,
    double deactivationRange,
    ItemStack keyItem,
    Optional<ResourceKey<LootTable>> overrideLootTableToDisplay,
    PlayerDetector playerDetector,
    PlayerDetector.EntitySelector entitySelector
) {
    static final String TAG_NAME = "config";
    static VaultConfig DEFAULT = new VaultConfig();
    static Codec<VaultConfig> CODEC = RecordCodecBuilder.<VaultConfig>create(
            p_329931_ -> p_329931_.group(
                        ResourceKey.codec(Registries.LOOT_TABLE)
                            .lenientOptionalFieldOf("loot_table", DEFAULT.lootTable())
                            .forGetter(VaultConfig::lootTable),
                        Codec.DOUBLE.lenientOptionalFieldOf("activation_range", Double.valueOf(DEFAULT.activationRange())).forGetter(VaultConfig::activationRange),
                        Codec.DOUBLE.lenientOptionalFieldOf("deactivation_range", Double.valueOf(DEFAULT.deactivationRange())).forGetter(VaultConfig::deactivationRange),
                        ItemStack.lenientOptionalFieldOf("key_item").forGetter(VaultConfig::keyItem),
                        ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("override_loot_table_to_display").forGetter(VaultConfig::overrideLootTableToDisplay)
                    )
                    .apply(p_329931_, VaultConfig::new)
        )
        .validate(VaultConfig::validate);

    private VaultConfig() {
        this(
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD,
            4.0,
            4.5,
            new ItemStack(Items.TRIAL_KEY),
            Optional.empty(),
            PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
            PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
        );
    }

    public VaultConfig(ResourceKey<LootTable> p_335368_, double p_335328_, double p_335598_, ItemStack p_328193_, Optional<ResourceKey<LootTable>> p_333693_) {
        this(p_335368_, p_335328_, p_335598_, p_328193_, p_333693_, DEFAULT.playerDetector(), DEFAULT.entitySelector());
    }

    private DataResult<VaultConfig> validate() {
        return this.activationRange > this.deactivationRange
            ? DataResult.error(() -> "Activation range must (" + this.activationRange + ") be less or equal to deactivation range (" + this.deactivationRange + ")")
            : DataResult.success(this);
    }
}