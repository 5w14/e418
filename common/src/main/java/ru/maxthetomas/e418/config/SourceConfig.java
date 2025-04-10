package ru.maxthetomas.e418.config;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.Mth;

public class SourceConfig {
    private boolean enabled;
    private float chance;

    public SourceConfig(boolean enabled, float chance) {
        this.enabled = enabled;
        this.chance = chance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = Mth.clamp(chance, 0, 1);
    }

    public void setValues(Dynamic<?> data) {
        data.get("enabled").asBoolean().ifSuccess(this::setEnabled);
        data.get("chance").asNumber().ifSuccess(n -> setChance(n.floatValue()));
    }

    public Dynamic<?> storeValues(Dynamic<?> data) {
        return data.set("enabled", data.createBoolean(enabled))
                .set("chance", data.createFloat(getChance()));
    }
}

