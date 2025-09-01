package ru.maxthetomas.e418.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.neoforge.player.NeoForgePlayerDataManager;

@Mod(E418.MOD_ID)
public final class E418NeoForge {
    public E418NeoForge(IEventBus modBus) {
        // Run our common setup.
        E418.init();

        // Mod loader specific actions
        AttachmentTypes.ATTACHMENT_TYPES.register(modBus);
        E418.PlayerDataManager = new NeoForgePlayerDataManager();
    }
}
