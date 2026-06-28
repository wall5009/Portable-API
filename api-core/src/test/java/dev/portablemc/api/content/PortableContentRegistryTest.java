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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.portablemc.api.spi.PortableContentAdapter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for common content registration validation.
 */
final class PortableContentRegistryTest {
    @Test
    void registersSimpleBlockAndCreativeTabEntry() {
        FakeContentAdapter adapter = new FakeContentAdapter();
        PortableContentRegistry registry = new PortableContentRegistry("example", adapter);

        PortableBlockRegistration block = registry.registerSimpleBlock(
                "portable_block",
                PortableBlockSettings.stoneLike(),
                PortableItemSettings.defaults()
        );
        registry.addToCreativeTab(PortableCreativeTabKey.BUILDING_BLOCKS, block.item());

        assertEquals("example:portable_block", block.block().id().asString());
        assertEquals(1, adapter.blocks.size());
        assertEquals(1, adapter.creativeTabEntries.size());
    }

    @Test
    void rejectsDuplicateItems() {
        FakeContentAdapter adapter = new FakeContentAdapter();
        PortableContentRegistry registry = new PortableContentRegistry("example", adapter);

        registry.registerItem("item", PortableItemSettings.defaults());

        assertThrows(IllegalStateException.class, () -> registry.registerItem("item", PortableItemSettings.defaults()));
    }

    private static final class FakeContentAdapter implements PortableContentAdapter {
        private final List<PortableBlockDefinition> blocks = new ArrayList<>();
        private final List<PortableItemDefinition> items = new ArrayList<>();
        private final List<PortableCreativeTabEntry> creativeTabEntries = new ArrayList<>();

        @Override
        public PortableBlockRegistration registerSimpleBlock(
                final PortableBlockDefinition blockDefinition,
                final PortableItemDefinition itemDefinition
        ) {
            blocks.add(blockDefinition);
            items.add(itemDefinition);
            return new PortableBlockRegistration(
                    new PortableRegistryHandle<>(blockDefinition.id(), PortableBlockDefinition.class),
                    new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class)
            );
        }

        @Override
        public PortableRegistryHandle<PortableItemDefinition> registerItem(final PortableItemDefinition itemDefinition) {
            items.add(itemDefinition);
            return new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class);
        }

        @Override
        public void addCreativeTabEntry(final PortableCreativeTabEntry entry) {
            creativeTabEntries.add(entry);
        }
    }
}
