package ru.maxthetomas.e418.config.registries;

import com.mojang.serialization.Dynamic;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;

public class RandomSourceConfig extends SourceConfig {
    private int minTime;
    private int maxTime;

    public RandomSourceConfig(boolean enabled, int minTime, int maxTime) {
        super(enabled, 0.9F);
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public RandomSourceConfig() {
        super(true, 0.9F);
        this.minTime = 20 * 60 * 30; // 30 minutes
        this.maxTime = 20 * 60 * 90; // 1.5 hours
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public void setValues(Dynamic<?> data) {
        super.setValues(data);
        data.get("min_time").asNumber().ifSuccess(n -> this.minTime = n.intValue());
        data.get("max_time").asNumber().ifSuccess(n -> this.maxTime = n.intValue());
    }

    @Override
    public Dynamic<?> storeValues(Dynamic<?> data) {
        return super.storeValues(data)
                .set("min_time", data.createInt(minTime))
                .set("max_time", data.createInt(maxTime));
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "random_event");
    }
}
