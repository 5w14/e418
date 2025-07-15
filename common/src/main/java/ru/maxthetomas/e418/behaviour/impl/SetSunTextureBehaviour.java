package ru.maxthetomas.e418.behaviour.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.behaviour.Behaviour;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.IBehaviourExecutor;
import ru.maxthetomas.e418.networking.S2CSetSun;

public class SetSunTextureBehaviour extends Behaviour {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(E418.MOD_ID, "set_sun_texture");
    public static final MapCodec<SetSunTextureBehaviour> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(SetSunTextureBehaviour::getTextureResource)
    ).apply(instance, SetSunTextureBehaviour::new));

    ResourceLocation textureResource;

    public SetSunTextureBehaviour(ResourceLocation textureResource) {
        this.textureResource = textureResource;
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

    public ResourceLocation getTextureResource() {
        return textureResource;
    }
}
