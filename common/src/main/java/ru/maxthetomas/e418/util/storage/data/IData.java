package ru.maxthetomas.e418.util.storage.data;

public interface IData<T extends IData<T>> {
    public T duplicate();
}
