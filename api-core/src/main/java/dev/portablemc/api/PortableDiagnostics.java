/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Low-noise diagnostic summary for Portable API runtime state.
 */
@PublicApi
@Since("1.1.0")
public final class PortableDiagnostics {
    private final PortableModContext context;

    /**
     * Creates diagnostics for a context.
     *
     * @param context context
     */
    public PortableDiagnostics(final PortableModContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    /**
     * Builds stable diagnostic lines. The summary intentionally avoids user
     * paths beyond directory categories and does not include secrets.
     *
     * @return diagnostic lines
     */
    public List<String> summaryLines() {
        PlatformInfo platform = context.platform();
        PortablePlatformServices services = context.platformServices();
        List<String> lines = new ArrayList<>();
        lines.add("Portable API context: modId=" + context.modId());
        lines.add("Loader=" + platform.loader() + ", Minecraft=" + platform.minecraftVersion().id() + ", side=" + platform.side());
        lines.add("Java=" + Runtime.version());
        lines.add("Config directory available=" + (services.configDirectory() != null));
        lines.add("Game directory available=" + services.gameDirectory().isPresent());
        lines.add("Generated resources directory available=" + services.generatedResourcesDirectory().isPresent());
        lines.add("Development environment=" + services.isDevelopmentEnvironment().map(Object::toString).orElse("unknown"));
        lines.add("Portable subsystems=content, commands, config, lifecycle, networking, datagen");
        return List.copyOf(lines);
    }

    /**
     * Logs the summary at info level.
     */
    public void logSummary() {
        summaryLines().forEach(context.logger()::info);
    }
}
