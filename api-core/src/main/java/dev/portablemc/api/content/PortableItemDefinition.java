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
 * Portable declaration for a basic item or block item.
 *
 * @param id registry id
 * @param settings portable item settings
 * @param blockItem whether this item represents the block with the same id
 */
public record PortableItemDefinition(PortableIdentifier id, PortableItemSettings settings, boolean blockItem) {
    /**
     * Creates an item declaration.
     */
    public PortableItemDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(settings, "settings");
    }
}
