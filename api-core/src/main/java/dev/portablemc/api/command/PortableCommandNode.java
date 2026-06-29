/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable node in a portable command tree.
 *
 * @param name literal text or argument name
 * @param argumentType empty for literal nodes
 * @param permissionLevel vanilla permission level from 0 through 4
 * @param executor optional executor
 * @param suggestions optional suggestions for argument nodes
 * @param children child nodes
 */
@PublicApi
@Since("1.1.0")
public record PortableCommandNode(
        String name,
        Optional<PortableCommandArgumentType> argumentType,
        int permissionLevel,
        Optional<PortableCommandExecutor> executor,
        Optional<PortableCommandSuggestions> suggestions,
        List<PortableCommandNode> children
) {
    /**
     * Validates a command node.
     */
    public PortableCommandNode {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(argumentType, "argumentType");
        Objects.requireNonNull(executor, "executor");
        Objects.requireNonNull(suggestions, "suggestions");
        children = List.copyOf(Objects.requireNonNull(children, "children"));
        if (!name.matches("[a-zA-Z0-9_./-]+")) {
            throw new IllegalArgumentException("Command node name contains unsupported characters: " + name);
        }
        if (permissionLevel < 0 || permissionLevel > 4) {
            throw new IllegalArgumentException("permissionLevel must be between 0 and 4: " + permissionLevel);
        }
        Set<String> childKeys = new HashSet<>();
        for (PortableCommandNode child : children) {
            String key = child.argumentType().map(type -> "$" + child.name()).orElse(child.name());
            if (!childKeys.add(key)) {
                throw new IllegalArgumentException("Duplicate child command node under " + name + ": " + child.name());
            }
        }
    }

    /**
     * Returns true for literal nodes.
     *
     * @return whether this node is literal
     */
    public boolean literal() {
        return argumentType.isEmpty();
    }

    /**
     * Starts a literal-node builder.
     *
     * @param name literal name
     * @return builder
     */
    public static Builder literal(final String name) {
        return new Builder(name, Optional.empty());
    }

    /**
     * Starts an argument-node builder.
     *
     * @param name argument name
     * @param type argument type
     * @return builder
     */
    public static Builder argument(final String name, final PortableCommandArgumentType type) {
        return new Builder(name, Optional.of(Objects.requireNonNull(type, "type")));
    }

    /**
     * Mutable builder for a command node.
     */
    public static final class Builder {
        private final String name;
        private final Optional<PortableCommandArgumentType> argumentType;
        private int permissionLevel;
        private PortableCommandExecutor executor;
        private PortableCommandSuggestions suggestions;
        private final List<Builder> children = new ArrayList<>();

        private Builder(final String name, final Optional<PortableCommandArgumentType> argumentType) {
            this.name = Objects.requireNonNull(name, "name");
            this.argumentType = Objects.requireNonNull(argumentType, "argumentType");
        }

        /**
         * Sets the vanilla permission level required from this node downward.
         *
         * @param value permission level from 0 through 4
         * @return this builder
         */
        public Builder requiresPermission(final int value) {
            this.permissionLevel = value;
            return this;
        }

        /**
         * Sets the executor for this node.
         *
         * @param value executor
         * @return this builder
         */
        public Builder executes(final PortableCommandExecutor value) {
            this.executor = Objects.requireNonNull(value, "value");
            return this;
        }

        /**
         * Sets suggestions for an argument node.
         *
         * @param value suggestions provider
         * @return this builder
         */
        public Builder suggests(final PortableCommandSuggestions value) {
            this.suggestions = Objects.requireNonNull(value, "value");
            return this;
        }

        /**
         * Adds a child literal and returns it.
         *
         * @param childName child literal name
         * @return child builder
         */
        public Builder thenLiteral(final String childName) {
            Builder child = PortableCommandNode.literal(childName);
            children.add(child);
            return child;
        }

        /**
         * Adds a child argument and returns it.
         *
         * @param childName argument name
         * @param type argument type
         * @return child builder
         */
        public Builder thenArgument(final String childName, final PortableCommandArgumentType type) {
            Builder child = PortableCommandNode.argument(childName, type);
            children.add(child);
            return child;
        }

        /**
         * Adds an existing child builder.
         *
         * @param child child builder
         * @return this builder
         */
        public Builder then(final Builder child) {
            children.add(Objects.requireNonNull(child, "child"));
            return this;
        }

        /**
         * Builds the immutable node and all children.
         *
         * @return immutable node
         */
        public PortableCommandNode build() {
            List<PortableCommandNode> builtChildren = children.stream()
                    .map(Builder::build)
                    .toList();
            return new PortableCommandNode(
                    name,
                    argumentType,
                    permissionLevel,
                    Optional.ofNullable(executor),
                    Optional.ofNullable(suggestions),
                    builtChildren
            );
        }
    }
}
