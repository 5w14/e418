package ru.maxthetomas.e418.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ru.maxthetomas.e418.event.cause.IEventCause;
import ru.maxthetomas.e418.util.Location;

public class EventContext {
    private final MinecraftServer server;
    private ActiveEvent sourceEvent;
    private boolean forced = false;
    @Nullable
    private Location location;
    @Nullable
    private ServerPlayer player;
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
        return player;
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
        this.player = player;
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
            newContext.location = new Location(location.getLevel(),
                    new Vec3(location.getPosition().x, location.getPosition().y, location.getPosition().z));
        }

        // TODO add more fields here

        return newContext;
    }
}
