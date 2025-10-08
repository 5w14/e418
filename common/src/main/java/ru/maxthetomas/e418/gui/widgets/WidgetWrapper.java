package ru.maxthetomas.e418.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;

/**
 * Chainable wrapper for adding and configuring widgets.
 */
public class WidgetWrapper<T extends AbstractWidget> {
    protected final T widget;

    protected WidgetWrapper(T widget) {
        this.widget = widget;
    }

    public WidgetWrapper<T> setPosition(int x, int y) {
        this.widget.setPosition(x, y);
        return this;
    }

    public WidgetWrapper<T> x(int x) {
        this.widget.setX(x);
        return this;
    }

    public WidgetWrapper<T> y(int y) {
        this.widget.setY(y);
        return this;
    }

    public WidgetWrapper<T> w(int w) {
        this.widget.setWidth(w);
        return this;
    }

    public WidgetWrapper<T> h(int h) {
        this.widget.setHeight(h);
        return this;
    }

    public WidgetWrapper<T> setSize(int w, int h) {
        this.widget.setSize(w, h);
        return this;
    }

    public WidgetWrapper<T> centerX(int screenWidth) {
        var width = this.widget.getWidth();
        this.widget.setX(screenWidth / 2 - width / 2);
        return this;
    }

    public WidgetWrapper<T> centerY(int screenHeight) {
        var height = this.widget.getHeight();
        this.widget.setY(screenHeight / 2 - height / 2);
        return this;
    }


    public WidgetWrapper<T> bottom(int screenHeight, int offset) {
        return this.y(screenHeight - offset - this.widget.getHeight());
    }

    public WidgetWrapper<T> right(int screenWidth, int offset) {
        return this.x(screenWidth - offset - this.widget.getWidth());
    }

    public WidgetWrapper<T> center(int screenWidth, int screenHeight) {
        return this.centerX(screenWidth).centerY(screenHeight);
    }

    public WidgetWrapper<T> apply(Modifier<T> modifier) {
        modifier.apply(this.widget);
        return this;
    }

    public static <T extends AbstractWidget> WidgetWrapper<T> create(T widget) {
        return new WidgetWrapper<T>(widget);
    }

    public static WidgetWrapper<MultiLineTextWidget> string(String translatableKey) {
        return new MultilineTextWidgetWrapper(
                new MultiLineTextWidget(Component.literal(translatableKey), Minecraft.getInstance().font));
    }


    public T widget() {
        return widget;
    }

    @FunctionalInterface
    public interface Modifier<T> {
        void apply(T widget);
    }
}
