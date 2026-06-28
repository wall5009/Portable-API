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
