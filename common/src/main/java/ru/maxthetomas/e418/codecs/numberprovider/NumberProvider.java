package ru.maxthetomas.e418.codecs.numberprovider;

import net.minecraft.resources.ResourceLocation;
import ru.maxthetomas.e418.event.EventContext;

public interface NumberProvider {
    ResourceLocation getType();

    default Number get() {
        return get(null, null);
    }

    Number get(EventContext context, NumberRequester requester);
}
