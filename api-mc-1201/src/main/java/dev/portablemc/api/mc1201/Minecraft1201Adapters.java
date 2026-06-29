/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.mc1201;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.content.PortableBlockDefinition;
import dev.portablemc.api.content.PortableCreativeTabKey;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.content.PortableItemSettings;
import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Minecraft 1.20.1 object factories used by loader modules.
 *
 * <p>This class is version-specific on purpose. Minecraft 1.21 changed several
 * construction helpers, including resource-location creation, so common code
 * that touches Minecraft classes belongs in these modules instead of the pure
 * core API.</p>
 */
public final class Minecraft1201Adapters {
    private Minecraft1201Adapters() {
    }

    /**
     * Converts a portable id to a 1.20.1 resource location.
     *
     * @param id portable id
     * @return Minecraft resource location
     */
    public static ResourceLocation resourceLocation(final PortableIdentifier id) {
        Objects.requireNonNull(id, "id");
        return new ResourceLocation(id.namespace(), id.path());
    }

    /**
     * Maps the portable creative-tab subset to vanilla 1.20.1 tab keys.
     *
     * @param tab portable tab
     * @return vanilla creative tab key
     */
    public static ResourceKey<CreativeModeTab> creativeTabKey(final PortableCreativeTabKey tab) {
        return switch (Objects.requireNonNull(tab, "tab")) {
            case BUILDING_BLOCKS -> creativeTabKey("building_blocks");
            case NATURAL_BLOCKS -> creativeTabKey("natural_blocks");
            case FUNCTIONAL_BLOCKS -> creativeTabKey("functional_blocks");
            case INGREDIENTS -> creativeTabKey("ingredients");
            case TOOLS_AND_UTILITIES -> creativeTabKey("tools_and_utilities");
        };
    }

    /**
     * Creates a simple block from a portable declaration.
     *
     * @param definition block declaration
     * @return Minecraft block
     */
    public static Block createBlock(final PortableBlockDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .strength(definition.settings().hardness(), definition.settings().resistance());
        if (definition.settings().requiresCorrectTool()) {
            properties.requiresCorrectToolForDrops();
        }
        return new Block(properties);
    }

    /**
     * Creates a standalone item from a portable declaration.
     *
     * @param definition item declaration
     * @return Minecraft item
     */
    public static Item createItem(final PortableItemDefinition definition) {
        return new Item(itemProperties(definition.settings()));
    }

    /**
     * Creates a block item from a portable declaration.
     *
     * @param block owning block
     * @param definition item declaration
     * @return Minecraft block item
     */
    public static BlockItem createBlockItem(final Block block, final PortableItemDefinition definition) {
        return new BlockItem(Objects.requireNonNull(block, "block"), itemProperties(definition.settings()));
    }

    private static Item.Properties itemProperties(final PortableItemSettings settings) {
        Objects.requireNonNull(settings, "settings");
        Item.Properties properties = new Item.Properties();
        if (settings.maxStackSize() != 64) {
            properties.stacksTo(settings.maxStackSize());
        }
        if (settings.fireResistant()) {
            properties.fireResistant();
        }
        return properties;
    }

    private static ResourceKey<CreativeModeTab> creativeTabKey(final String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("minecraft", path));
    }
}
