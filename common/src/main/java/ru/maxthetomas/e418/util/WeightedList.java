package ru.maxthetomas.e418.util;

import java.util.*;

public class WeightedList<T> {
    public List<Entry<T>> values;

    public void add(int weight, T value){
        values.add(new Entry<>(weight, value));
    }

    public T getRandomElement(Random random) {
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
     * Executes {@code getRandomEvent} with a new {@linkplain Random}
     */
    public T getRandomElement() {
        return getRandomElement(new Random());
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

    public record Entry<T>(int weight, T element) {}
}

