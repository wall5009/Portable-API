/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Map;
import java.util.Objects;

/**
 * Builders for simple item and block model JSON.
 */
@PublicApi
@Since("1.1.0")
public final class PortableModelBuilders {
    private PortableModelBuilders() {
    }

    /**
     * Creates an item model that uses {@code minecraft:item/generated}.
     *
     * @param layer0 layer 0 texture id
     * @return JSON text
     */
    public static String generatedItem(final PortableIdentifier layer0) {
        Objects.requireNonNull(layer0, "layer0");
        return PortableJson.object(Map.of(
                "parent", PortableJson.quote("minecraft:item/generated"),
                "textures", "{ \"layer0\": " + PortableJson.id(layer0) + " }"
        ));
    }

    /**
     * Creates an item model that references a block model.
     *
     * @param blockModel block model id
     * @return JSON text
     */
    public static String blockItem(final PortableIdentifier blockModel) {
        return PortableJson.object(Map.of("parent", PortableJson.id(blockModel)));
    }

    /**
     * Creates a cube-all block model.
     *
     * @param texture block texture id
     * @return JSON text
     */
    public static String cubeAllBlock(final PortableIdentifier texture) {
        Objects.requireNonNull(texture, "texture");
        return PortableJson.object(Map.of(
                "parent", PortableJson.quote("minecraft:block/cube_all"),
                "textures", "{ \"all\": " + PortableJson.id(texture) + " }"
        ));
    }

    /**
     * Creates a simple single-variant blockstate.
     *
     * @param model block model id
     * @return JSON text
     */
    public static String simpleBlockState(final PortableIdentifier model) {
        return PortableJson.object(Map.of(
                "variants", "{ \"\": { \"model\": " + PortableJson.id(model) + " } }"
        ));
    }
}
