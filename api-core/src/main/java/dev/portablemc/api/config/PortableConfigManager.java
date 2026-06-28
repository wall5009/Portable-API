/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
 * Minimal config hook that writes default config files without prescribing a
 * serialization library.
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
}
