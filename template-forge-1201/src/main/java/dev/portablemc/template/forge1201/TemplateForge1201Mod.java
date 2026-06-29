/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
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
     */

    @SuppressWarnings("removal")
    public TemplateForge1201Mod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Forge1201Bootstrap.initialize(PortableTemplateMod.MOD_ID, modEventBus, new PortableTemplateMod());
    }
}
