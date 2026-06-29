/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe handle for a typed config file.
 */
@PublicApi
@Since("1.1.0")
public final class PortableConfigHandle {
    private final Path path;
    private final PortableTypedConfigSpec spec;
    private final AtomicReference<PortableConfigSnapshot> snapshot;
    private final AtomicReference<List<PortableConfigWarning>> warnings = new AtomicReference<>(List.of());
    private final List<PortableConfigReloadListener> listeners = new CopyOnWriteArrayList<>();
    private final Object ioLock = new Object();

    PortableConfigHandle(final Path path, final PortableTypedConfigSpec spec) {
        this.path = Objects.requireNonNull(path, "path");
        this.spec = Objects.requireNonNull(spec, "spec");
        this.snapshot = new AtomicReference<>(defaults(spec));
    }

    /**
     * Returns the config file path.
     *
     * @return config path
     */
    public Path path() {
        return path;
    }

    /**
     * Returns the most recent immutable snapshot.
     *
     * @return current snapshot
     */
    public PortableConfigSnapshot snapshot() {
        return snapshot.get();
    }

    /**
     * Returns warnings from the most recent load or reload.
     *
     * @return immutable warning list
     */
    public List<PortableConfigWarning> warnings() {
        return warnings.get();
    }

    /**
     * Adds a reload listener.
     *
     * @param listener listener
     */
    public void addReloadListener(final PortableConfigReloadListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    /**
     * Loads or creates the file, recovering malformed values to defaults and
     * atomically saving a repaired file when needed.
     */
    public void load() {
        synchronized (ioLock) {
            try {
                Files.createDirectories(path.getParent());
                if (Files.notExists(path)) {
                    snapshot.set(defaults(spec));
                    warnings.set(List.of());
                    saveSnapshot(snapshot.get());
                    return;
                }
                LoadResult result = loadFromDisk();
                snapshot.set(result.snapshot());
                warnings.set(result.warnings());
                if (!result.warnings().isEmpty() || result.rewriteRequired()) {
                    saveSnapshot(result.snapshot());
                }
            } catch (IOException exception) {
                throw new UncheckedIOException("Failed to load config file " + path, exception);
            }
        }
    }

    /**
     * Reloads the config file and notifies listeners after the snapshot changes.
     */
    public void reload() {
        load();
        PortableConfigSnapshot current = snapshot.get();
        listeners.forEach(listener -> listener.onReload(current));
    }

    /**
     * Saves the current snapshot using atomic replacement when the file system
     * supports it.
     */
    public void save() {
        synchronized (ioLock) {
            try {
                saveSnapshot(snapshot.get());
            } catch (IOException exception) {
                throw new UncheckedIOException("Failed to save config file " + path, exception);
            }
        }
    }

    private LoadResult loadFromDisk() throws IOException {
        Map<String, String> rawValues = parseRaw(Files.readAllLines(path, StandardCharsets.UTF_8));
        Map<String, Object> values = new LinkedHashMap<>();
        List<PortableConfigWarning> loadWarnings = new ArrayList<>();
        boolean rewrite = false;

        for (PortableConfigEntry<?> entry : spec.entries()) {
            String raw = rawValues.remove(entry.key());
            if (raw == null) {
                values.put(entry.key(), entry.defaultValue());
                loadWarnings.add(warning(entry.key(), "Missing value; restored default"));
                rewrite = true;
                continue;
            }
            try {
                values.put(entry.key(), entry.parseValidated(raw.trim()));
            } catch (IllegalArgumentException exception) {
                values.put(entry.key(), entry.defaultValue());
                loadWarnings.add(warning(entry.key(), "Malformed value '" + raw + "'; restored default"));
                rewrite = true;
            }
        }

        rawValues.keySet().stream()
                .sorted()
                .forEach(key -> loadWarnings.add(warning(key, "Unknown key ignored and removed on recovery save")));
        if (!rawValues.isEmpty()) {
            rewrite = true;
        }
        return new LoadResult(new PortableConfigSnapshot(values), List.copyOf(loadWarnings), rewrite);
    }

    private Map<String, String> parseRaw(final List<String> lines) {
        Map<String, String> rawValues = new HashMap<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int separator = trimmed.indexOf('=');
            if (separator < 1) {
                warnings.set(List.of(warning("", "Ignored malformed line: " + trimmed)));
                continue;
            }
            rawValues.put(trimmed.substring(0, separator).trim(), trimmed.substring(separator + 1).trim());
        }
        return rawValues;
    }

    private void saveSnapshot(final PortableConfigSnapshot value) throws IOException {
        Files.createDirectories(path.getParent());
        Path temp = Files.createTempFile(path.getParent(), path.getFileName().toString(), ".tmp");
        Files.writeString(temp, render(value), StandardCharsets.UTF_8);
        try {
            Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String render(final PortableConfigSnapshot value) {
        String lineSeparator = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        if (!spec.headerComment().isBlank()) {
            for (String line : spec.headerComment().split("\\R")) {
                builder.append("# ").append(line).append(lineSeparator);
            }
            builder.append(lineSeparator);
        }
        for (PortableConfigEntry<?> entry : spec.entries()) {
            if (!entry.comment().isBlank()) {
                builder.append("# ").append(entry.comment()).append(lineSeparator);
            }
            builder.append(entry.key()).append('=').append(entry.format(value.asMap().get(entry.key()))).append(lineSeparator);
            builder.append(lineSeparator);
        }
        return builder.toString();
    }

    private static PortableConfigSnapshot defaults(final PortableTypedConfigSpec spec) {
        Map<String, Object> values = new LinkedHashMap<>();
        spec.entries().forEach(entry -> values.put(entry.key(), entry.defaultValue()));
        return new PortableConfigSnapshot(values);
    }

    private PortableConfigWarning warning(final String key, final String message) {
        return new PortableConfigWarning(spec.fileName(), key, message);
    }

    private record LoadResult(
            PortableConfigSnapshot snapshot,
            List<PortableConfigWarning> warnings,
            boolean rewriteRequired
    ) {
    }
}
