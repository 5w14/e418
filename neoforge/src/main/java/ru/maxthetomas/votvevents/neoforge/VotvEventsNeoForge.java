package ru.maxthetomas.votvevents.neoforge;

import net.neoforged.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixins;
import ru.maxthetomas.votvevents.VotvEvents;

@Mod(VotvEvents.MOD_ID)
public final class VotvEventsNeoForge {
    public VotvEventsNeoForge() {
        Mixins.addConfiguration(VotvEvents.MOD_ID + ".mixins.json");

        // Run our common setup.
        VotvEvents.init();
    }
}
