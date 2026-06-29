/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import java.util.Objects;

/**
 * Simple configuration file declaration.
 *
 * @param fileName file name below the loader config directory
 * @param defaultContent default UTF-8 content written when the file is missing
 */
public record PortableConfigSpec(String fileName, String defaultContent) {
    /**
     * Validates a config declaration.
     */
    public PortableConfigSpec {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(defaultContent, "defaultContent");
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            throw new IllegalArgumentException("Config fileName must be a simple file name: " + fileName);
        }
    }
}
