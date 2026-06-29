/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Contract tests for safe generated-resource writes.
 */
final class PortableDataGenerationContextTest {
    @TempDir
    private Path tempDir;

    @Test
    void writesAssetsAndDataBelowRoot() {
        PortableDataGenerationContext context = new PortableDataGenerationContext("example", tempDir);

        context.writeAsset("lang/en_us.json", "{}\n");
        context.writeData("tags/items/example.json", "{}\n");

        assertTrue(Files.isRegularFile(tempDir.resolve("assets/example/lang/en_us.json")));
        assertTrue(Files.isRegularFile(tempDir.resolve("data/example/tags/items/example.json")));
    }

    @Test
    void rejectsEscapingPath() {
        PortableDataGenerationContext context = new PortableDataGenerationContext("example", tempDir);

        assertThrows(IllegalArgumentException.class, () -> context.writeAsset("../../outside.json", "{}"));
    }
}
