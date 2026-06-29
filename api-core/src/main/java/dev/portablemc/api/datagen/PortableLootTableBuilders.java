/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Builders for simple loot table JSON files.
 */
@PublicApi
@Since("1.1.0")
public final class PortableLootTableBuilders {
    private PortableLootTableBuilders() {
    }

    /**
     * Builds a simple self-dropping block loot table.
     *
     * @param block block id
     * @return JSON text
     */
    public static String selfDroppingBlock(final PortableIdentifier block) {
        return "{\n"
                + "  \"type\": \"minecraft:block\",\n"
                + "  \"pools\": [ {\n"
                + "    \"rolls\": 1,\n"
                + "    \"entries\": [ { \"type\": \"minecraft:item\", \"name\": " + PortableJson.id(block) + " } ],\n"
                + "    \"conditions\": [ { \"condition\": \"minecraft:survives_explosion\" } ]\n"
                + "  } ]\n"
                + "}\n";
    }
}
