/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
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
