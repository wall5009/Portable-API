/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Builder for {@code pack.mcmeta}.
 */
@PublicApi
@Since("1.1.0")
public final class PortablePackMetadataBuilder {
    private int packFormat;
    private String description = "";

    /**
     * Sets the pack format.
     *
     * @param value pack format
     * @return this builder
     */
    public PortablePackMetadataBuilder packFormat(final int value) {
        if (value < 1) {
            throw new IllegalArgumentException("pack_format must be positive: " + value);
        }
        this.packFormat = value;
        return this;
    }

    /**
     * Sets the description.
     *
     * @param value description
     * @return this builder
     */
    public PortablePackMetadataBuilder description(final String value) {
        this.description = Objects.requireNonNull(value, "value");
        return this;
    }

    /**
     * Builds stable JSON.
     *
     * @return JSON text
     */
    public String toJson() {
        if (packFormat < 1) {
            throw new IllegalStateException("pack_format has not been set");
        }
        return "{\n"
                + "  \"pack\": {\n"
                + "    \"pack_format\": " + packFormat + ",\n"
                + "    \"description\": " + PortableJson.quote(description) + "\n"
                + "  }\n"
                + "}\n";
    }
}
