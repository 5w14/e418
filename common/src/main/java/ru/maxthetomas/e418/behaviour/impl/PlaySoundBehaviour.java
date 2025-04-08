package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;

import java.util.Optional;

public class PlaySoundBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "play_sound");

    public static final MapCodec<PlaySoundBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("sound").forGetter(PlaySoundBehaviour::getSoundEventId),
                    Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(PlaySoundBehaviour::getVolume),
                    Codec.FLOAT.optionalFieldOf("range", 16.0F).forGetter(PlaySoundBehaviour::getRange),
                    Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(PlaySoundBehaviour::getPitch),
                    Codec.STRING.xmap((string) -> SoundSource.valueOf(string.toUpperCase()), SoundSource::getName)
                            .optionalFieldOf("source", SoundSource.AMBIENT).forGetter(PlaySoundBehaviour::getSoundSource)
            ).apply(instance, PlaySoundBehaviour::new)
    );

    private final ResourceLocation soundEventId;
    private final SoundSource source;
    private final float volume;
    private final float range;
    private final float pitch;

    public PlaySoundBehaviour(ResourceLocation soundEventId, float volume, float range, float pitch, SoundSource source) {
        this.soundEventId = soundEventId;
        this.source = source;
        this.volume = volume;
        this.range = range;
        this.pitch = pitch;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        var soundEvent =
                new SoundEvent(getSoundEventId(), Optional.of(getRange()));

        if (context.getPlayer() == null) {
            if (context.getLocation() != null) {
                context.getLocation().getLevel().playLocalSound(
                        context.getLocation().getBlockPosition(),
                        soundEvent, getSoundSource(), getVolume(), getPitch(), false
                );
            }

            return;
        }

        context.getPlayer().playNotifySound(
                soundEvent,
                getSoundSource(), getVolume(), getPitch()
        );
    }

    @Override
    public boolean canRun(EventContext context) {
        return context.getPlayer() != null || context.getLocation() != null;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    public ResourceLocation getSoundEventId() {
        return soundEventId;
    }

    public SoundSource getSoundSource() {
        return source;
    }

    public float getVolume() {
        return volume;
    }

    public float getRange() {
        return range;
    }

    public float getPitch() {
        return pitch;
    }
}
