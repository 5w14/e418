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
import ru.maxthetomas.e418.networking.S2CSetMetaParanoia;

/**
 * Prevents the user from being able to leave.
 */
public class MetaParanoiaBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "meta_paranoia");
    public static final MapCodec<MetaParanoiaBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(MetaParanoiaBehaviour::new));

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetMetaParanoia(true));

        // TODO: remove game's ability to save while this event is happening
    }

    @Override
    public void stop() {
        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetMetaParanoia(false));
        setDone(true);
    }
}
