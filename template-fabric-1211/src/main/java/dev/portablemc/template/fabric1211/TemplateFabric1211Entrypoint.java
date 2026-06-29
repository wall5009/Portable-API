/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.template.fabric1211;

import dev.portablemc.api.fabric1211.Fabric1211Bootstrap;
import dev.portablemc.template.PortableTemplateMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

/**
 * Thin Fabric 1.21.1 entrypoint for the template mod.
 */
public final class TemplateFabric1211Entrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        Fabric1211Bootstrap.initialize(PortableTemplateMod.MOD_ID, new PortableTemplateMod());
    }

    @Override
    public void onInitializeClient() {
        Fabric1211Bootstrap.initializeClient(PortableTemplateMod.MOD_ID);
    }
}
