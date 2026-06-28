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
package dev.portablemc.template;

import dev.portablemc.api.PortableMod;
import dev.portablemc.api.PortableModContext;
import dev.portablemc.api.config.PortableConfigSpec;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableBlockSettings;
import dev.portablemc.api.content.PortableCreativeTabKey;
import dev.portablemc.api.content.PortableItemSettings;
import dev.portablemc.api.content.PortableRegistryHandle;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.network.NetworkPhase;

/**
 * The entire shared implementation of the template mod.
 *
 * <p>The class intentionally contains only the minimum needed to prove the
 * repository layout: content registration, creative-tab entries, a command, a
 * config file, lifecycle hooks, a network channel declaration, and generated
 * resources. Real projects should copy this module and grow from here.</p>
 */
public final class PortableTemplateMod implements PortableMod {
    /** Stable mod id used by every loader target. */
    public static final String MOD_ID = "portable_template";

    @Override
    public void initialize(final PortableModContext context) {
        PortableBlockRegistration block = context.content().registerSimpleBlock(
                "portable_block",
                PortableBlockSettings.stoneLike(),
                PortableItemSettings.defaults()
        );
        PortableRegistryHandle<PortableItemDefinition> item =
                context.content().registerItem("portable_item", PortableItemSettings.defaults());

        context.content().addToCreativeTab(PortableCreativeTabKey.BUILDING_BLOCKS, block.item());
        context.content().addToCreativeTab(PortableCreativeTabKey.INGREDIENTS, item);

        context.commands().registerLiteral("portable_template", 2, commandContext -> {
            commandContext.reply("Portable Template is running on "
                    + context.platform().loader() + " " + context.platform().minecraftVersion().id());
            return 1;
        });

        context.config().register(new PortableConfigSpec(
                MOD_ID + ".properties",
                "# Portable Template configuration\nexampleValue=true\n"
        ));

        context.networking().declareChannel("main", NetworkPhase.PLAY, 1, 32767);

        context.lifecycle().onCommonSetup(() -> context.logger().info("Portable Template common setup completed."));
        context.lifecycle().onClientSetup(() -> context.logger().debug("Portable Template client setup completed."));
        context.lifecycle().onServerStarting(server -> context.logger().info("Portable Template server starting at "
                + server.worldDirectory()));
        context.lifecycle().onDataGeneration(data -> {
            data.writeAsset("lang/en_us.json",
                    "{\n"
                            + "  \"block.portable_template.portable_block\": \"Portable Block\",\n"
                            + "  \"item.portable_template.portable_item\": \"Portable Item\"\n"
                            + "}\n");
            data.writeData("tags/blocks/mineable/pickaxe.json",
                    "{\n"
                            + "  \"replace\": false,\n"
                            + "  \"values\": [ \"portable_template:portable_block\" ]\n"
                            + "}\n");
        });
    }
}
