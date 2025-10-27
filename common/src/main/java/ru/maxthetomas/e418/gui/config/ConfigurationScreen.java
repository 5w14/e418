package ru.maxthetomas.e418.gui.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.maxthetomas.e418.config.Config;
import ru.maxthetomas.e418.gui.widgets.WidgetWrapper;

public class ConfigurationScreen extends Screen {
    private final Screen parentScreen;

    public ConfigurationScreen(Screen parentScreen) {
        super(Component.translatable("e418.screen.config.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        int a = 5;
        for (Config.Value<?> config : Config.getConfigurationValues()) {
            addRenderableWidget(
                    WidgetWrapper.string("e418.config." + config.getSerializedName() + ".name")
                            .x(10).y(a += 10).w(400).widget());
            addRenderableWidget(
                    WidgetWrapper.string("is: " + analyzeType(config))
                            .right(width, 10).y(a).widget());
        }
    }

    protected String analyzeType(Config.Value<?> value) {
        var def = value.getDefaultValue();
        var type = def.getClass();
        var str = type.getSimpleName();

        if (type.getTypeParameters().length == 1
                && value.getSerializer().toString().contains("String")) {
            str += "<Stringifiable>";
        }

        return str;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parentScreen);
    }
}
