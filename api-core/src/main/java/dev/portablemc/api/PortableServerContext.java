/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Loader-neutral server lifecycle context.
 *
 * @param worldDirectory directory used by the current server run
 */
public record PortableServerContext(Path worldDirectory) {
    /**
     * Creates a server context.
     */
    public PortableServerContext {
        Objects.requireNonNull(worldDirectory, "worldDirectory");
    }
}
