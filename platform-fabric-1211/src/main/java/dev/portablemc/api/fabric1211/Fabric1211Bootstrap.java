/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.fabric1211;

import com.mojang.brigadier.CommandDispatcher;
import dev.portablemc.api.LoaderKind;
import dev.portablemc.api.MinecraftVersion;
import dev.portablemc.api.PlatformInfo;
import dev.portablemc.api.PortableLifecycleEvents;
import dev.portablemc.api.PortableLogger;
import dev.portablemc.api.PortableMod;
import dev.portablemc.api.PortablePlatformServices;
import dev.portablemc.api.PortableServerContext;
import dev.portablemc.api.RuntimeSide;
import dev.portablemc.api.command.PortableCommand;
import dev.portablemc.api.command.PortableCommandManager;
import dev.portablemc.api.command.PortableCommandTree;
import dev.portablemc.api.config.PortableConfigManager;
import dev.portablemc.api.content.PortableBlockDefinition;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableContentRegistry;
import dev.portablemc.api.content.PortableCreativeTabEntry;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.content.PortableRegistryHandle;
import dev.portablemc.api.internal.DefaultPortableModContext;
import dev.portablemc.api.mc1211.Minecraft1211CommandAdapters;
import dev.portablemc.api.mc1211.Minecraft1211Adapters;
import dev.portablemc.api.network.PortableNetworkChannel;
import dev.portablemc.api.network.PortablePacketRegistration;
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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
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
        PortablePlatformServices platformServices = PortablePlatformServices.builder(platform)
                .modLoaded(loader::isModLoaded)
                .modVersion(id -> loader.getModContainer(id)
                        .map(container -> container.getMetadata().getVersion().getFriendlyString()))
                .developmentEnvironment(loader.isDevelopmentEnvironment())
                .build();
        PortableLifecycleEvents lifecycle = new PortableLifecycleEvents();
        return new DefaultPortableModContext(
                modId,
                platform,
                new SystemPortableLogger(modId),
                lifecycle,
                new PortableContentRegistry(modId, runtime),
                new PortableCommandManager(runtime),
                new PortableConfigManager(platform.configDirectory()),
                new PortableNetworking(modId, runtime),
                platformServices
        );
    }

    private static final class FabricRuntime implements PortableContentAdapter, PortableCommandAdapter, PortableNetworkingAdapter {
        private final String modId;
        private final Map<dev.portablemc.api.PortableIdentifier, Item> items = new ConcurrentHashMap<>();
        private final List<PortableCommand> commands = new ArrayList<>();
        private final List<PortableCommandTree> commandTrees = new ArrayList<>();
        private final List<PortablePacketRegistration<?>> packetRegistrations = new ArrayList<>();

        private FabricRuntime(final String modId) {
            this.modId = Objects.requireNonNull(modId, "modId");
        }

        private void installEventBridges(final DefaultPortableModContext context) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
            ServerLifecycleEvents.SERVER_STARTING.register(server -> context.lifecycle().fireServerStarting(serverContext(server)));
            ServerLifecycleEvents.SERVER_STARTED.register(server -> context.lifecycle().fireServerStarted(serverContext(server)));
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> context.lifecycle().fireServerStopping(serverContext(server)));
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> context.lifecycle().fireServerStopped(serverContext(server)));
            ServerTickEvents.END_SERVER_TICK.register(server -> context.lifecycle().fireServerTick(serverContext(server)));
        }

        private static PortableServerContext serverContext(final MinecraftServer server) {
            Path worldPath = server.getWorldPath(LevelResource.ROOT);
            return new PortableServerContext(worldPath);
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
        public void registerTree(final PortableCommandTree tree) {
            commandTrees.add(tree);
        }

        @Override
        public void declare(final PortableNetworkChannel channel) {
            System.getLogger(modId).log(System.Logger.Level.DEBUG, "Declared portable network channel " + channel.id());
        }

        @Override
        public <T> void registerPacket(final PortablePacketRegistration<T> registration) {
            packetRegistrations.add(registration);
            System.getLogger(modId).log(System.Logger.Level.DEBUG, "Registered portable packet " + registration.type().id());
        }

        private void registerCommands(final CommandDispatcher<CommandSourceStack> dispatcher) {
            for (PortableCommand command : commands) {
                dispatcher.register(Minecraft1211CommandAdapters.literalCommand(command));
            }
            for (PortableCommandTree tree : commandTrees) {
                dispatcher.register(Minecraft1211CommandAdapters.commandTree(tree));
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
