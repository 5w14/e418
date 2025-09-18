package ru.maxthetomas.e418.codecs.numberprovider.impl;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.E418;
import ru.maxthetomas.e418.codecs.numberprovider.NumberProvider;
import ru.maxthetomas.e418.codecs.numberprovider.NumberRequester;
import ru.maxthetomas.e418.event.EventContext;

public class ConstantNumberProvider implements NumberProvider {
    private final Number number;

    public ConstantNumberProvider(Number number) {
        this.number = number;
    }

    @Override
    public ResourceLocation getType() {
        return E418.resLoc("constant");
    }

    @Override
    public Number get(EventContext context, NumberRequester requester) {
        return number;
    }
}
