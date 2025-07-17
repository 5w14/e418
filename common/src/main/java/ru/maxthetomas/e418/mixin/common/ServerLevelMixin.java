package ru.maxthetomas.e418.mixin.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.ticks.LevelTicks;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.event.EventContext;
import ru.maxthetomas.e418.event.cause.impl.WakeUpEventCause;
import ru.maxthetomas.e418.event.registry.EventRegistries;
import ru.maxthetomas.e418.util.E418Variables;

import java.util.List;
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
    @Final
    private LevelTicks<Fluid> fluidTicks;

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

    @Shadow
    public abstract RandomSource getRandomSequence(ResourceLocation arg);

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

            if (E418Variables.DisableNightSkip)
                return;

            var cancelTimeSkip = false;

            var random = this.getRandomSequence(E418.resLoc("event_engine/random_player"));

            if (random.nextFloat() > 0.5) {
                var cause = new WakeUpEventCause();
                var ctx = new EventContext(this.getServer())
                        .withCause(cause);
                var e = EventRegistries.getQueueableEventsWithTag("action.minecraft.wake_up", ctx).getRandomElement(random);

                if (e != null) {
                    E418.getEventManager().queueEvent(e, ctx);
                }

                cancelTimeSkip = cause.isTimeSkipCancelled();
            }

            if (!cancelTimeSkip && this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
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
        if (Config.isEmptyWorld(dim.location()))
            cir.cancel();
    }

}