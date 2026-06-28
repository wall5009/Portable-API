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
package dev.portablemc.api.spi;

import dev.portablemc.api.content.PortableBlockDefinition;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableCreativeTabEntry;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.content.PortableRegistryHandle;

/**
 * Service-provider interface implemented by loader modules for content registration.
 *
 * <p>Mod authors normally use {@code PortableModContext.content()}; direct use
 * of this SPI is for platform adapters and advanced integration tests.</p>
 */
public interface PortableContentAdapter {
    /**
     * Registers a basic block and matching block item.
     *
     * @param blockDefinition portable block declaration
     * @param itemDefinition portable block-item declaration
     * @return portable handles for both registered objects
     */
    PortableBlockRegistration registerSimpleBlock(PortableBlockDefinition blockDefinition, PortableItemDefinition itemDefinition);

    /**
     * Registers a basic standalone item.
     *
     * @param itemDefinition portable item declaration
     * @return portable item handle
     */
    PortableRegistryHandle<PortableItemDefinition> registerItem(PortableItemDefinition itemDefinition);

    /**
     * Adds an item to a vanilla creative tab.
     *
     * @param entry creative-tab entry
     */
    void addCreativeTabEntry(PortableCreativeTabEntry entry);
}
