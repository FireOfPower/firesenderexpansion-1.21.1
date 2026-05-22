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

    private List<ResourceKey<Level>> dimensions = Collections.emptyList();

    private Map<ResourceKey<Level>,EffectDetails> dimensionToEffectsCache = new HashMap<>();
    private boolean cacheBuilt = false;

    private EffectDimensionMatcher(){}

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pBarrier,
                                          ResourceManager pResourceManager,
                                          ProfilerFiller pPreparationsProfiler,
                                          ProfilerFiller pReloadProfiler,
                                          Executor pBackgroundExecutor,
                                          Executor pGameExecutor) {

        CompletableFuture<List<ResourceKey<Level>>> prepareFuture = CompletableFuture.supplyAsync(() -> loadFromDisk(pResourceManager), pBackgroundExecutor);

        return prepareFuture
                .thenCompose(pBarrier::wait)
                .thenAcceptAsync(loaded -> {
                    this.dimensionToEffectsCache = new HashMap<>();
                    this.cacheBuilt = false;
                    FiresEnderExpansion.LOGGER.debug("Applied {} adaptable dimensions.", loaded.size());
                }, pGameExecutor);
    }

    private List<ResourceKey<Level>> loadFromDisk(ResourceManager resourceManager) {
        List<ResourceKey<Level>> loaded = new ArrayList<>();

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
                ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(dimension));
                loaded.add(key);
            }

            FiresEnderExpansion.LOGGER.debug("Loaded {} dimensions from adaptable_dimensions.json.", loaded.size());
        } catch (Exception e) {
            FiresEnderExpansion.LOGGER.error("Failed to parse adaptable_dimensions.json: {}", e.getMessage(), e);
        }

        return loaded;
    }

    public EffectDetails getEffectDetailsForDimension(ResourceKey<Level> dimension) {
        if (!cacheBuilt) {
            buildCache();
        }
        return dimensionToEffectsCache.getOrDefault(dimension, new EffectDetails(null, 0));
    }

    public List<ResourceKey<Level>> getDimensions() {
        return dimensions;
    }

    public void applyFromNetwork(List<ResourceKey<Level>> received) {
        applyCategories(received);
        FiresEnderExpansion.LOGGER.debug("Received {} effect categories from server.", received.size());
    }

    private void applyCategories(List<ResourceKey<Level>> loaded) {
        this.dimensions = Collections.unmodifiableList(loaded);
        this.dimensionToEffectsCache = new HashMap<>();
        this.cacheBuilt = false;
    }

    private void buildCache() {
        Map<ResourceKey<Level>,EffectDetails> cache = new HashMap<>();

        for (ResourceKey<Level> dimension : dimensions) {
            ResourceLocation key = ResourceLocation.tryParse(dimension.location().getPath());
            if (key == null) {
                FiresEnderExpansion.LOGGER.warn("Invalid tag '{}', skipping.",
                        dimension.location().getPath());
                continue;
            }
            while (key.getPath().contains("/")) {
                var path = key.getPath().split("/");
                key = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path[path.length - 1]);
            }
            key = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "dimension_effects/" + key.getPath());

            try (FileReader reader = new FileReader(key.toDebugFileName(), StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray entries = root.getAsJsonArray("entries");

                if (entries == null) {
                    FiresEnderExpansion.LOGGER.warn("Fire's Ender Expansion adaptable_dimensions.json has no 'entries' array.");
                    return;
                }

                for (JsonElement el : entries) {
                    JsonObject obj = el.getAsJsonObject();
                    String[] id = obj.get("id").getAsString().split(":");
                    int duration = obj.get("duration").getAsInt();
                    cache.put(dimension, new EffectDetails(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.fromNamespaceAndPath(id[0],id[1]))),duration));
                }

                FiresEnderExpansion.LOGGER.debug("Loaded {} dimensions from adaptable_dimensions.json.", dimensions.size());
            } catch (Exception e) {
                FiresEnderExpansion.LOGGER.error("Failed to parse adaptable_dimensions.json: {}", e.getMessage(), e);
            }
        }
        //cache.replaceAll((k, v) -> Collections.unmodifiableList(v));
        this.dimensionToEffectsCache = Collections.unmodifiableMap(cache);
        this.cacheBuilt = true;
    }
}
