/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.template.forge1201;

import dev.portablemc.template.PortableTemplateMod;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Forge 1.20.1 GameTests for the copyable template mod.
 */
@GameTestHolder(PortableTemplateMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TemplateForge1201GameTests {
    private TemplateForge1201GameTests() {
    }

    /**
     * Verifies that Forge discovers the template GameTest holder and can run a
     * headless server-side GameTest with the template mod loaded.
     *
     * @param helper active GameTest helper
     */
    @GameTest(template = "empty")
    public static void portableTemplateLoads(final GameTestHelper helper) {
        if (!PortableTemplateMod.MOD_ID.equals("portable_template")) {
            throw new IllegalStateException("Portable template mod id changed unexpectedly.");
        }
        helper.succeed();
    }
}
