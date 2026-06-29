/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.mc1211;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.command.PortableCommand;
import dev.portablemc.api.command.PortableCommandArgumentType;
import dev.portablemc.api.command.PortableCommandContext;
import dev.portablemc.api.command.PortableCommandNode;
import dev.portablemc.api.command.PortableCommandTree;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Minecraft 1.21.1 Brigadier bridge for portable command trees.
 */
public final class Minecraft1211CommandAdapters {
    private Minecraft1211CommandAdapters() {
    }

    /**
     * Converts a v1.0 literal command to Brigadier.
     *
     * @param command command
     * @return literal builder
     */
    public static LiteralArgumentBuilder<CommandSourceStack> literalCommand(final PortableCommand command) {
        Objects.requireNonNull(command, "command");
        return Commands.literal(command.name())
                .requires(source -> source.hasPermission(command.permissionLevel()))
                .executes(context -> command.executor().run(new Context(context)));
    }

    /**
     * Converts a portable command tree to Brigadier.
     *
     * @param tree command tree
     * @return literal builder
     */
    public static LiteralArgumentBuilder<CommandSourceStack> commandTree(final PortableCommandTree tree) {
        return castLiteral(buildNode(Objects.requireNonNull(tree, "tree").root()));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> buildNode(final PortableCommandNode node) {
        ArgumentBuilder<CommandSourceStack, ?> builder;
        if (node.literal()) {
            builder = Commands.literal(node.name());
        } else {
            RequiredArgumentBuilder<CommandSourceStack, ?> argument =
                    Commands.argument(node.name(), argumentType(node.argumentType().orElseThrow()));
            node.suggestions().ifPresent(suggestions -> argument.suggests((context, builder1) -> {
                suggestions.suggest(new Context(context)).forEach(builder1::suggest);
                return builder1.buildFuture();
            }));
            builder = argument;
        }
        builder.requires(source -> source.hasPermission(node.permissionLevel()));
        node.executor().ifPresent(executor -> builder.executes(context -> executor.run(new Context(context))));
        node.children().forEach(child -> builder.then(buildNode(child)));
        return builder;
    }

    private static ArgumentType<?> argumentType(final PortableCommandArgumentType type) {
        return switch (type) {
            case BOOLEAN -> BoolArgumentType.bool();
            case INTEGER -> IntegerArgumentType.integer();
            case LONG -> LongArgumentType.longArg();
            case DOUBLE -> DoubleArgumentType.doubleArg();
            case WORD -> StringArgumentType.word();
            case GREEDY_STRING -> StringArgumentType.greedyString();
            case IDENTIFIER -> ResourceLocationArgument.id();
        };
    }

    private static LiteralArgumentBuilder<CommandSourceStack> castLiteral(
            final ArgumentBuilder<CommandSourceStack, ?> builder
    ) {
        @SuppressWarnings("unchecked")
        LiteralArgumentBuilder<CommandSourceStack> literal = (LiteralArgumentBuilder<CommandSourceStack>) builder;
        return literal;
    }

    private static final class Context implements PortableCommandContext {
        private final CommandContext<CommandSourceStack> context;

        private Context(final CommandContext<CommandSourceStack> context) {
            this.context = context;
        }

        @Override
        public void reply(final String message) {
            context.getSource().sendSystemMessage(Component.literal(message));
        }

        @Override
        public Optional<String> sourceName() {
            return Optional.of(context.getSource().getTextName());
        }

        @Override
        public boolean hasPermission(final int level) {
            return context.getSource().hasPermission(level);
        }

        @Override
        public <T> T argument(final String name, final Class<T> type) {
            if (type == PortableIdentifier.class) {
                ResourceLocation id = ResourceLocationArgument.getId(context, name);
                return type.cast(new PortableIdentifier(id.getNamespace(), id.getPath()));
            }
            return context.getArgument(name, type);
        }
    }
}
