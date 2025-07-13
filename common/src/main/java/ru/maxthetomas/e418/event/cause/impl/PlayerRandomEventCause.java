package ru.maxthetomas.e418.event.cause.impl;

import ru.maxthetomas.e418.event.cause.IEventCause;

import java.util.UUID;

public class PlayerRandomEventCause implements IEventCause {
    private boolean isGroupEffectCancelled = false;

    public final UUID playerUuid;

    public PlayerRandomEventCause(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public void cancelGroupEffect(){
        isGroupEffectCancelled = true;
    }

    public boolean isGroupEffectCancelled() {
        return isGroupEffectCancelled;
    }
}
