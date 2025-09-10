package ru.maxthetomas.e418.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.E418Client;

@Mod(value = E418.MOD_ID, dist = Dist.CLIENT)
public class E418NeoForgeClient {
    public E418NeoForgeClient(IEventBus modBus) {
        E418Client.init();
    }
}
