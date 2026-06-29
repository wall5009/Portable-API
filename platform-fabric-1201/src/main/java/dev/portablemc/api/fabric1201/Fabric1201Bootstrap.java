/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.fabric1201;

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
import dev.portablemc.api.command.PortableCommandTree;
import dev.portablemc.api.command.PortableCommandManager;
import dev.portablemc.api.config.PortableConfigManager;
import dev.portablemc.api.content.PortableBlockDefinition;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableContentRegistry;
import dev.portablemc.api.content.PortableCreativeTabEntry;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.content.PortableRegistryHandle;
import dev.portablemc.api.internal.DefaultPortableModContext;
import dev.portablemc.api.mc1201.Minecraft1201CommandAdapters;
import dev.portablemc.api.mc1201.Minecraft1201Adapters;
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PortableEncodedPacket;
import dev.portablemc.api.network.PortableNetworkChannel;
import dev.portablemc.api.network.PortablePacketBuffer;
import dev.portablemc.api.network.PortablePacketContext;
import dev.portablemc.api.network.PortablePacketException;
import dev.portablemc.api.network.PortablePacketRegistration;
import dev.portablemc.api.network.PortablePacketSender;
import dev.portablemc.api.network.PortablePacketType;
import dev.portablemc.api.network.PortablePacketWireFormat;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;

/**
 * Fabric bootstrap for Minecraft 1.20.1 mods using Portable API.
 */
public final class Fabric1201Bootstrap {
    private static final Map<String, DefaultPortableModContext> CONTEXTS = new ConcurrentHashMap<>();
    private static final Map<String, FabricRuntime> RUNTIMES = new ConcurrentHashMap<>();

    private Fabric1201Bootstrap() {
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
        RUNTIMES.put(modId, runtime);
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
        Fabric1201ClientBridge.install(context, RUNTIMES.get(modId));
    }

    private static DefaultPortableModContext createContext(final String modId, final FabricRuntime runtime) {
        FabricLoader loader = FabricLoader.getInstance();
        RuntimeSide side = loader.getEnvironmentType() == EnvType.CLIENT ? RuntimeSide.CLIENT : RuntimeSide.DEDICATED_SERVER;
        PlatformInfo platform = new PlatformInfo(
                LoaderKind.FABRIC,
                MinecraftVersion.MC_1_20_1,
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

    static final class FabricRuntime implements PortableContentAdapter, PortableCommandAdapter, PortableNetworkingAdapter {
        private final String modId;
        private final Map<dev.portablemc.api.PortableIdentifier, Item> items = new ConcurrentHashMap<>();
        private final List<PortableCommand> commands = new ArrayList<>();
        private final List<PortableCommandTree> commandTrees = new ArrayList<>();
        private final List<PortablePacketRegistration<?>> packetRegistrations = new ArrayList<>();
        private boolean serverStartingFired;
        private boolean serverStartedFired;
        private boolean serverStoppingFired;

        private FabricRuntime(final String modId) {
            this.modId = Objects.requireNonNull(modId, "modId");
        }

        private void installEventBridges(final DefaultPortableModContext context) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
            ServerLifecycleEvents.SERVER_STARTING.register(server -> fireServerStarting(context, server));
            ServerLifecycleEvents.SERVER_STARTED.register(server -> ensureServerStarted(context, server));
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> ensureServerStopping(context, server));
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> fireServerStopped(context, server));
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                ensureServerStarted(context, server);
                context.lifecycle().fireServerTick(serverContext(server));
            });
        }

        private synchronized void fireServerStarting(
                final DefaultPortableModContext context,
                final MinecraftServer server
        ) {
            if (!serverStartingFired) {
                serverStartingFired = true;
                serverStoppingFired = false;
                context.lifecycle().fireServerStarting(serverContext(server));
            }
        }

        private synchronized void ensureServerStarted(
                final DefaultPortableModContext context,
                final MinecraftServer server
        ) {
            fireServerStarting(context, server);
            if (!serverStartedFired) {
                serverStartedFired = true;
                context.lifecycle().fireServerStarted(serverContext(server));
            }
        }

        private synchronized void ensureServerStopping(
                final DefaultPortableModContext context,
                final MinecraftServer server
        ) {
            if (serverStartedFired && !serverStoppingFired) {
                serverStoppingFired = true;
                context.lifecycle().fireServerStopping(serverContext(server));
            }
        }

        private synchronized void fireServerStopped(
                final DefaultPortableModContext context,
                final MinecraftServer server
        ) {
            if (!serverStartedFired) {
                return;
            }
            ensureServerStopping(context, server);
            context.lifecycle().fireServerStopped(serverContext(server));
            serverStartingFired = false;
            serverStartedFired = false;
            serverStoppingFired = false;
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
            Block block = Minecraft1201Adapters.createBlock(blockDefinition);
            Registry.register(BuiltInRegistries.BLOCK, Minecraft1201Adapters.resourceLocation(blockDefinition.id()), block);
            Item item = Minecraft1201Adapters.createBlockItem(block, itemDefinition);
            Registry.register(BuiltInRegistries.ITEM, Minecraft1201Adapters.resourceLocation(itemDefinition.id()), item);
            items.put(itemDefinition.id(), item);
            return new PortableBlockRegistration(
                    new PortableRegistryHandle<>(blockDefinition.id(), PortableBlockDefinition.class),
                    new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class)
            );
        }

        @Override
        public PortableRegistryHandle<PortableItemDefinition> registerItem(final PortableItemDefinition itemDefinition) {
            Item item = Minecraft1201Adapters.createItem(itemDefinition);
            Registry.register(BuiltInRegistries.ITEM, Minecraft1201Adapters.resourceLocation(itemDefinition.id()), item);
            items.put(itemDefinition.id(), item);
            return new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class);
        }

        @Override
        public void addCreativeTabEntry(final PortableCreativeTabEntry entry) {
            Item item = items.get(entry.itemId());
            if (item == null) {
                throw new IllegalStateException("Creative tab entry references an item that has not been registered: " + entry.itemId());
            }
            ItemGroupEvents.modifyEntriesEvent(Minecraft1201Adapters.creativeTabKey(entry.tab())).register(entries -> entries.accept(item));
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
            if (registration.type().direction() == PacketDirection.CLIENT_TO_SERVER) {
                registerServerReceiver(registration);
            }
            System.getLogger(modId).log(System.Logger.Level.DEBUG, "Registered portable packet " + registration.type().id());
        }

        @Override
        public void sendToServer(final PortableEncodedPacket packet) {
            Fabric1201ClientBridge.sendToServer(packet);
        }

        List<PortablePacketRegistration<?>> packetRegistrations() {
            return List.copyOf(packetRegistrations);
        }

        <T> void sendToPlayer(final ServerPlayer player, final PortablePacketType<T> type, final T packet) {
            if (type.direction() != PacketDirection.SERVER_TO_CLIENT) {
                throw new IllegalArgumentException("Packet " + type.id() + " is not server-to-client");
            }
            PortableEncodedPacket encoded = new PortableEncodedPacket(
                    type.id(),
                    type.phase(),
                    type.direction(),
                    type.protocolVersion(),
                    PortablePacketBuffer.encode(type, packet)
            );
            FriendlyByteBuf buffer = PacketByteBufs.create();
            buffer.writeBytes(PortablePacketWireFormat.encode(encoded));
            ServerPlayNetworking.send(player, Minecraft1201Adapters.resourceLocation(type.id()), buffer);
        }

        private <T> void registerServerReceiver(final PortablePacketRegistration<T> registration) {
            boolean registered = ServerPlayNetworking.registerGlobalReceiver(
                    Minecraft1201Adapters.resourceLocation(registration.type().id()),
                    (server, player, handler, buffer, responseSender) -> {
                        byte[] wireBytes = readWireBytes(buffer, registration);
                        server.execute(() -> dispatchServerPacket(server, player, registration, wireBytes));
                    }
            );
            if (!registered) {
                throw new IllegalStateException("Duplicate Fabric server packet receiver for " + registration.type().id());
            }
        }

        private <T> void dispatchServerPacket(
                final MinecraftServer server,
                final ServerPlayer player,
                final PortablePacketRegistration<T> registration,
                final byte[] wireBytes
        ) {
            try {
                PortableEncodedPacket encoded = PortablePacketWireFormat.decode(registration, wireBytes);
                registration.dispatch(encoded, new PortablePacketContext(
                        PacketDirection.CLIENT_TO_SERVER,
                        Optional.of(new FabricServerPacketSender(player)),
                        server::execute
                ));
            } catch (PortablePacketException exception) {
                System.getLogger(modId).log(
                        System.Logger.Level.WARNING,
                        "Rejected malformed portable packet " + registration.type().id(),
                        exception
                );
            }
        }

        private static byte[] readWireBytes(
                final FriendlyByteBuf buffer,
                final PortablePacketRegistration<?> registration
        ) {
            int readableBytes = buffer.readableBytes();
            int maxWireBytes = registration.type().maxPayloadBytes() + 2048;
            if (readableBytes > maxWireBytes) {
                throw new PortablePacketException(
                        "Packet " + registration.type().id() + " wire payload " + readableBytes
                                + " exceeds limit " + maxWireBytes
                );
            }
            byte[] bytes = new byte[readableBytes];
            buffer.readBytes(bytes);
            return bytes;
        }

        private final class FabricServerPacketSender implements PortablePacketSender {
            private final ServerPlayer player;

            private FabricServerPacketSender(final ServerPlayer player) {
                this.player = Objects.requireNonNull(player, "player");
            }

            @Override
            public Optional<UUID> playerId() {
                return Optional.of(player.getUUID());
            }

            @Override
            public Optional<String> displayName() {
                return Optional.of(player.getDisplayName().getString());
            }

            @Override
            public <T> void send(final PortablePacketType<T> type, final T packet) {
                sendToPlayer(player, type, packet);
            }
        }

        private void registerCommands(final CommandDispatcher<CommandSourceStack> dispatcher) {
            for (PortableCommand command : commands) {
                dispatcher.register(Minecraft1201CommandAdapters.literalCommand(command));
            }
            for (PortableCommandTree tree : commandTrees) {
                dispatcher.register(Minecraft1201CommandAdapters.commandTree(tree));
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
