/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.List;

/**
 * Computes portable command suggestions for an argument.
 */
@FunctionalInterface
@PublicApi
@Since("1.1.0")
public interface PortableCommandSuggestions {
    /**
     * Returns candidate suggestions.
     *
     * @param context command context
     * @return immutable or mutable suggestion list
     */
    List<String> suggest(PortableCommandContext context);
}
