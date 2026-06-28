/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package dev.portablemc.api.fabric1211;

import com.mojang.brigadier.CommandDispatcher;
import dev.portablemc.api.LoaderKind;
import dev.portablemc.api.MinecraftVersion;
import dev.portablemc.api.PlatformInfo;
import dev.portablemc.api.PortableLifecycleEvents;
import dev.portablemc.api.PortableLogger;
import dev.portablemc.api.PortableMod;
import dev.portablemc.api.PortableServerContext;
import dev.portablemc.api.RuntimeSide;
import dev.portablemc.api.command.PortableCommand;
import dev.portablemc.api.command.PortableCommandContext;
import dev.portablemc.api.command.PortableCommandManager;
import dev.portablemc.api.config.PortableConfigManager;
import dev.portablemc.api.content.PortableBlockDefinition;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableContentRegistry;
import dev.portablemc.api.content.PortableCreativeTabEntry;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.content.PortableRegistryHandle;
import dev.portablemc.api.internal.DefaultPortableModContext;
import dev.portablemc.api.mc1211.Minecraft1211Adapters;
import dev.portablemc.api.network.PortableNetworkChannel;
import dev.portablemc.api.network.PortableNetworking;
import dev.portablemc.api.spi.PortableCommandAdapter;
import dev.portablemc.api.spi.PortableContentAdapter;
import dev.portablemc.api.spi.PortableNetworkingAdapter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;

/**
 * Fabric bootstrap for Minecraft 1.21.1 mods using Portable API.
 */
public final class Fabric1211Bootstrap {
    private static final Map<String, DefaultPortableModContext> CONTEXTS = new ConcurrentHashMap<>();

    private Fabric1211Bootstrap() {
    }

    /**
     * Initializes a shared mod from a Fabric {@code ModInitializer}.
     *
     * @param modId owning mod id
     * @param mod shared mod initializer
     */
    public static void initialize(final String modId, final PortableMod mod) {
        Objects.requireNonNull(mod, "mod");
        FabricRuntime runtime = new FabricRuntime(modId);
        DefaultPortableModContext context = createContext(modId, runtime);
        runtime.installEventBridges(context);
        mod.initialize(context);
        context.lifecycle().fireCommonSetup();
        CONTEXTS.put(modId, context);
    }

    /**
     * Fires Portable API client setup from a Fabric {@code ClientModInitializer}.
     *
     * @param modId owning mod id
     */
    public static void initializeClient(final String modId) {
        DefaultPortableModContext context = CONTEXTS.get(modId);
        if (context == null) {
            throw new IllegalStateException("Portable API client setup ran before common initialization for " + modId);
        }
        context.lifecycle().fireClientSetup();
    }

    private static DefaultPortableModContext createContext(final String modId, final FabricRuntime runtime) {
        FabricLoader loader = FabricLoader.getInstance();
        RuntimeSide side = loader.getEnvironmentType() == EnvType.CLIENT ? RuntimeSide.CLIENT : RuntimeSide.DEDICATED_SERVER;
        PlatformInfo platform = new PlatformInfo(
                LoaderKind.FABRIC,
                MinecraftVersion.MC_1_21_1,
                side,
                Optional.of(loader.getGameDir()),
                loader.getConfigDir()
        );
        PortableLifecycleEvents lifecycle = new PortableLifecycleEvents();
        return new DefaultPortableModContext(
                modId,
                platform,
                new SystemPortableLogger(modId),
                lifecycle,
                new PortableContentRegistry(modId, runtime),
                new PortableCommandManager(runtime),
                new PortableConfigManager(platform.configDirectory()),
                new PortableNetworking(modId, runtime)
        );
    }

    private static final class FabricRuntime implements PortableContentAdapter, PortableCommandAdapter, PortableNetworkingAdapter {
        private final String modId;
        private final Map<dev.portablemc.api.PortableIdentifier, Item> items = new ConcurrentHashMap<>();
        private final List<PortableCommand> commands = new ArrayList<>();

        private FabricRuntime(final String modId) {
            this.modId = Objects.requireNonNull(modId, "modId");
        }

        private void installEventBridges(final DefaultPortableModContext context) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                Path worldPath = server.getWorldPath(LevelResource.ROOT);
                context.lifecycle().fireServerStarting(new PortableServerContext(worldPath));
            });
        }

        @Override
        public PortableBlockRegistration registerSimpleBlock(
                final PortableBlockDefinition blockDefinition,
                final PortableItemDefinition itemDefinition
        ) {
            Block block = Minecraft1211Adapters.createBlock(blockDefinition);
            Registry.register(BuiltInRegistries.BLOCK, Minecraft1211Adapters.resourceLocation(blockDefinition.id()), block);
            Item item = Minecraft1211Adapters.createBlockItem(block, itemDefinition);
            Registry.register(BuiltInRegistries.ITEM, Minecraft1211Adapters.resourceLocation(itemDefinition.id()), item);
            items.put(itemDefinition.id(), item);
            return new PortableBlockRegistration(
                    new PortableRegistryHandle<>(blockDefinition.id(), PortableBlockDefinition.class),
                    new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class)
            );
        }

        @Override
        public PortableRegistryHandle<PortableItemDefinition> registerItem(final PortableItemDefinition itemDefinition) {
            Item item = Minecraft1211Adapters.createItem(itemDefinition);
            Registry.register(BuiltInRegistries.ITEM, Minecraft1211Adapters.resourceLocation(itemDefinition.id()), item);
            items.put(itemDefinition.id(), item);
            return new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class);
        }

        @Override
        public void addCreativeTabEntry(final PortableCreativeTabEntry entry) {
            Item item = items.get(entry.itemId());
            if (item == null) {
                throw new IllegalStateException("Creative tab entry references an item that has not been registered: " + entry.itemId());
            }
            ItemGroupEvents.modifyEntriesEvent(Minecraft1211Adapters.creativeTabKey(entry.tab())).register(entries -> entries.accept(item));
        }

        @Override
        public void register(final PortableCommand command) {
            commands.add(command);
        }

        @Override
        public void declare(final PortableNetworkChannel channel) {
            System.getLogger(modId).log(System.Logger.Level.DEBUG, "Declared portable network channel " + channel.id());
        }

        private void registerCommands(final CommandDispatcher<CommandSourceStack> dispatcher) {
            for (PortableCommand command : commands) {
                dispatcher.register(Commands.literal(command.name())
                        .requires(source -> source.hasPermission(command.permissionLevel()))
                        .executes(context -> {
                            PortableCommandContext portableContext = message ->
                                    context.getSource().sendSystemMessage(Component.literal(message));
                            return command.executor().run(portableContext);
                        }));
            }
        }
    }

    private static final class SystemPortableLogger implements PortableLogger {
        private final System.Logger logger;

        private SystemPortableLogger(final String name) {
            this.logger = System.getLogger(name);
        }

        @Override
        public void debug(final String message) {
            logger.log(System.Logger.Level.DEBUG, message);
        }

        @Override
        public void info(final String message) {
            logger.log(System.Logger.Level.INFO, message);
        }

        @Override
        public void warn(final String message) {
            logger.log(System.Logger.Level.WARNING, message);
        }

        @Override
        public void error(final String message, final Throwable throwable) {
            logger.log(System.Logger.Level.ERROR, message, throwable);
        }
    }
}
