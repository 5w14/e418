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
import ru.maxthetomas.e418.networking.S2CSetMoon;

/**
 * Changes the moon's texture.
 * <ul>
 *   <li><code>texture</code> – The new texture to use.</li>
 * </ul>
 */
public class SetMoonTextureBehaviour extends Behaviour {
    public static final ResourceLocation ID = E418.resLoc("set_moon_texture");
    public static final MapCodec<SetMoonTextureBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SetMoonTextureBehaviour::getTextureResource)
    ).apply(instance, SetMoonTextureBehaviour::new));
    public static final MapCodec<SetMoonTextureBehaviour> STATE_CODEC = CODEC;

    private ResourceLocation textureResource;

    public SetMoonTextureBehaviour(ResourceLocation textureResource) {
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
            NetworkManager.sendToPlayers(E418.allPlayers(), new S2CSetMoon(textureResource));
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

        NetworkManager.sendToPlayers(E418.allPlayers(), new S2CSetMoon(textureResource));
    }

    @Override
    public void stop() {
        NetworkManager.sendToPlayers(E418.allPlayers(), new S2CSetMoon(ResourceLocation.withDefaultNamespace("empty")));
        setDone(true);
    }

    public ResourceLocation getTextureResource() {
        return textureResource;
    }

    @Override
    public void dispose() {
        unregister();
        super.dispose();
    }
}
