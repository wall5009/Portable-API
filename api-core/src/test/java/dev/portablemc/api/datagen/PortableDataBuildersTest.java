/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.portablemc.api.PortableIdentifier;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for deterministic structured data builders.
 */
final class PortableDataBuildersTest {
    @Test
    void languageBuilderSortsAndRejectsDuplicates() {
        PortableLanguageBuilder builder = new PortableLanguageBuilder()
                .add("item.example.b", "B")
                .add("item.example.a", "A");

        String json = builder.toJson();

        assertTrue(json.indexOf("item.example.a") < json.indexOf("item.example.b"));
        assertThrows(IllegalStateException.class, () -> builder.add("item.example.a", "Again"));
    }

    @Test
    void tagBuilderSortsAndRejectsDuplicates() {
        PortableTagBuilder builder = new PortableTagBuilder()
                .add(PortableIdentifier.of("example", "b"))
                .add(PortableIdentifier.of("example", "a"));

        String json = builder.toJson();

        assertTrue(json.indexOf("example:a") < json.indexOf("example:b"));
        assertThrows(IllegalStateException.class, () -> builder.add(PortableIdentifier.of("example", "a")));
    }

    @Test
    void recipeBuildersValidateBounds() {
        assertThrows(IllegalArgumentException.class, () -> PortableRecipeBuilders.shapeless(
                List.of(),
                PortableIdentifier.of("example", "result"),
                1
        ));
        String shaped = PortableRecipeBuilders.shaped(
                List.of("A"),
                Map.of('A', PortableIdentifier.of("example", "ingredient")),
                PortableIdentifier.of("example", "result"),
                1
        );
        assertTrue(shaped.contains("minecraft:crafting_shaped"));
    }

    @Test
    void modelAndPackBuildersProduceStableJson() {
        assertTrue(PortableModelBuilders.generatedItem(PortableIdentifier.of("example", "item")).contains("minecraft:item/generated"));
        assertEquals("{\n"
                        + "  \"pack\": {\n"
                        + "    \"pack_format\": 15,\n"
                        + "    \"description\": \"Example\"\n"
                        + "  }\n"
                        + "}\n",
                new PortablePackMetadataBuilder().packFormat(15).description("Example").toJson());
    }
}
