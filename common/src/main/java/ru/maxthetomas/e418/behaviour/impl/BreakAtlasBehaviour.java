package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CSetBreakAtlas;

/// Breaks game's texture atlas to make textures look glitchy
public class BreakAtlasBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "break_atlas");
    public static final MapCodec<BreakAtlasBehaviour> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(BreakAtlasBehaviour::new));

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setBreakAtlas(context.getPlayer(), true);
    }

    @Override
    public void dispose() {
        super.dispose();
        setBreakAtlas(null, false);
    }

    private void setBreakAtlas(ServerPlayer player, boolean value) {
        // Todo: if player rejoins server - this won't work.
        // needs a better sync solution.

        if (player == null) {
            NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                    new S2CSetBreakAtlas(value));
        } else {
            NetworkManager.sendToPlayer(player, new S2CSetBreakAtlas(value));
        }
    }

    @Override
    public boolean canRun(EventContext context) {
        return !context.shouldAwaitPlayer();
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }
}
