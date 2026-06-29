/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Typed portable configuration file declaration.
 */
@PublicApi
@Since("1.1.0")
public final class PortableTypedConfigSpec {
    private final String fileName;
    private final PortableConfigScope scope;
    private final String headerComment;
    private final List<PortableConfigEntry<?>> entries;

    private PortableTypedConfigSpec(
            final String fileName,
            final PortableConfigScope scope,
            final String headerComment,
            final List<PortableConfigEntry<?>> entries
    ) {
        this.fileName = validateFileName(fileName);
        this.scope = Objects.requireNonNull(scope, "scope");
        this.headerComment = Objects.requireNonNull(headerComment, "headerComment");
        this.entries = List.copyOf(entries);
        if (this.entries.isEmpty()) {
            throw new IllegalArgumentException("Typed config spec must contain at least one entry");
        }
    }

    /**
     * Creates a builder for a config file in the portable global scope.
     *
     * @param fileName simple file name below the loader config directory
     * @return builder
     */
    public static Builder builder(final String fileName) {
        return new Builder(fileName);
    }

    /**
     * Returns the config file name.
     *
     * @return file name
     */
    public String fileName() {
        return fileName;
    }

    /**
     * Returns the portable config scope.
     *
     * @return config scope
     */
    public PortableConfigScope scope() {
        return scope;
    }

    /**
     * Returns the header comment.
     *
     * @return header comment
     */
    public String headerComment() {
        return headerComment;
    }

    /**
     * Returns entries in deterministic write order.
     *
     * @return entries
     */
    public List<PortableConfigEntry<?>> entries() {
        return entries;
    }

    static String validateFileName(final String fileName) {
        Objects.requireNonNull(fileName, "fileName");
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            throw new IllegalArgumentException("Config fileName must be a simple file name: " + fileName);
        }
        if (!fileName.endsWith(".properties")) {
            throw new IllegalArgumentException("Portable typed config files use the .properties extension: " + fileName);
        }
        return fileName;
    }

    /**
     * Builder for a typed config spec.
     */
    public static final class Builder {
        private final String fileName;
        private PortableConfigScope scope = PortableConfigScope.GLOBAL;
        private String headerComment = "";
        private final List<PortableConfigEntry<?>> entries = new ArrayList<>();

        private Builder(final String fileName) {
            this.fileName = validateFileName(fileName);
        }

        /**
         * Sets a file header comment.
         *
         * @param comment comment text without leading {@code #}
         * @return this builder
         */
        public Builder headerComment(final String comment) {
            this.headerComment = Objects.requireNonNull(comment, "comment");
            return this;
        }

        /**
         * Sets the config scope.
         *
         * @param value scope
         * @return this builder
         */
        public Builder scope(final PortableConfigScope value) {
            this.scope = Objects.requireNonNull(value, "value");
            return this;
        }

        /**
         * Adds a boolean value.
         *
         * @param key key
         * @param defaultValue default value
         * @param comment comment
         * @return entry handle
         */
        public PortableConfigEntry<Boolean> booleanValue(
                final String key,
                final boolean defaultValue,
                final String comment
        ) {
            return add(key, defaultValue, comment, Builder::parseBoolean, Object::toString, value -> true);
        }

        /**
         * Adds a bounded integer value.
         *
         * @param key key
         * @param defaultValue default value
         * @param min inclusive minimum
         * @param max inclusive maximum
         * @param comment comment
         * @return entry handle
         */
        public PortableConfigEntry<Integer> intValue(
                final String key,
                final int defaultValue,
                final int min,
                final int max,
                final String comment
        ) {
            requireOrderedRange(min, max);
            return add(key, defaultValue, comment, Integer::parseInt, Object::toString, value -> value >= min && value <= max);
        }

        /**
         * Adds a bounded long value.
         *
         * @param key key
         * @param defaultValue default value
         * @param min inclusive minimum
         * @param max inclusive maximum
         * @param comment comment
         * @return entry handle
         */
        public PortableConfigEntry<Long> longValue(
                final String key,
                final long defaultValue,
                final long min,
                final long max,
                final String comment
        ) {
            requireOrderedRange(min, max);
            return add(key, defaultValue, comment, Long::parseLong, Object::toString, value -> value >= min && value <= max);
        }

        /**
         * Adds a bounded double value.
         *
         * @param key key
         * @param defaultValue default value
         * @param min inclusive minimum
         * @param max inclusive maximum
         * @param comment comment
         * @return entry handle
         */
        public PortableConfigEntry<Double> doubleValue(
                final String key,
                final double defaultValue,
                final double min,
                final double max,
                final String comment
        ) {
            requireOrderedRange(min, max);
            Predicate<Double> validator = value -> Double.isFinite(value) && value >= min && value <= max;
            return add(key, defaultValue, comment, Double::parseDouble, Object::toString, validator);
        }

        /**
         * Adds a string value with a maximum character count.
         *
         * @param key key
         * @param defaultValue default value
         * @param maxCharacters maximum characters
         * @param comment comment
         * @return entry handle
         */
        public PortableConfigEntry<String> stringValue(
                final String key,
                final String defaultValue,
                final int maxCharacters,
                final String comment
        ) {
            if (maxCharacters < 1) {
                throw new IllegalArgumentException("maxCharacters must be positive: " + maxCharacters);
            }
            return add(key, defaultValue, comment, Function.identity(), Function.identity(), value -> value.length() <= maxCharacters);
        }

        /**
         * Adds an enum value by enum constant name.
         *
         * @param key key
         * @param enumType enum class
         * @param defaultValue default value
         * @param comment comment
         * @param <E> enum type
         * @return entry handle
         */
        public <E extends Enum<E>> PortableConfigEntry<E> enumValue(
                final String key,
                final Class<E> enumType,
                final E defaultValue,
                final String comment
        ) {
            Objects.requireNonNull(enumType, "enumType");
            Function<String, E> parser = raw -> Enum.valueOf(enumType, raw.trim().toUpperCase(Locale.ROOT));
            Function<E, String> formatter = value -> value.name().toLowerCase(Locale.ROOT);
            return add(key, defaultValue, comment, parser, formatter, value -> true);
        }

        /**
         * Adds a bounded list of strings.
         *
         * @param key key
         * @param defaultValue default values
         * @param maxElements maximum elements
         * @param maxElementCharacters maximum characters per element
         * @param comment comment
         * @return entry handle
         */
        public PortableConfigEntry<List<String>> stringList(
                final String key,
                final List<String> defaultValue,
                final int maxElements,
                final int maxElementCharacters,
                final String comment
        ) {
            if (maxElements < 0 || maxElementCharacters < 1) {
                throw new IllegalArgumentException("List bounds must be non-negative and positive");
            }
            Predicate<List<String>> validator = values -> values.size() <= maxElements
                    && values.stream().allMatch(value -> value.length() <= maxElementCharacters);
            return add(key, List.copyOf(defaultValue), comment, PortableTypedConfigSpec::parseList,
                    PortableTypedConfigSpec::formatList, validator);
        }

        /**
         * Builds the spec.
         *
         * @return config spec
         */
        public PortableTypedConfigSpec build() {
            return new PortableTypedConfigSpec(fileName, scope, headerComment, entries);
        }

        private <T> PortableConfigEntry<T> add(
                final String key,
                final T defaultValue,
                final String comment,
                final Function<String, T> parser,
                final Function<T, String> formatter,
                final Predicate<T> validator
        ) {
            if (entries.stream().anyMatch(entry -> entry.key().equals(key))) {
                throw new IllegalStateException("Duplicate config key: " + key);
            }
            PortableConfigEntry<T> entry = new PortableConfigEntry<>(key, defaultValue, comment, parser, formatter, validator);
            entries.add(entry);
            return entry;
        }

        private static Boolean parseBoolean(final String raw) {
            String normalized = raw.trim().toLowerCase(Locale.ROOT);
            if ("true".equals(normalized)) {
                return Boolean.TRUE;
            }
            if ("false".equals(normalized)) {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Expected true or false");
        }

        private static void requireOrderedRange(final long min, final long max) {
            if (min > max) {
                throw new IllegalArgumentException("Minimum must not be greater than maximum");
            }
        }

        private static void requireOrderedRange(final double min, final double max) {
            if (!Double.isFinite(min) || !Double.isFinite(max) || min > max) {
                throw new IllegalArgumentException("Double range must be finite and ordered");
            }
        }
    }

    private static List<String> parseList(final String raw) {
        if (raw.isEmpty()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;
        for (int index = 0; index < raw.length(); index++) {
            char character = raw.charAt(index);
            if (escaping) {
                current.append(character);
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == ',') {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        if (escaping) {
            current.append('\\');
        }
        result.add(current.toString());
        return List.copyOf(result);
    }

    private static String formatList(final List<String> values) {
        return values.stream()
                .map(value -> value.replace("\\", "\\\\").replace(",", "\\,"))
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }
}
