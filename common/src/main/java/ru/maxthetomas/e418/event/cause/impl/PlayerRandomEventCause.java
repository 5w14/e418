package ru.maxthetomas.e418.event.cause.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.IEventCause;

import java.util.UUID;

public class PlayerRandomEventCause implements IEventCause {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "player_random");
    public static final MapCodec<PlayerRandomEventCause> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("player").forGetter(v -> v.playerUuid),
            Codec.BOOL.fieldOf("is_group_effect_cancelled").forGetter(v -> v.isGroupEffectCancelled)
    ).apply(instance, PlayerRandomEventCause::fromCodec));

    public final UUID playerUuid;
    private boolean isGroupEffectCancelled = false;

    public PlayerRandomEventCause(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public void cancelGroupEffect() {
        isGroupEffectCancelled = true;
    }

    public boolean isGroupEffectCancelled() {
        return isGroupEffectCancelled;
    }

    private static PlayerRandomEventCause fromCodec(UUID player, boolean isEffectCancelled) {
        var cause = new PlayerRandomEventCause(player);
        cause.isGroupEffectCancelled = isEffectCancelled;
        return cause;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }
}
