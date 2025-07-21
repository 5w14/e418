package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CSetShader;

import java.util.UUID;

/// Gives player a custom shader
///
/// <li> <code>shader</code> - Shader to use
public class SetShaderBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "set_shader");
    public static final MapCodec<SetShaderBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("shader", ResourceLocation.withDefaultNamespace("empty"))
                    .forGetter(SetShaderBehaviour::getShaderId)
    ).apply(instance, SetShaderBehaviour::new));

    public static final MapCodec<SetShaderBehaviour> STATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("shader", ResourceLocation.withDefaultNamespace("empty"))
                    .forGetter(SetShaderBehaviour::getShaderId),
            UUIDUtil.CODEC.lenientOptionalFieldOf("player_uuid", null).forGetter(v -> v.playerUUID)
    ).apply(instance, SetShaderBehaviour::new));

    private ResourceLocation shaderId;
    private UUID playerUUID = null;

    public SetShaderBehaviour(ResourceLocation shaderId) {
        this.shaderId = shaderId;
        PlayerEvent.PLAYER_JOIN.register(this::playerJoined);
    }

    private SetShaderBehaviour(ResourceLocation shaderId, @Nullable UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.shaderId = shaderId;
        PlayerEvent.PLAYER_JOIN.register(this::playerJoined);
    }

    @Override
    public void stop() {
        PlayerEvent.PLAYER_JOIN.unregister(this::playerJoined);
        super.stop();
    }

    void playerJoined(ServerPlayer player) {
        if (isExecuted() && !isDone() && (this.playerUUID == null
                || player.getUUID().equals(this.playerUUID)))
            player.getServer().execute(() -> {
                NetworkManager.sendToPlayer(player, new S2CSetShader(shaderId));
            });
    }

    public ResourceLocation getShaderId() {
        return shaderId;
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);

        var player = context.getPlayer();
        if (context.hasPlayer() && player != null) {
            NetworkManager.sendToPlayer(player, new S2CSetShader(shaderId));
            this.playerUUID = player.getUUID();
        } else {
            NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                    new S2CSetShader(shaderId));
        }
    }

    @Override
    public void dispose() {
        if (this.playerUUID != null) {
            var player = E418.getCurrentServer().get().getPlayerList().getPlayer(this.playerUUID);
            if (player == null) return;
            NetworkManager.sendToPlayer(player,
                    new S2CSetShader(S2CSetShader.EMPTY_SHADER));
        } else {
            NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                    new S2CSetShader(S2CSetShader.EMPTY_SHADER));
        }

        super.dispose();
    }
}
