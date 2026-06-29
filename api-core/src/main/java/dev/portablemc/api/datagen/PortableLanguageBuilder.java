/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Deterministic builder for Minecraft language JSON files.
 */
@PublicApi
@Since("1.1.0")
public final class PortableLanguageBuilder {
    private final Map<String, String> entries = new TreeMap<>();

    /**
     * Adds one translation entry.
     *
     * @param key translation key
     * @param value translation value
     * @return this builder
     */
    public PortableLanguageBuilder add(final String key, final String value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        if (entries.putIfAbsent(key, PortableJson.quote(value)) != null) {
            throw new IllegalStateException("Duplicate language entry: " + key);
        }
        return this;
    }

    /**
     * Builds stable JSON.
     *
     * @return JSON text
     */
    public String toJson() {
        return PortableJson.object(entries);
    }
}
