/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Non-fatal configuration load or recovery warning.
 *
 * @param fileName config file name
 * @param key config key, or an empty string for whole-file warnings
 * @param message warning message
 */
@PublicApi
@Since("1.1.0")
public record PortableConfigWarning(String fileName, String key, String message) {
    /**
     * Creates a config warning.
     */
    public PortableConfigWarning {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(message, "message");
    }
}
