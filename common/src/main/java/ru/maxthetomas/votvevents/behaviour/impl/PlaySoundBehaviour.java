package ru.maxthetomas.votvevents.behaviour.impl;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import ru.maxthetomas.votvevents.behaviour.IBehaviour;
import ru.maxthetomas.votvevents.event.EventContext;

import java.util.Optional;

public class PlaySoundBehaviour implements IBehaviour {
    private ResourceLocation soundResourceLocation;
    private float volume = 1f;
    private float pitch = 1f;

    public PlaySoundBehaviour(JsonElement properties) {
        var soundResourcePath = properties.getAsJsonObject().get("sound").getAsString();
        soundResourceLocation = ResourceLocation.parse(soundResourcePath);

        if (properties.getAsJsonObject().has("volume")) {
            volume = properties.getAsJsonObject().get("volume").getAsFloat();
        }

        if (properties.getAsJsonObject().has("pitch")) {
            pitch = properties.getAsJsonObject().get("pitch").getAsFloat();
        }

    }

    @Override
    public void execute(EventContext context) {
        context.getPlayer().playNotifySound(new SoundEvent(soundResourceLocation, Optional.empty()), SoundSource.MASTER, volume, pitch);
    }
}
