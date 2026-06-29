/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for typed config load, recovery, and reload behavior.
 */
final class PortableTypedConfigTest {
    @TempDir
    private Path tempDir;

    @Test
    void createsDefaultsAndLoadsSnapshot() {
        PortableTypedConfigSpec.Builder builder = PortableTypedConfigSpec.builder("example.properties")
                .headerComment("Example config");
        PortableConfigEntry<Boolean> enabled = builder.booleanValue("enabled", true, "Enabled flag");
        PortableConfigEntry<Integer> count = builder.intValue("count", 3, 0, 10, "Bounded count");
        PortableConfigEntry<List<String>> names = builder.stringList("names", List.of("a", "b"), 4, 8, "Names");

        PortableConfigHandle handle = new PortableConfigManager(tempDir).registerTyped(builder.build());

        assertTrue(handle.snapshot().get(enabled));
        assertEquals(3, handle.snapshot().get(count));
        assertEquals(List.of("a", "b"), handle.snapshot().get(names));
        assertTrue(Files.isRegularFile(tempDir.resolve("example.properties")));
    }

    @Test
    void recoversMalformedValuesAndNotifiesReloadListeners() throws Exception {
        PortableTypedConfigSpec.Builder builder = PortableTypedConfigSpec.builder("example.properties");
        PortableConfigEntry<Boolean> enabled = builder.booleanValue("enabled", true, "Enabled flag");
        PortableConfigEntry<Integer> count = builder.intValue("count", 3, 0, 10, "Bounded count");
        PortableConfigEntry<Mode> mode = builder.enumValue("mode", Mode.class, Mode.QUIET, "Mode");
        Files.writeString(tempDir.resolve("example.properties"), "enabled=nope\ncount=99\nmode=loud\nunknown=value\n");

        PortableConfigHandle handle = new PortableConfigManager(tempDir).registerTyped(builder.build());
        assertFalse(handle.warnings().isEmpty());
        AtomicInteger reloads = new AtomicInteger();
        handle.addReloadListener(snapshot -> reloads.incrementAndGet());
        handle.reload();

        assertTrue(handle.snapshot().get(enabled));
        assertEquals(3, handle.snapshot().get(count));
        assertEquals(Mode.LOUD, handle.snapshot().get(mode));
        assertEquals(1, reloads.get());
    }

    private enum Mode {
        QUIET,
        LOUD
    }
}
