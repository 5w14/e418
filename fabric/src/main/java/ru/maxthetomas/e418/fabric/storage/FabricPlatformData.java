package ru.maxthetomas.e418.fabric.storage;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import ru.maxthetomas.e418.util.storage.PlatformDataType;
import ru.maxthetomas.e418.util.storage.data.IData;

import java.util.function.Supplier;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public class FabricPlatformData<H extends AttachmentTarget, D extends IData<D>> extends PlatformDataType<H, D> {
    private final AttachmentType<D> attachmentType;

    public FabricPlatformData(AttachmentType<D> attachment) {
        this.attachmentType = attachment;
    }

    @Override
    public D ensureData(H object, Supplier<D> defaultData) {
        var attachment = object.getAttachedOrCreate(attachmentType, defaultData);
        return attachment.duplicate();
    }

    @Override
    public D getData(H object) {
        var attachment = object.getAttached(attachmentType);
        if (attachment == null)
            return null;
        else
            return attachment.duplicate();
    }

    @Override
    public void storeData(H object, D data) {
        object.setAttached(attachmentType, data);
    }
}
