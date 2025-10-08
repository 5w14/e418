package ru.maxthetomas.e418.gui.onboarding;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.maxthetomas.e418.gui.widgets.WidgetWrapper;

public class OnboardingScreen extends Screen {
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
                WidgetWrapper.create(new Button.Builder(Component.translatable("e418.screen.onboarding.mode",
                                Component.translatable("e418.screen.onboarding.mode." + this.pickedMode)), x -> {
                            this.pickedMode = (this.pickedMode + 1) % 4;
                            rebuildWidgets();
                        }).build())
                        .centerX(this.width).y(90).widget()
        );

        addRenderableWidget(WidgetWrapper.string("e418.screen.onboarding.mode." + this.pickedMode + ".description").y(125)
                .w(300).centerX(this.width).widget());

        addRenderableWidget(
                WidgetWrapper.create(new Button.Builder(Component.translatable("e418.screen.onboarding.continue"), x -> {
                            finishSetup();
                        }).build())
                        .bottom(this.height, 20).centerX(this.width).widget()
        );
    }

    private void finishSetup() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
