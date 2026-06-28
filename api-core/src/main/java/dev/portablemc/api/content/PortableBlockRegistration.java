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

import java.util.Objects;

/**
 * Pair of handles returned by simple block registration.
 *
 * @param block handle for the block
 * @param item handle for the generated block item
 */
public record PortableBlockRegistration(
        PortableRegistryHandle<PortableBlockDefinition> block,
        PortableRegistryHandle<PortableItemDefinition> item
) {
    /**
     * Creates a block registration result.
     */
    public PortableBlockRegistration {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(item, "item");
    }
}
