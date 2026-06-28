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
package dev.portablemc.template.forge1201;

import dev.portablemc.api.forge1201.Forge1201Bootstrap;
import dev.portablemc.template.PortableTemplateMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Thin Forge 1.20.1 entrypoint for the template mod.
 */
@Mod(PortableTemplateMod.MOD_ID)
public final class TemplateForge1201Mod {
    /**
     * Forge injects the mod event bus for modern Java mod constructors.
     *
     * @param modEventBus mod event bus
     */

    @SuppressWarnings("removal")
    public TemplateForge1201Mod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Forge1201Bootstrap.initialize(PortableTemplateMod.MOD_ID, modEventBus, new PortableTemplateMod());
    }
}
