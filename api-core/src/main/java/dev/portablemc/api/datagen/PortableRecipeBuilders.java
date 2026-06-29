/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * Builders for common deterministic recipe JSON files.
 */
@PublicApi
@Since("1.1.0")
public final class PortableRecipeBuilders {
    private PortableRecipeBuilders() {
    }

    /**
     * Builds a shaped crafting recipe.
     *
     * @param pattern recipe pattern rows
     * @param keys symbol-to-ingredient ids
     * @param result result item
     * @param count result count
     * @return JSON text
     */
    public static String shaped(
            final List<String> pattern,
            final Map<Character, PortableIdentifier> keys,
            final PortableIdentifier result,
            final int count
    ) {
        validateCount(count);
        if (pattern.isEmpty() || pattern.size() > 3 || pattern.stream().anyMatch(row -> row.isEmpty() || row.length() > 3)) {
            throw new IllegalArgumentException("Shaped recipe patterns must contain one to three rows of one to three characters");
        }
        Map<String, String> keyJson = new TreeMap<>();
        keys.forEach((symbol, id) -> keyJson.put(String.valueOf(symbol), "{ \"item\": " + PortableJson.id(id) + " }"));
        return "{\n"
                + "  \"type\": \"minecraft:crafting_shaped\",\n"
                + "  \"pattern\": " + quotedArray(pattern) + ",\n"
                + "  \"key\": " + inlineObject(keyJson) + ",\n"
                + "  \"result\": { \"item\": " + PortableJson.id(result) + ", \"count\": " + count + " }\n"
                + "}\n";
    }

    /**
     * Builds a shapeless crafting recipe.
     *
     * @param ingredients ingredient item ids
     * @param result result item
     * @param count result count
     * @return JSON text
     */
    public static String shapeless(
            final List<PortableIdentifier> ingredients,
            final PortableIdentifier result,
            final int count
    ) {
        validateCount(count);
        if (ingredients.isEmpty() || ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes require one to nine ingredients");
        }
        StringJoiner ingredientJson = new StringJoiner(", ", "[ ", " ]");
        ingredients.forEach(id -> ingredientJson.add("{ \"item\": " + PortableJson.id(id) + " }"));
        return "{\n"
                + "  \"type\": \"minecraft:crafting_shapeless\",\n"
                + "  \"ingredients\": " + ingredientJson + ",\n"
                + "  \"result\": { \"item\": " + PortableJson.id(result) + ", \"count\": " + count + " }\n"
                + "}\n";
    }

    /**
     * Builds a smelting-family recipe such as smelting, blasting, smoking, or
     * campfire cooking.
     *
     * @param type full recipe type id
     * @param ingredient ingredient item
     * @param result result item
     * @param experience experience
     * @param cookingTime cooking time in ticks
     * @return JSON text
     */
    public static String smeltingFamily(
            final PortableIdentifier type,
            final PortableIdentifier ingredient,
            final PortableIdentifier result,
            final double experience,
            final int cookingTime
    ) {
        if (!Double.isFinite(experience) || experience < 0.0D || cookingTime < 1) {
            throw new IllegalArgumentException("Smelting recipe experience and cooking time must be non-negative and positive");
        }
        return "{\n"
                + "  \"type\": " + PortableJson.id(type) + ",\n"
                + "  \"ingredient\": { \"item\": " + PortableJson.id(ingredient) + " },\n"
                + "  \"result\": " + PortableJson.id(result) + ",\n"
                + "  \"experience\": " + experience + ",\n"
                + "  \"cookingtime\": " + cookingTime + "\n"
                + "}\n";
    }

    private static String quotedArray(final List<String> values) {
        StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
        values.forEach(value -> joiner.add(PortableJson.quote(value)));
        return joiner.toString();
    }

    private static String inlineObject(final Map<String, String> values) {
        Map<String, String> copy = new LinkedHashMap<>(values);
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
        copy.forEach((key, value) -> joiner.add(PortableJson.quote(key) + ": " + value));
        return joiner.toString();
    }

    private static void validateCount(final int count) {
        if (count < 1 || count > 64) {
            throw new IllegalArgumentException("Recipe result count must be between 1 and 64: " + count);
        }
    }
}
