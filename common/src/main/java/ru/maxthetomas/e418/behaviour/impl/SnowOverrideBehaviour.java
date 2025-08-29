package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.mixin.client.WeatherEffectRendererMixin;
import ru.maxthetomas.e418.mixin.common.BiomeMixin;
import ru.maxthetomas.e418.mixin.common.SnowAndFreezeFeatureMixin;
import ru.maxthetomas.e418.networking.S2CSetSnowRender;
import ru.maxthetomas.e418.util.E418Variables;

/**
 * Overrides every biome to start snowing. Use stop() to end.
 *
 * @see WeatherEffectRendererMixin
 * @see BiomeMixin
 * @see SnowAndFreezeFeatureMixin
 */
public class SnowOverrideBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("snow_override");
    public static final MapCodec<SnowOverrideBehaviour> CODEC = MapCodec.unit(SnowOverrideBehaviour::new);
    public static final MapCodec<SnowOverrideBehaviour> STATE_CODEC = MapCodec.unit(SnowOverrideBehaviour::new);

    public SnowOverrideBehaviour() {
        PlayerEvent.PLAYER_JOIN.register(this::playerJoin);
    }

    void playerJoin(ServerPlayer player) {
        if (E418Variables.ShouldSnow)
            NetworkManager.sendToPlayer(player, new S2CSetSnowRender(true));
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setSnow(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        PlayerEvent.PLAYER_JOIN.unregister(this::playerJoin);
        setSnow(false);
    }

    private void setSnow(boolean value) {
        E418Variables.ShouldSnow = value;

        // Todo: if player rejoins server - this won't work.
        // needs a better sync solution.
        NetworkManager.sendToPlayers(E418.allPlayers(),
                new S2CSetSnowRender(value));
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
