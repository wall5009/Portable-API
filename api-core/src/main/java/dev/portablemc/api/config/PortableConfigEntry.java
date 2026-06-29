/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * One typed config entry.
 *
 * @param key stable config key
 * @param defaultValue default value
 * @param comment optional comment
 * @param parser text parser
 * @param formatter text formatter
 * @param validator value validator
 * @param <T> value type
 */
@PublicApi
@Since("1.1.0")
public record PortableConfigEntry<T>(
        String key,
        T defaultValue,
        String comment,
        Function<String, T> parser,
        Function<T, String> formatter,
        Predicate<T> validator
) {
    /**
     * Creates a config entry.
     */
    public PortableConfigEntry {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(defaultValue, "defaultValue");
        Objects.requireNonNull(comment, "comment");
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(formatter, "formatter");
        Objects.requireNonNull(validator, "validator");
        if (!key.matches("[a-zA-Z0-9_.-]+")) {
            throw new IllegalArgumentException("Config key contains unsupported characters: " + key);
        }
        if (!validator.test(defaultValue)) {
            throw new IllegalArgumentException("Default value does not satisfy validation for " + key);
        }
        if (defaultValue instanceof List<?> list && list.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Default list contains null element for " + key);
        }
    }

    T parseValidated(final String raw) {
        T value = parser.apply(raw);
        if (!validator.test(value)) {
            throw new IllegalArgumentException("Value is outside the allowed range");
        }
        return value;
    }

    String format(final Object value) {
        @SuppressWarnings("unchecked")
        T typed = (T) value;
        return formatter.apply(typed);
    }
}
