package ru.maxthetomas.votvevents.neoforge;

import ru.maxthetomas.votvevents.VotvEvents;
import net.neoforged.fml.common.Mod;

@Mod(VotvEvents.MOD_ID)
public final class VotvEventsNeoForge {
    public VotvEventsNeoForge() {
        // Run our common setup.
        VotvEvents.init();
    }
}
