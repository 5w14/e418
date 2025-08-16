package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CSetSun;

/**
 * Changes the sun's texture.
 * <ul>
 *   <li><code>texture</code> - New texture to use.</li>
 * </ul>
 */
public class SetSunTextureBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "set_sun_texture");
    public static final MapCodec<SetSunTextureBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SetSunTextureBehaviour::getTextureResource)
    ).apply(instance, SetSunTextureBehaviour::new));
    public static final MapCodec<SetSunTextureBehaviour> STATE_CODEC = CODEC;

    ResourceLocation textureResource;

    public SetSunTextureBehaviour(ResourceLocation textureResource) {
        this.textureResource = textureResource;
        register();
    }

    void register() {
        PlayerEvent.PLAYER_JOIN.register(this::playerJoin);
    }

    void unregister() {
        PlayerEvent.PLAYER_JOIN.unregister(this::playerJoin);
    }

    void playerJoin(ServerPlayer player) {
        player.getServer().execute(() -> {
            NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                    new S2CSetSun(textureResource));
        });
    }

    @Override
    public ResourceLocation getTypeId() {
        return ID;
    }

    @Override
    public void execute(EventContext context, IBehaviourExecutor executor) {
        super.execute(context, executor);
        setDone(true);

        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetSun(textureResource));
    }

    @Override
    public void stop() {
        NetworkManager.sendToPlayers(E418.getCurrentServer().get().getPlayerList().getPlayers(),
                new S2CSetSun(ResourceLocation.withDefaultNamespace("empty")));
        setDone(true);
    }

    @Override
    public void dispose() {
        unregister();
        super.dispose();
    }

    public ResourceLocation getTextureResource() {
        return textureResource;
    }
}
