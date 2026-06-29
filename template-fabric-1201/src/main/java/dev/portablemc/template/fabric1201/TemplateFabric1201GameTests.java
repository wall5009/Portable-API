/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.template.fabric1201;

import dev.portablemc.template.PortableTemplateMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Fabric 1.20.1 GameTests for the copyable template mod.
 */
public final class TemplateFabric1201GameTests implements FabricGameTest {
    /**
     * Verifies that Fabric discovers the template test entrypoint and can run a
     * headless server-side GameTest with the template mod loaded.
     *
     * @param helper active GameTest helper
     */
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void portableTemplateLoads(final GameTestHelper helper) {
        if (!PortableTemplateMod.MOD_ID.equals("portable_template")) {
            throw new IllegalStateException("Portable template mod id changed unexpectedly.");
        }
        helper.succeed();
    }
}
