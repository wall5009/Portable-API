/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.template.neoforge1211;

import dev.portablemc.api.neoforge1211.NeoForge1211Bootstrap;
import dev.portablemc.template.PortableTemplateMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Thin NeoForge 1.21.1 entrypoint for the template mod.
 */
@Mod(PortableTemplateMod.MOD_ID)
public final class TemplateNeoForge1211Mod {
    /**
     * NeoForge injects the mod event bus for Java mod constructors.
     *
     * @param modEventBus mod event bus
     */
    public TemplateNeoForge1211Mod(final IEventBus modEventBus) {
        NeoForge1211Bootstrap.initialize(PortableTemplateMod.MOD_ID, modEventBus, new PortableTemplateMod());
    }
}
