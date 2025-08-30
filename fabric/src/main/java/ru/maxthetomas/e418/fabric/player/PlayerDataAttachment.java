package ru.maxthetomas.e418.fabric.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ru.maxthetomas.e418.player.PlayerData;

public record PlayerDataAttachment(PlayerData playerData) {
    public static Codec<PlayerDataAttachment> CODEC = RecordCodecBuilder.<PlayerDataAttachment>create(instance -> instance.group(
            PlayerData.CODEC.fieldOf("player_data").forGetter(v -> v.playerData)
    ).apply(instance, PlayerDataAttachment::new));

    public static PlayerDataAttachment DEFAULT = new PlayerDataAttachment(new PlayerData());
}
