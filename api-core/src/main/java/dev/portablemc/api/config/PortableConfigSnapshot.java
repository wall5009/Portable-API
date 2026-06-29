/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of typed configuration values.
 */
@PublicApi
@Since("1.1.0")
public final class PortableConfigSnapshot {
    private final Map<String, Object> values;

    PortableConfigSnapshot(final Map<String, Object> values) {
        this.values = Map.copyOf(Objects.requireNonNull(values, "values"));
    }

    /**
     * Returns a typed value from this immutable snapshot.
     *
     * @param entry config entry
     * @param <T> value type
     * @return configured value
     */
    public <T> T get(final PortableConfigEntry<T> entry) {
        Objects.requireNonNull(entry, "entry");
        Object value = values.get(entry.key());
        if (value == null) {
            return entry.defaultValue();
        }
        @SuppressWarnings("unchecked")
        T typed = (T) value;
        return typed;
    }

    /**
     * Returns an immutable key/value view.
     *
     * @return values by key
     */
    public Map<String, Object> asMap() {
        return values;
    }
}
