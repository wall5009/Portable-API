/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Minimal resource/data-generation writer.
 *
 * <p>The V1 API writes plain text resources rather than wrapping each loader's
 * full data-generator framework. That boundary is intentional: Minecraft's data
 * provider APIs have changed repeatedly, while generated resource paths and
 * UTF-8 contents are stable. Loader modules can add richer typed providers as
 * extension points without changing this core contract.</p>
 */
public final class PortableDataGenerationContext {
    private final String modId;
    private final Path rootDirectory;

    /**
     * Creates a context rooted at the supplied generated-resource directory.
     *
     * @param modId owning mod id
     * @param rootDirectory generation root
     */
    public PortableDataGenerationContext(final String modId, final Path rootDirectory) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory");
    }

    /**
     * Writes a client resource under {@code assets/<mod id>/}.
     *
     * @param relativePath path below the mod asset namespace
     * @param content UTF-8 content
     */
    public void writeAsset(final String relativePath, final String content) {
        write("assets/" + modId + "/" + relativePath, content);
    }

    /**
     * Writes server data under {@code data/<mod id>/}.
     *
     * @param relativePath path below the mod data namespace
     * @param content UTF-8 content
     */
    public void writeData(final String relativePath, final String content) {
        write("data/" + modId + "/" + relativePath, content);
    }

    /**
     * Writes a root-level generated resource such as {@code pack.mcmeta}.
     *
     * @param relativePath path below the generation root
     * @param content UTF-8 content
     */
    public void writeRoot(final String relativePath, final String content) {
        write(relativePath, content);
    }

    /**
     * Returns the generation root.
     *
     * @return root directory
     */
    public Path rootDirectory() {
        return rootDirectory;
    }

    private void write(final String relativePath, final String content) {
        Objects.requireNonNull(content, "content");
        Path target = resolveSafe(relativePath);
        try {
            Files.createDirectories(target.getParent());
            Files.writeString(target, content, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to write generated resource " + target, exception);
        }
    }

    private Path resolveSafe(final String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath");
        Path requested = Path.of(relativePath);
        if (requested.isAbsolute() || containsParentTraversal(requested)) {
            throw new IllegalArgumentException("Generated resource path must be relative and must not contain '..': " + relativePath);
        }
        Path normalized = rootDirectory.resolve(relativePath).normalize();
        if (!normalized.startsWith(rootDirectory.normalize())) {
            throw new IllegalArgumentException("Generated resource path escapes output directory: " + relativePath);
        }
        return normalized;
    }

    private static boolean containsParentTraversal(final Path path) {
        for (Path element : path) {
            if ("..".equals(element.toString())) {
                return true;
            }
        }
        return false;
    }
}
