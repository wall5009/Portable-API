/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.spi;

import dev.portablemc.api.content.PortableBlockDefinition;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableCreativeTabEntry;
import dev.portablemc.api.content.PortableCreativeTabDefinition;
import dev.portablemc.api.content.PortableCustomCreativeTabEntry;
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

    /**
     * Registers a simple custom creative tab.
     *
     * @param definition creative-tab declaration
     * @return creative-tab handle
     */
    default PortableRegistryHandle<PortableCreativeTabDefinition> registerCreativeTab(
            final PortableCreativeTabDefinition definition
    ) {
        throw new UnsupportedOperationException("Portable custom creative tabs are not implemented by this adapter");
    }

    /**
     * Adds an item to a custom creative tab.
     *
     * @param entry custom creative-tab entry
     */
    default void addCustomCreativeTabEntry(final PortableCustomCreativeTabEntry entry) {
        throw new UnsupportedOperationException("Portable custom creative tab entries are not implemented by this adapter");
    }
}
