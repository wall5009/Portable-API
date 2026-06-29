/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.template.fabric1201;

import dev.portablemc.api.fabric1201.Fabric1201Bootstrap;
import dev.portablemc.template.PortableTemplateMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

/**
 * Thin Fabric 1.20.1 entrypoint for the template mod.
 */
public final class TemplateFabric1201Entrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        Fabric1201Bootstrap.initialize(PortableTemplateMod.MOD_ID, new PortableTemplateMod());
    }

    @Override
    public void onInitializeClient() {
        Fabric1201Bootstrap.initializeClient(PortableTemplateMod.MOD_ID);
    }
}
