package ru.maxthetomas.votvevents.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.behaviour.Behaviour;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.IBehaviourExecutor;
import ru.maxthetomas.votvevents.networking.S2CSetSnowRender;
import ru.maxthetomas.votvevents.util.VotvEventsVariables;

/**
 * Overrides every biome to start snowing. Use stop() to end.
 *
 * @see ru.maxthetomas.votvevents.mixin.client.WeatherEffectRendererMixin
 * @see ru.maxthetomas.votvevents.mixin.common.BiomeMixin
 * @see ru.maxthetomas.votvevents.mixin.common.SnowAndFreezeFeatureMixin
 */
public class SnowOverrideBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(VotvEvents.MOD_ID, "snow_override");
    public static final MapCodec<SnowOverrideBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(SnowOverrideBehaviour::new));

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setSnow(true);
    }

    @Override
    public void stop() {
        super.stop();
        setSnow(false);
        setDone(true);
    }

    private void setSnow(boolean value) {
        VotvEventsVariables.ShouldSnow = value;

        // Todo: if player rejoins server - this won't work.
        // needs a better sync solution.
        NetworkManager.sendToPlayers(VotvEvents.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetSnowRender(value));
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
