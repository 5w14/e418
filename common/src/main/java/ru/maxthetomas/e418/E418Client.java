package ru.maxthetomas.e418;

import dev.architectury.event.events.client.ClientPlayerEvent;
import ru.maxthetomas.e418.util.E418ClientVariables;

public class E418Client {
    public static void init() {
        E418ClientVariables.init();

        ClientPlayerEvent.CLIENT_PLAYER_QUIT
                .register(evt -> E418ClientVariables.init());
    }
}
