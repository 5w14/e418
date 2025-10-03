package ru.maxthetomas.e418.codecs.numberprovider.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.numberprovider.NumberProvider;
import ru.maxthetomas.e418.codecs.numberprovider.NumberRequester;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.EventContext;

public class ConfigNumberProvider implements NumberProvider {
    public static ResourceLocation ID = E418.resLoc("config");
    public static MapCodec<ConfigNumberProvider> CODEC = RecordCodecBuilder.<ConfigNumberProvider>mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("field").forGetter(v -> v.configField)
    ).apply(instance, ConfigNumberProvider::new));

    private final String configField;

    public ConfigNumberProvider(String configField) {
        this.configField = configField;
    }

    @Override
    public ResourceLocation getType() {
        return ID;
    }

    @Override
    public Number get(EventContext context, NumberRequester requester) {
        var opValue = Config.getConfigValue(this.configField);
        if (opValue.isEmpty()) { 
            throw new IllegalArgumentException("Config field not found!");
        }

        var value = opValue.get().get();


        if (value instanceof Number num)
            return num;

        throw new IllegalArgumentException("Provided config value is not a number");
    }
}
