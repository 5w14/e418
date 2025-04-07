package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
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
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "snow_override");
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
        E418Variables.ShouldSnow = value;

        // Todo: if player rejoins server - this won't work.
        // needs a better sync solution.
        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetSnowRender(value));
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
