package net.fireofpower.firesenderexpansion.effect_dimension_matcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fireofpower.firesenderexpansion.FiresEnderExpansion;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EffectDimensionMatcher implements PreparableReloadListener {
    public static final EffectDimensionMatcher INSTANCE = new EffectDimensionMatcher();

    private static final ResourceLocation DATA_FILE = ResourceLocation.fromNamespaceAndPath(FiresEnderExpansion.MODID, "adaptable_dimensions.json");

    private Map<String,EffectDetails> dimensionToEffectsCache = new HashMap<>();

    private EffectDimensionMatcher(){}

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pBarrier,
                                          ResourceManager pResourceManager,
                                          ProfilerFiller pPreparationsProfiler,
                                          ProfilerFiller pReloadProfiler,
                                          Executor pBackgroundExecutor,
                                          Executor pGameExecutor) {

        CompletableFuture<List<String>> prepareFuture = CompletableFuture.supplyAsync(() -> loadFromDisk(pResourceManager), pBackgroundExecutor);

        return prepareFuture
                .thenCompose(pBarrier::wait)
                .thenAcceptAsync(loaded -> {
                    FiresEnderExpansion.LOGGER.debug("Applied {} adaptable dimensions.", loaded.size());
                }, pGameExecutor);
    }

    private List<String> loadFromDisk(ResourceManager resourceManager) {
        List<String> loaded = new ArrayList<>();
        Map<String,EffectDetails> cache = new HashMap<>();

        Optional<Resource> resource = resourceManager.getResource(DATA_FILE);
        if (resource.isEmpty()) {
            FiresEnderExpansion.LOGGER.warn("Fire's Ender Expansion adaptable_dimensions.json not found at '{}'.", DATA_FILE);
            return loaded;
        }

        try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray entries = root.getAsJsonArray("entries");

            if (entries == null) {
                FiresEnderExpansion.LOGGER.warn("Fire's Ender Expansion adaptable_dimensions.json has no 'entries' array.");
                return loaded;
            }

            for (JsonElement el : entries) {
                JsonObject obj = el.getAsJsonObject();
                String dimension = obj.get("dimension").getAsString();
                String id = obj.get("id").getAsString();
                int duration = obj.get("duration").getAsInt();
                int amplifier = obj.get("amplifier").getAsInt();
                FiresEnderExpansion.LOGGER.warn("Putting {} id and {} duration", id, duration);
                cache.put(dimension, new EffectDetails(id,duration,amplifier));
                loaded.add(dimension);
            }

            FiresEnderExpansion.LOGGER.debug("Loaded {} dimensions from adaptable_dimensions.json.", loaded.size());
        } catch (Exception e) {
            FiresEnderExpansion.LOGGER.error("Failed to parse adaptable_dimensions.json: {}", e.getMessage(), e);
        }

        this.dimensionToEffectsCache = Collections.unmodifiableMap(cache);
        return loaded;
    }

    public EffectDetails getEffectDetailsForDimension(String dimension) {
        if(dimensionToEffectsCache.get(dimension) == null){

        }
        return dimensionToEffectsCache.getOrDefault(dimension, new EffectDetails(null, 200, 0));
    }
}
