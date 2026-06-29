/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Optional platform queries exposed by loaders that can answer them honestly.
 */
@PublicApi
@Since("1.1.0")
public final class PortablePlatformServices {
    private final PlatformInfo platform;
    private final Function<String, Optional<Boolean>> modLoaded;
    private final Function<String, Optional<String>> modVersion;
    private final Supplier<Optional<Boolean>> developmentEnvironment;
    private final Supplier<Optional<Path>> generatedResourcesDirectory;

    private PortablePlatformServices(
            final PlatformInfo platform,
            final Function<String, Optional<Boolean>> modLoaded,
            final Function<String, Optional<String>> modVersion,
            final Supplier<Optional<Boolean>> developmentEnvironment,
            final Supplier<Optional<Path>> generatedResourcesDirectory
    ) {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.modLoaded = Objects.requireNonNull(modLoaded, "modLoaded");
        this.modVersion = Objects.requireNonNull(modVersion, "modVersion");
        this.developmentEnvironment = Objects.requireNonNull(developmentEnvironment, "developmentEnvironment");
        this.generatedResourcesDirectory = Objects.requireNonNull(generatedResourcesDirectory, "generatedResourcesDirectory");
    }

    /**
     * Creates a services object with no optional loader-specific answers.
     *
     * @param platform platform info
     * @return basic services
     */
    public static PortablePlatformServices basic(final PlatformInfo platform) {
        return builder(platform).build();
    }

    /**
     * Creates a services builder.
     *
     * @param platform platform info
     * @return builder
     */
    public static Builder builder(final PlatformInfo platform) {
        return new Builder(platform);
    }

    /**
     * Returns whether a mod id is loaded, or empty when the loader adapter
     * cannot answer.
     *
     * @param modId mod id
     * @return optional loaded state
     */
    public Optional<Boolean> isModLoaded(final String modId) {
        return modLoaded.apply(Objects.requireNonNull(modId, "modId"));
    }

    /**
     * Returns an installed mod version, or empty when unavailable or not loaded.
     *
     * @param modId mod id
     * @return optional version string
     */
    public Optional<String> installedModVersion(final String modId) {
        return modVersion.apply(Objects.requireNonNull(modId, "modId"));
    }

    /**
     * Returns whether this is a development environment, or empty when unknown.
     *
     * @return optional development-environment state
     */
    public Optional<Boolean> isDevelopmentEnvironment() {
        return developmentEnvironment.get();
    }

    /**
     * Returns the game directory exposed by the platform.
     *
     * @return optional game directory
     */
    public Optional<Path> gameDirectory() {
        return platform.gameDirectory();
    }

    /**
     * Returns the config directory exposed by the platform.
     *
     * @return config directory
     */
    public Path configDirectory() {
        return platform.configDirectory();
    }

    /**
     * Returns a generated-resource directory when the platform adapter has one
     * for the active run mode.
     *
     * @return optional generated-resource directory
     */
    public Optional<Path> generatedResourcesDirectory() {
        return generatedResourcesDirectory.get();
    }

    /**
     * Builder for platform services.
     */
    public static final class Builder {
        private final PlatformInfo platform;
        private Function<String, Optional<Boolean>> modLoaded = ignored -> Optional.empty();
        private Function<String, Optional<String>> modVersion = ignored -> Optional.empty();
        private Supplier<Optional<Boolean>> developmentEnvironment = Optional::empty;
        private Supplier<Optional<Path>> generatedResourcesDirectory = Optional::empty;

        private Builder(final PlatformInfo platform) {
            this.platform = Objects.requireNonNull(platform, "platform");
        }

        /**
         * Sets a mod-loaded query.
         *
         * @param query query
         * @return this builder
         */
        public Builder modLoaded(final Predicate<String> query) {
            Objects.requireNonNull(query, "query");
            this.modLoaded = modId -> Optional.of(query.test(modId));
            return this;
        }

        /**
         * Sets a mod-version query.
         *
         * @param query query
         * @return this builder
         */
        public Builder modVersion(final Function<String, Optional<String>> query) {
            this.modVersion = Objects.requireNonNull(query, "query");
            return this;
        }

        /**
         * Sets development-environment detection.
         *
         * @param value development-environment state
         * @return this builder
         */
        public Builder developmentEnvironment(final boolean value) {
            this.developmentEnvironment = () -> Optional.of(value);
            return this;
        }

        /**
         * Sets the generated-resource directory.
         *
         * @param path generated-resource directory
         * @return this builder
         */
        public Builder generatedResourcesDirectory(final Path path) {
            this.generatedResourcesDirectory = () -> Optional.of(Objects.requireNonNull(path, "path"));
            return this;
        }

        /**
         * Builds the services object.
         *
         * @return platform services
         */
        public PortablePlatformServices build() {
            return new PortablePlatformServices(
                    platform,
                    modLoaded,
                    modVersion,
                    developmentEnvironment,
                    generatedResourcesDirectory
            );
        }
    }
}
