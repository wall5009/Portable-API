/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.datagen;

import dev.portablemc.api.PortableIdentifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

final class PortableJson {
    private PortableJson() {
    }

    static String quote(final String value) {
        StringBuilder builder = new StringBuilder("\"");
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> {
                    if (character < 0x20) {
                        builder.append(String.format("\\u%04x", (int) character));
                    } else {
                        builder.append(character);
                    }
                }
            }
        }
        return builder.append('"').toString();
    }

    static String object(final Map<String, String> entries) {
        StringJoiner joiner = new StringJoiner("," + System.lineSeparator(), "{" + System.lineSeparator(), System.lineSeparator() + "}");
        entries.forEach((key, value) -> joiner.add("  " + quote(key) + ": " + value));
        return joiner + System.lineSeparator();
    }

    static String array(final Collection<String> values) {
        StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
        values.forEach(value -> joiner.add(value));
        return joiner.toString();
    }

    static String id(final PortableIdentifier identifier) {
        return quote(Objects.requireNonNull(identifier, "identifier").asString());
    }
}
