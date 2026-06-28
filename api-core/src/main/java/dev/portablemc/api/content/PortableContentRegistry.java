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
import dev.portablemc.api.spi.PortableContentAdapter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Basic content registration facade.
 *
 * <p>The registry performs portable validation and duplicate checks before
 * delegating to the active loader. It intentionally covers only simple blocks,
 * simple items, and vanilla creative-tab membership in V1.</p>
 */
public final class PortableContentRegistry {
    private final String modId;
    private final PortableContentAdapter adapter;
    private final Set<PortableIdentifier> blocks = new HashSet<>();
    private final Set<PortableIdentifier> items = new HashSet<>();

    /**
     * Creates a content registry.
     *
     * @param modId owning mod id
     * @param adapter loader adapter
     */
    public PortableContentRegistry(final String modId, final PortableContentAdapter adapter) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    /**
     * Registers a simple block and a block item with the same id.
     *
     * @param path registry path under this mod id
     * @param blockSettings block settings
     * @param itemSettings generated block-item settings
     * @return handles for the block and block item
     */
    public PortableBlockRegistration registerSimpleBlock(
            final String path,
            final PortableBlockSettings blockSettings,
            final PortableItemSettings itemSettings
    ) {
        PortableIdentifier id = PortableIdentifier.of(modId, path);
        reserve(blocks, id, "block");
        reserve(items, id, "item");
        PortableBlockDefinition blockDefinition = new PortableBlockDefinition(id, blockSettings);
        PortableItemDefinition itemDefinition = new PortableItemDefinition(id, itemSettings, true);
        return adapter.registerSimpleBlock(blockDefinition, itemDefinition);
    }

    /**
     * Registers a standalone item.
     *
     * @param path registry path under this mod id
     * @param settings item settings
     * @return item handle
     */
    public PortableRegistryHandle<PortableItemDefinition> registerItem(final String path, final PortableItemSettings settings) {
        PortableIdentifier id = PortableIdentifier.of(modId, path);
        reserve(items, id, "item");
        return adapter.registerItem(new PortableItemDefinition(id, settings, false));
    }

    /**
     * Adds an item to one of the stable vanilla creative tabs.
     *
     * @param tab target tab
     * @param item item handle returned from a prior registration
     */
    public void addToCreativeTab(
            final PortableCreativeTabKey tab,
            final PortableRegistryHandle<PortableItemDefinition> item
    ) {
        Objects.requireNonNull(item, "item");
        if (!items.contains(item.id())) {
            throw new IllegalArgumentException("Cannot add unregistered item to creative tab: " + item.id());
        }
        adapter.addCreativeTabEntry(new PortableCreativeTabEntry(tab, item.id()));
    }

    private static void reserve(final Set<PortableIdentifier> ids, final PortableIdentifier id, final String kind) {
        if (!ids.add(id)) {
            throw new IllegalStateException("Duplicate " + kind + " registration for " + id);
        }
    }
}
