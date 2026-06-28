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
 * Portable request to add an item to a vanilla creative tab.
 *
 * @param tab target tab
 * @param itemId item registry id
 */
public record PortableCreativeTabEntry(PortableCreativeTabKey tab, PortableIdentifier itemId) {
    /**
     * Creates a creative-tab entry.
     */
    public PortableCreativeTabEntry {
        Objects.requireNonNull(tab, "tab");
        Objects.requireNonNull(itemId, "itemId");
    }
}
