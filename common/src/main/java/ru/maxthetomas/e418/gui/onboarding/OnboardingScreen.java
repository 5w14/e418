package ru.maxthetomas.e418.gui.onboarding;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.maxthetomas.e418.gui.widgets.WidgetWrapper;

public class OnboardingScreen extends Screen {
    private final Screen parent;

    public OnboardingScreen(Screen parent) {
        super(Component.translatable("e418.screen.onboarding.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(
                WidgetWrapper.create(new StringWidget(Component.literal("e418 configuration"), font))
                        .y(20).centerX(this.width).apply(StringWidget::alignLeft).widget()
        );

        var explanation = addRenderableWidget(
                WidgetWrapper.create(new MultiLineTextWidget(
                                Component.literal("Long text ".repeat(70)), font))
                        .apply(s -> s.setMaxWidth(300))
                        .y(50).w(300).centerX(this.width).widget()
        );

        addRenderableWidget(
                WidgetWrapper.create(new Button.Builder(Component.literal("Button"), x -> {
                        }).build())
                        .centerX(this.width).y(explanation.getHeight() + 65).widget()
        );

        addRenderableWidget(
                WidgetWrapper.create(new Button.Builder(Component.literal("Skip"),
                                x -> this.minecraft.setScreen(parent)).build())
                        .bottom(this.height, 20).x(this.width / 2 - 104).w(100).widget()
        );

        addRenderableWidget(
                WidgetWrapper.create(new Button.Builder(Component.literal("Continue"), x -> {
                        }).build())
                        .bottom(this.height, 20).x(this.width / 2 + 4).w(100).widget()
        );
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
