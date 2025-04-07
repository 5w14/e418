package ru.maxthetomas.votvevents.mixin.common;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.maxthetomas.votvevents.VotvEvents;
import ru.maxthetomas.votvevents.event.EventContext;
import ru.maxthetomas.votvevents.event.registry.EventRegistries;
import ru.maxthetomas.votvevents.util.VotvEventsVariables;

import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow
    @Final
    private SleepStatus sleepStatus;
    @Shadow
    @Final
    private ServerLevelData serverLevelData;

    @Shadow
    public abstract GameRules getGameRules();

    @Shadow
    public abstract List<ServerPlayer> players();

    @Shadow
    public abstract void setDayTime(long l);

    @Shadow
    protected abstract void wakeUpAllPlayers();

    @Shadow
    public abstract void resetWeatherCycle();

    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    @ModifyVariable(at = @At("STORE"), method = "tick", ordinal = 0)
    public int modifySleepingPercentage(int input) {
        return 101; // Disable sleep through night logic.
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/SleepStatus;areEnoughSleeping(I)Z"
            ),
            method = "tick"
    )
    public void customSleepLogic(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        // Code from vanilla - do not remove!
        var sleepingPercentage = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        if (this.sleepStatus.areEnoughSleeping(sleepingPercentage)
                && this.sleepStatus.areEnoughDeepSleeping(sleepingPercentage, this.players())) {

            if (VotvEventsVariables.DisableNightSkip)
                return;

            // todo: Probably should separate this into a different class
            var server = getServer();
            var config = VotvEvents.getConfig().orElseThrow();
            var random = new Random(); // todo: make this use world random
            if (config.isWakeUpEventsEnabled() &&
                    random.nextFloat() <= config.getWakeUpEventChance()) {
                var resource = EventRegistries.WAKE_UP.getRandomEvent();
                var context = new EventContext(server);
                VotvEvents.getEventManager().runEvent(resource, context);
            }

            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                var l = this.serverLevelData.getDayTime() + 24000L;
                this.setDayTime(l - l % 24000L);
            }

            this.wakeUpAllPlayers();

            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.serverLevelData.isRaining()) {
                this.resetWeatherCycle();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "addEntity", cancellable = true)
    public void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof LivingEntity)) return;

        var dim = ((ServerLevel) (Object) this).dimension();
        if (dim.location().equals(VotvEvents.NO_ENTITY_AND_STRUCTURE_DIMENSION_ID))
            cir.cancel();
    }

}