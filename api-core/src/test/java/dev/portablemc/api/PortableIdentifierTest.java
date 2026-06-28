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
package dev.portablemc.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Contract tests for portable id validation.
 */
final class PortableIdentifierTest {
    @Test
    void normalizesNamespace() {
        assertEquals("example_mod:block/path", PortableIdentifier.of("Example_Mod", "block/path").asString());
    }

    @Test
    void parsesDefaultNamespace() {
        assertEquals(new PortableIdentifier("example", "item"), PortableIdentifier.parse("item", "example"));
    }

    @Test
    void rejectsUnsafePathCharacters() {
        assertThrows(IllegalArgumentException.class, () -> PortableIdentifier.of("example", "Bad Path"));
    }
}
