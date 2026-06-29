/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Deterministic builder for item and block tag JSON.
 */
@PublicApi
@Since("1.1.0")
public final class PortableTagBuilder {
    private final Set<String> values = new TreeSet<>();
    private boolean replace;

    /**
     * Sets whether the tag replaces lower-priority values.
     *
     * @param value replace flag
     * @return this builder
     */
    public PortableTagBuilder replace(final boolean value) {
        this.replace = value;
        return this;
    }

    /**
     * Adds one required value.
     *
     * @param id value id
     * @return this builder
     */
    public PortableTagBuilder add(final PortableIdentifier id) {
        String value = PortableJson.id(Objects.requireNonNull(id, "id"));
        if (!values.add(value)) {
            throw new IllegalStateException("Duplicate tag value: " + id);
        }
        return this;
    }

    /**
     * Builds stable JSON.
     *
     * @return JSON text
     */
    public String toJson() {
        return "{\n"
                + "  \"replace\": " + replace + ",\n"
                + "  \"values\": " + PortableJson.array(values) + "\n"
                + "}\n";
    }
}
