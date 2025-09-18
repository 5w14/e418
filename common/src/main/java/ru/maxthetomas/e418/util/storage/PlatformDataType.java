package ru.maxthetomas.e418.util.storage;

import java.util.function.Supplier;

/**
 * For platform implementation for data management.
 *
 * @param <O> Object to store data on
 * @param <D> Data type to store on object
 */
public abstract class PlatformDataType<O, D> {
    public abstract D ensureData(O object, Supplier<D> defaultData);

    public abstract D getData(O object);

    public abstract void storeData(O object, D data);

    public void reset() {
    }

    public static class NoopStorage<A, B> extends PlatformDataType<A, B> {
        @Override
        public B ensureData(A object, Supplier<B> defaultData) {
            throw new IllegalStateException("NonDefined storage was not replaced in mod initialization!");
        }

        @Override
        public B getData(A object) {
            throw new IllegalStateException("NonDefined storage was not replaced in mod initialization!");
        }

        @Override
        public void storeData(A object, B data) {
            throw new IllegalStateException("NonDefined storage was not replaced in mod initialization!");
        }

        public static <A, B> NoopStorage<A, B> construct() {
            return new NoopStorage<A, B>();
        }
    }
}
