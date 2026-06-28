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
