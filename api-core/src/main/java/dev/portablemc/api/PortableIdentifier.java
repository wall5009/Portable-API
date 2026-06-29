/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Loader-neutral identifier with the same namespace/path shape used by Minecraft.
 *
 * <p>The validation here intentionally follows the conservative subset that is
 * accepted by all supported loaders and resource-pack paths. Rejecting early in
 * common code gives mod authors an actionable error before a loader-specific
 * registry throws a less obvious exception later in startup.</p>
 *
 * @param namespace namespace, normally the mod id
 * @param path object path inside the namespace
 */
public record PortableIdentifier(String namespace, String path) {
    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH = Pattern.compile("[a-z0-9_./-]+");

    /**
     * Validates and stores the identifier.
     */
    public PortableIdentifier {
        Objects.requireNonNull(namespace, "namespace");
        Objects.requireNonNull(path, "path");
        if (!NAMESPACE.matcher(namespace).matches()) {
            throw new IllegalArgumentException("Invalid namespace '" + namespace + "'. Use lowercase letters, digits, '_', '.', or '-'.");
        }
        if (!PATH.matcher(path).matches()) {
            throw new IllegalArgumentException("Invalid path '" + path + "'. Use lowercase letters, digits, '_', '.', '/', or '-'.");
        }
    }

    /**
     * Creates an identifier from a mod id and object path.
     *
     * @param modId owning mod id
     * @param path object path
     * @return validated identifier
     */
    public static PortableIdentifier of(final String modId, final String path) {
        return new PortableIdentifier(normalizeNamespace(modId), path);
    }

    /**
     * Parses {@code namespace:path}. If no namespace is present, the provided
     * default namespace is used.
     *
     * @param text textual id
     * @param defaultNamespace namespace to apply when {@code text} has no colon
     * @return validated identifier
     */
    public static PortableIdentifier parse(final String text, final String defaultNamespace) {
        Objects.requireNonNull(text, "text");
        int separator = text.indexOf(':');
        if (separator >= 0) {
            return new PortableIdentifier(normalizeNamespace(text.substring(0, separator)), text.substring(separator + 1));
        }
        return of(defaultNamespace, text);
    }

    /**
     * Returns the canonical {@code namespace:path} representation.
     *
     * @return stable string form
     */
    public String asString() {
        return namespace + ":" + path;
    }

    @Override
    public String toString() {
        return asString();
    }

    private static String normalizeNamespace(final String namespace) {
        return Objects.requireNonNull(namespace, "namespace").toLowerCase(Locale.ROOT);
    }
}
