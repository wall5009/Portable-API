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
