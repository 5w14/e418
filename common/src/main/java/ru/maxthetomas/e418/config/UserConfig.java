package ru.maxthetomas.e418.config;

import java.util.ArrayList;
import java.util.List;

public class UserConfig {
    private static final List<ModifiableOption<?>> FIELDS = new ArrayList<>();


    public record ModifiableOption<T>(Config.Value<T> value, OptionType optionType) {
    }

    public enum OptionType {
        STRING,
        BOOLEAN,
        FLOAT,
        INTEGER,
        SET,
        RANGE
    }
}
