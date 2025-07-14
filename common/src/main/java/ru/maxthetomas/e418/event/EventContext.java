package ru.maxthetomas.e418.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.event.cause.EventCauses;
import ru.maxthetomas.e418.event.cause.IEventCause;
import ru.maxthetomas.e418.util.Location;

import java.util.Optional;
import java.util.UUID;

public class EventContext {
    public static final MapCodec<EventContext> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.BOOL.lenientOptionalFieldOf("forced", false).forGetter(v -> v.forced),
                    Location.CODEC.codec().optionalFieldOf("location").forGetter(v -> Optional.ofNullable(v.location)),
                    UUIDUtil.CODEC.optionalFieldOf("player").forGetter(v -> Optional.ofNullable(v.player)),
                    EventCauses.DISPATCH_CODEC.fieldOf("cause").forGetter(v -> v.cause)
            ).apply(instance, EventContext::constructByCodec));


    private final MinecraftServer server;
    @Nullable
    private ActiveEvent sourceEvent;
    private boolean forced = false;
    @Nullable
    private Location location;
    @Nullable
    private UUID player;
    @Nullable
    private IEventCause cause;

    // TODO add more fields here

    public EventContext(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ActiveEvent getSourceEvent() {
        return sourceEvent;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return server.getPlayerList().getPlayer(player);
    }

    public boolean hasPlayer() {
        return player != null;
    }

    // If the event is restored, and the player with this specific UUID is not online.
    public boolean shouldAwaitPlayer() {
        return hasPlayer() && getPlayer() == null;
    }

    public boolean isForced() {
        return forced;
    }

    public IEventCause getCause() {
        return cause;
    }

    public EventContext withSourceEvent(ActiveEvent sourceEvent) {
        this.sourceEvent = sourceEvent;
        return this;
    }

    public EventContext withPlayer(ServerPlayer player) {
        this.player = player.getUUID();
        return this;
    }

    public EventContext withForced(boolean forced) {
        this.forced = forced;
        return this;
    }

    public EventContext withLocation(Location location) {
        this.location = location;
        return this;
    }

    public EventContext withCause(IEventCause cause) {
        this.cause = cause;
        return this;
    }

    @Override
    public EventContext clone() {
        var newContext = new EventContext(server);

        newContext.player = player;
        newContext.sourceEvent = sourceEvent;
        newContext.forced = forced;
        newContext.cause = cause;
        if (location != null) {
            newContext.location = new Location(location.level(),
                    new Vec3(location.position().x, location.position().y, location.position().z));
        }

        // TODO add more fields here

        return newContext;
    }

    private static EventContext constructByCodec(boolean forced, Optional<Location> location, Optional<UUID> player, IEventCause cause) {
        var context = new EventContext(E418.getCurrentServer().get());
        context.cause = cause;
        context.forced = forced;
        context.location = location.orElse(null);
        context.player = player.orElse(null);
        return context;
    }
}
