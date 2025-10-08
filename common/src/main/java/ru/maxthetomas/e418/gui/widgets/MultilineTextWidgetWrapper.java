package ru.maxthetomas.e418.gui.widgets;

import net.minecraft.client.gui.components.MultiLineTextWidget;

public class MultilineTextWidgetWrapper extends WidgetWrapper<MultiLineTextWidget> {
    protected MultilineTextWidgetWrapper(MultiLineTextWidget w) {
        super(w);
    }

    @Override
    public WidgetWrapper<MultiLineTextWidget> w(int w) {
        this.widget.setMaxWidth(w);
        return super.w(w);
    }
}
