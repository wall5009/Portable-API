/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import dev.portablemc.api.PortableIdentifier;
import java.util.Objects;

/**
 * Stable handle returned to shared code after a registry declaration.
 *
 * <p>The handle intentionally exposes only the id and declared definition type.
 * Native Minecraft objects are loader- and version-specific; shared code should
 * pass ids through Portable API services instead of storing raw objects.</p>
 *
 * @param id registered id
 * @param definitionType Java type of the portable definition
 * @param <T> portable definition type
 */
public record PortableRegistryHandle<T>(PortableIdentifier id, Class<T> definitionType) {
    /**
     * Creates a registry handle.
     */
    public PortableRegistryHandle {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(definitionType, "definitionType");
    }
}
