package ru.maxthetomas.e418;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import ru.maxthetomas.e418.gui.onboarding.OnboardingScreen;
import ru.maxthetomas.e418.util.E418ClientVariables;

public class E418Client {
    public static void init() {
        E418ClientVariables.init();
        ClientPlayerEvent.CLIENT_PLAYER_QUIT
                .register(evt -> E418ClientVariables.init());

        ClientGuiEvent.SET_SCREEN.register(E418Client::setScreen);
        if (System.getProperty("e418.forceOnboarding", "false").equals("true"))
            waitForOnboarding();
    }

    private static CompoundEventResult<Screen> setScreen(Screen screen) {
        if (!_waitingOnboarding || !(screen instanceof TitleScreen ts))
            return CompoundEventResult.pass();

        _waitingOnboarding = false;
        ClientGuiEvent.SET_SCREEN.unregister(E418Client::setScreen);
        return CompoundEventResult.interruptTrue(new OnboardingScreen(screen));
    }

    static boolean _waitingOnboarding;

    public static void waitForOnboarding() {
        _waitingOnboarding = true;
    }
}
