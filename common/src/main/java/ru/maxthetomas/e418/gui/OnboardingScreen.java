package ru.maxthetomas.e418.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.config.ConfigLoader;
import ru.maxthetomas.e418.gui.config.ConfigurationScreen;
import ru.maxthetomas.e418.gui.widgets.WidgetWrapper;

import java.util.concurrent.CompletableFuture;

public class OnboardingScreen extends Screen {
    private static final int MODE_COUNT = 4;
    private final Screen parent;

    public OnboardingScreen(Screen parent) {
        super(Component.translatable("e418.screen.onboarding.title"));
        this.parent = parent;
    }

    private int pickedMode = 1;

    @Override
    protected void init() {
        addRenderableWidget(WidgetWrapper.string("e418.screen.onboarding.title").y(20).centerX(this.width).widget());
        addRenderableWidget(WidgetWrapper.string("e418.screen.onboarding.description").y(50).w(300).centerX(this.width).widget());

        addRenderableWidget(
                WidgetWrapper.button(Component.translatable("e418.screen.onboarding.mode",
                                Component.translatable("e418.screen.onboarding.mode." + this.pickedMode)), this::advanceOption)
                        .centerX(this.width).y(90).widget()
        );

        addRenderableWidget(WidgetWrapper.string("e418.screen.onboarding.mode." + this.pickedMode + ".description").y(125)
                .w(300).centerX(this.width).widget());

        addRenderableWidget(
                WidgetWrapper.button(Component.translatable("e418.screen.onboarding.continue"), this::finishSetup)
                        .bottom(this.height, 20).centerX(this.width).widget()
        );

        addRenderableWidget(
                WidgetWrapper.button(Component.translatable("e418.screen.onboarding.configure"), this::advancedConfig)
                        .w(100).bottom(this.height, 20).right(this.width, 20).widget()
        );
    }

    private void finishSetup() {
        saveConfig();
        this.minecraft.setScreen(this.parent);
    }

    private void saveConfig() {
        Config.baseIntrusiveness.set((float) this.pickedMode);
        CompletableFuture.runAsync(ConfigLoader::saveConfig);
    }

    private void advancedConfig() {
        saveConfig();
        this.minecraft.setScreen(new ConfigurationScreen(this.parent));
    }

    private void advanceOption() {
        var adv = hasShiftDown() ? -1 : 1;
        this.pickedMode = (this.pickedMode + adv) % MODE_COUNT;
        if (this.pickedMode < 0)
            this.pickedMode = MODE_COUNT - 1;
        rebuildWidgets();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
