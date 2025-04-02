package ru.maxthetomas.votvevents.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.config.Config;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.EventResource;
import ru.maxthetomas.votvevents.event.registry.EventRegistries;
import ru.maxthetomas.votvevents.util.Location;

import java.util.Random;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "startSleepInBed", at = @At("HEAD"))
    private void onStartSleepInBed(BlockPos arg3, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cl) {
        // TODO: Trigger events when trying to sleep
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void onStopSleepInBed(boolean bl1, boolean bl2, CallbackInfo cl) {
        Player player = (Player) (Object) this;

        // Making sure that we only do it server-side
        if (player.getCommandSenderWorld().isClientSide()) {
            return;
        }


        // Probably should separate this into a different class
        MinecraftServer server = VotvEvents.getCurrentServer().orElseThrow();
        Config config = VotvEvents.getConfig().orElseThrow();
        Random random = new Random();

        if (random.nextFloat() >= config.getWakeUpEventChance() || !config.isWakeUpEventsEnabled()) {
            return;
        }

        server.overworld().setDayTime(18000);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            serverPlayer.displayClientMessage(Component.translatable("votvevents.events.sleep.insomnia"), true);
        }

        EventResource resource = EventRegistries.WAKE_UP.getRandomEvent();
        EventContext context = new EventContext(server)
                .withPlayer(player)
                .withLocation(Location.fromPlayer((ServerPlayer) player));

        VotvEvents.getEventManager().runEvent(resource, context);
    }
}
