package ru.maxthetomas.votvevents.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.config.Config;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.EventResource;
import ru.maxthetomas.votvevents.event.registry.EventRegistries;

import java.util.Random;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevelMixin.class)
public class ServerLevelMixin {

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V",
                    ordinal = 0
            )
    )
    private void onWakeUp(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        // Probably should separate this into a different class
        MinecraftServer server = VotvEvents.getCurrentServer().orElseThrow();
        Config config = VotvEvents.getConfig().orElseThrow();
        Random random = new Random();

        if (random.nextFloat() >= config.getWakeUpEventChance() ||
                !config.isWakeUpEventsEnabled()) {
            return;
        }

        EventResource resource = EventRegistries.WAKE_UP.getRandomEvent();
        EventContext context = new EventContext(server);

        VotvEvents.getEventManager().runEvent(resource, context);
    }
}
