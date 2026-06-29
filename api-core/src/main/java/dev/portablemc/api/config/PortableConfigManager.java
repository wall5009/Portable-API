/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Configuration manager rooted at the active loader's config directory.
 */
public final class PortableConfigManager {
    private final Path configDirectory;
    private final Set<String> registeredFiles = new HashSet<>();

    /**
     * Creates a config manager rooted at the active loader's config directory.
     *
     * @param configDirectory config directory
     */
    public PortableConfigManager(final Path configDirectory) {
        this.configDirectory = Objects.requireNonNull(configDirectory, "configDirectory");
    }

    /**
     * Registers a config file and writes its default contents if it does not
     * already exist. Existing user configuration is never overwritten.
     *
     * @param spec config declaration
     * @return path to the config file
     */
    @Deprecated(since = "1.1.0", forRemoval = false)
    public Path register(final PortableConfigSpec spec) {
        Objects.requireNonNull(spec, "spec");
        if (!registeredFiles.add(spec.fileName())) {
            throw new IllegalStateException("Duplicate config registration for " + spec.fileName());
        }
        Path target = configDirectory.resolve(spec.fileName()).normalize();
        if (!target.startsWith(configDirectory.normalize())) {
            throw new IllegalArgumentException("Config path escapes config directory: " + spec.fileName());
        }
        try {
            Files.createDirectories(configDirectory);
            if (Files.notExists(target)) {
                Files.writeString(target, spec.defaultContent(), StandardCharsets.UTF_8);
            }
            return target;
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to prepare config file " + target, exception);
        }
    }

    /**
     * Registers, loads, and if necessary creates a typed config file. Malformed
     * values are recovered to defaults and recorded on the returned handle.
     *
     * @param spec typed config declaration
     * @return config handle
     */
    public PortableConfigHandle registerTyped(final PortableTypedConfigSpec spec) {
        Objects.requireNonNull(spec, "spec");
        if (!registeredFiles.add(spec.fileName())) {
            throw new IllegalStateException("Duplicate config registration for " + spec.fileName());
        }
        Path target = configDirectory.resolve(spec.fileName()).normalize();
        if (!target.startsWith(configDirectory.normalize())) {
            throw new IllegalArgumentException("Config path escapes config directory: " + spec.fileName());
        }
        PortableConfigHandle handle = new PortableConfigHandle(target, spec);
        handle.load();
        return handle;
    }
}
