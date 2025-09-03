package ru.maxthetomas.e418.neoforge.storage;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import ru.maxthetomas.e418.util.storage.PlatformDataType;
import ru.maxthetomas.e418.util.storage.data.IData;

import java.util.function.Supplier;

public class NeoForgePlatformData<H extends IAttachmentHolder, D extends IData<D>> extends PlatformDataType<H, D> {
    private final AttachmentType<D> attachmentType;

    public NeoForgePlatformData(AttachmentType<D> attachment) {
        this.attachmentType = attachment;
    }

    @Override
    public D ensureData(H object, Supplier<D> defaultData) {
        if (!object.hasData(attachmentType)) {
            var data = defaultData.get();
            object.setData(attachmentType, data);
            return data.duplicate();
        }

        var attachment = object.getData(attachmentType);
        return attachment.duplicate();
    }

    @Override
    public D getData(H object) {
        var attachment = object.getData(attachmentType);
        return attachment.duplicate();
    }

    @Override
    public void storeData(H object, D data) {
        object.setData(attachmentType, data);
    }
}
