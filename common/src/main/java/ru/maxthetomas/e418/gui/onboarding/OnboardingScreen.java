package ru.maxthetomas.e418.gui.onboarding;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
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
                WidgetWrapper.create(new StringWidget(Component.literal("Onboarding"), font))
                        .y(20).centerX(this.width).apply(StringWidget::alignLeft).widget()
        );

        addRenderableWidget(
                WidgetWrapper.create(new Button.Builder(Component.literal("Back"),
                                x -> this.minecraft.setScreen(parent)).build())
                        .bottom(this.height, 20).centerX(this.width).widget()
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

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == GLFW.GLFW_KEY_R) {
            clearWidgets();
            init();
            return true;
        }

        return super.keyPressed(i, j, k);
    }
}
