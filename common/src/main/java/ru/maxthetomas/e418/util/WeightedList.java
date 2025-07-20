package ru.maxthetomas.e418.util;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class WeightedList<T> {
    public List<Entry<T>> values = new ArrayList<>();

    public void add(int weight, T value) {
        values.add(new Entry<>(weight, value));
    }

    public T getRandomElement(RandomSource random) {
        if (values.isEmpty()) {
            return null;
        }

        var totalWeight = 0;
        for (var pair : values) {
            totalWeight += pair.weight;
        }

        int randomWeight = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (var pair : values) {
            cumulativeWeight += pair.weight;
            if (randomWeight < cumulativeWeight) {
                return pair.element;
            }
        }

        return values.getLast().element;
    }

    /**
     * Executes {@code getRandomEvent} with a new {@linkplain RandomSource}
     */
    public T getRandomElement() {
        return getRandomElement(RandomSource.create());
    }

    public void clear() {
        values.clear();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Entry<T> getLast() {
        return values.getLast();
    }

    public int size() {
        return values.size();
    }

    public record Entry<T>(int weight, T element) {
    }
}

