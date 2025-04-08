package ru.maxthetomas.e418.config;

import com.mojang.serialization.Dynamic;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.registries.RandomSourceConfig;
import ru.maxthetomas.e418.config.registries.SourceConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceConfigs {
    private static final HashMap<ResourceLocation, SourceConfig> REGISTRY = new HashMap<>();

    public static RandomSourceConfig RANDOM_EVENT = create(new RandomSourceConfig(true,
            20 * 60 * 30, 20 * 60 * 90)); // 30 to 90 minutes
    public static SourceConfig WAKE_UP = create("wake_up", 0.05f);

    public static <T extends SourceConfig> T create(T instance) {
        REGISTRY.put(instance.getId(), instance);
        return instance;
    }

    private static SourceConfig create(String id, float chance) {
        return create(ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, id), chance);
    }

    public static SourceConfig create(ResourceLocation id, float chance) {
        var c = new SourceConfig(true, chance) {
            @Override
            public ResourceLocation getId() {
                return id;
            }
        };
        REGISTRY.put(id, c);
        return c;
    }

    public static void setValues(ResourceLocation id, Dynamic<?> values) {
        if (!REGISTRY.containsKey(id))
            return;

        if (values == null)
            return;

        REGISTRY.get(id).setValues(values);
    }

    public static Dynamic<?> storeValues(Dynamic<?> dynamic) {
        for (Map.Entry<ResourceLocation, SourceConfig> entry : REGISTRY.entrySet()) {
            ResourceLocation a = entry.getKey();
            SourceConfig b = entry.getValue();
            dynamic = dynamic.set(a.toString(),
                    b.storeValues(dynamic.emptyMap()));
        }
        return dynamic;
    }

    public static List<SourceConfig> getAllRegistries() {
        return REGISTRY.values().stream().toList();
    }
}
