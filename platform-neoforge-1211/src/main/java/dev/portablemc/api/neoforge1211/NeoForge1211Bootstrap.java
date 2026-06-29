/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.neoforge1211;

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
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PortableEncodedPacket;
import dev.portablemc.api.network.PortableNetworkChannel;
import dev.portablemc.api.network.PortablePacketContext;
import dev.portablemc.api.network.PortablePacketException;
import dev.portablemc.api.network.PortablePacketRegistration;
import dev.portablemc.api.network.PortablePacketSender;
import dev.portablemc.api.network.PortablePacketType;
import dev.portablemc.api.network.PortableNetworking;
import dev.portablemc.api.spi.PortableCommandAdapter;
import dev.portablemc.api.spi.PortableContentAdapter;
import dev.portablemc.api.spi.PortableNetworkingAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * NeoForge bootstrap for Minecraft 1.21.1 mods using Portable API.
 */
public final class NeoForge1211Bootstrap {
    private NeoForge1211Bootstrap() {
    }

    /**
     * Initializes a shared mod from a NeoForge {@code @Mod} constructor.
     *
     * @param modId owning mod id
     * @param modEventBus mod event bus supplied by NeoForge
     * @param mod shared mod initializer
     */
    public static void initialize(final String modId, final IEventBus modEventBus, final PortableMod mod) {
        Objects.requireNonNull(modEventBus, "modEventBus");
        Objects.requireNonNull(mod, "mod");
        NeoForgeRuntime runtime = new NeoForgeRuntime(modId, modEventBus);
        DefaultPortableModContext context = createContext(modId, runtime);
        runtime.installEventBridges(context, modEventBus);
        mod.initialize(context);
    }

    private static DefaultPortableModContext createContext(final String modId, final NeoForgeRuntime runtime) {
        RuntimeSide side = FMLEnvironment.dist == Dist.CLIENT ? RuntimeSide.CLIENT : RuntimeSide.DEDICATED_SERVER;
        PlatformInfo platform = new PlatformInfo(
                LoaderKind.NEOFORGE,
                MinecraftVersion.MC_1_21_1,
                side,
                Optional.empty(),
                FMLPaths.CONFIGDIR.get()
        );
        PortablePlatformServices platformServices = PortablePlatformServices.builder(platform)
                .modLoaded(id -> ModList.get().isLoaded(id))
                .modVersion(id -> ModList.get().getModContainerById(id)
                        .map(container -> container.getModInfo().getVersion().toString()))
                .developmentEnvironment(!FMLEnvironment.production)
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

    private static final class NeoForgeRuntime implements PortableContentAdapter, PortableCommandAdapter, PortableNetworkingAdapter {
        private final String modId;
        private final DeferredRegister<Block> blocks;
        private final DeferredRegister<Item> items;
        private final List<PortableCreativeTabEntry> creativeTabEntries = new ArrayList<>();
        private final List<PortableCommand> commands = new ArrayList<>();
        private final List<PortableCommandTree> commandTrees = new ArrayList<>();
        private final List<PortablePacketRegistration<?>> packetRegistrations = new ArrayList<>();
        private final Map<dev.portablemc.api.PortableIdentifier, CustomPacketPayload.Type<NeoForge1211PortablePayload>> payloadTypes =
                new ConcurrentHashMap<>();
        private final java.util.Map<dev.portablemc.api.PortableIdentifier, Supplier<? extends Item>> itemSuppliers =
                new java.util.concurrent.ConcurrentHashMap<>();

        private NeoForgeRuntime(final String modId, final IEventBus modEventBus) {
            this.modId = Objects.requireNonNull(modId, "modId");
            this.blocks = DeferredRegister.create(Registries.BLOCK, modId);
            this.items = DeferredRegister.create(Registries.ITEM, modId);
            this.blocks.register(modEventBus);
            this.items.register(modEventBus);
        }

        private void installEventBridges(final DefaultPortableModContext context, final IEventBus modEventBus) {
            modEventBus.addListener((FMLCommonSetupEvent event) -> context.lifecycle().fireCommonSetup());
            modEventBus.addListener((FMLClientSetupEvent event) -> {
                context.lifecycle().fireClientSetup();
                NeoForge1211ClientBridge.install(context);
            });
            modEventBus.addListener(this::onCreativeTab);
            modEventBus.addListener(this::registerPayloadHandlers);
            NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
            NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) ->
                    context.lifecycle().fireServerStarting(serverContext(event.getServer())));
            NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) ->
                    context.lifecycle().fireServerStarted(serverContext(event.getServer())));
            NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) ->
                    context.lifecycle().fireServerStopping(serverContext(event.getServer())));
            NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) ->
                    context.lifecycle().fireServerStopped(serverContext(event.getServer())));
            NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) ->
                    context.lifecycle().fireServerTick(serverContext(event.getServer())));
        }

        private static PortableServerContext serverContext(final MinecraftServer server) {
            return new PortableServerContext(server.getWorldPath(LevelResource.ROOT));
        }

        @Override
        public PortableBlockRegistration registerSimpleBlock(
                final PortableBlockDefinition blockDefinition,
                final PortableItemDefinition itemDefinition
        ) {
            DeferredHolder<Block, Block> block = blocks.register(
                    blockDefinition.id().path(),
                    () -> Minecraft1211Adapters.createBlock(blockDefinition)
            );
            DeferredHolder<Item, Item> item = items.register(
                    itemDefinition.id().path(),
                    () -> Minecraft1211Adapters.createBlockItem(block.get(), itemDefinition)
            );
            itemSuppliers.put(itemDefinition.id(), item);
            return new PortableBlockRegistration(
                    new PortableRegistryHandle<>(blockDefinition.id(), PortableBlockDefinition.class),
                    new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class)
            );
        }

        @Override
        public PortableRegistryHandle<PortableItemDefinition> registerItem(final PortableItemDefinition itemDefinition) {
            DeferredHolder<Item, Item> item = items.register(
                    itemDefinition.id().path(),
                    () -> Minecraft1211Adapters.createItem(itemDefinition)
            );
            itemSuppliers.put(itemDefinition.id(), item);
            return new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class);
        }

        @Override
        public void addCreativeTabEntry(final PortableCreativeTabEntry entry) {
            if (!itemSuppliers.containsKey(entry.itemId())) {
                throw new IllegalStateException("Creative tab entry references an item that has not been registered: " + entry.itemId());
            }
            creativeTabEntries.add(entry);
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
            payloadTypes.put(registration.type().id(), NeoForge1211PortablePayload.type(registration));
            System.getLogger(modId).log(System.Logger.Level.DEBUG, "Registered portable packet " + registration.type().id());
        }

        @Override
        public void sendToServer(final PortableEncodedPacket packet) {
            if (packet.direction() != PacketDirection.CLIENT_TO_SERVER) {
                throw new IllegalArgumentException("Packet " + packet.id() + " is not client-to-server");
            }
            CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType = payloadTypes.get(packet.id());
            if (payloadType == null) {
                throw new IllegalStateException("No NeoForge payload type registered for " + packet.id());
            }
            PacketDistributor.sendToServer(new NeoForge1211PortablePayload(payloadType, packet));
        }

        <T> void sendToPlayer(final ServerPlayer player, final PortablePacketType<T> type, final T packet) {
            if (type.direction() != PacketDirection.SERVER_TO_CLIENT) {
                throw new IllegalArgumentException("Packet " + type.id() + " is not server-to-client");
            }
            CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType = payloadTypes.get(type.id());
            if (payloadType == null) {
                throw new IllegalStateException("No NeoForge payload type registered for " + type.id());
            }
            PacketDistributor.sendToPlayer(player, NeoForge1211PortablePayload.encode(payloadType, type, packet));
        }

        private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
            var registrar = event.registrar("1.1.0");
            for (PortablePacketRegistration<?> registration : packetRegistrations) {
                CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType = payloadTypes.get(registration.type().id());
                if (registration.type().direction() == PacketDirection.CLIENT_TO_SERVER) {
                    registrar.playToServer(
                            payloadType,
                            NeoForge1211PortablePayload.codec(payloadType, registration),
                            (payload, context) -> handlePayload(registration, payload, context)
                    );
                } else {
                    registrar.playToClient(
                            payloadType,
                            NeoForge1211PortablePayload.codec(payloadType, registration),
                            (payload, context) -> handlePayload(registration, payload, context)
                    );
                }
            }
        }

        private void handlePayload(
                final PortablePacketRegistration<?> registration,
                final NeoForge1211PortablePayload payload,
                final IPayloadContext context
        ) {
            try {
                PacketDirection expectedDirection = context.flow() == PacketFlow.SERVERBOUND
                        ? PacketDirection.CLIENT_TO_SERVER : PacketDirection.SERVER_TO_CLIENT;
                if (payload.packet().direction() != expectedDirection) {
                    throw new PortablePacketException("Packet direction " + payload.packet().direction()
                            + " does not match NeoForge reception direction " + context.flow());
                }
                Optional<PortablePacketSender> sender = context.player() instanceof ServerPlayer serverPlayer
                        ? Optional.of(new NeoForgePacketSender(serverPlayer))
                        : Optional.empty();
                registration.dispatch(payload.packet(), new PortablePacketContext(
                        expectedDirection,
                        sender,
                        work -> context.enqueueWork(work)
                ));
            } catch (PortablePacketException exception) {
                System.getLogger(modId).log(System.Logger.Level.WARNING, "Rejected malformed portable packet", exception);
            }
        }

        private void onCreativeTab(final BuildCreativeModeTabContentsEvent event) {
            for (PortableCreativeTabEntry entry : creativeTabEntries) {
                if (event.getTabKey().equals(Minecraft1211Adapters.creativeTabKey(entry.tab()))) {
                    event.accept(itemSuppliers.get(entry.itemId()).get());
                }
            }
        }

        private void onRegisterCommands(final RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            for (PortableCommand command : commands) {
                dispatcher.register(Minecraft1211CommandAdapters.literalCommand(command));
            }
            for (PortableCommandTree tree : commandTrees) {
                dispatcher.register(Minecraft1211CommandAdapters.commandTree(tree));
            }
        }

        private final class NeoForgePacketSender implements PortablePacketSender {
            private final ServerPlayer player;

            private NeoForgePacketSender(final ServerPlayer player) {
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
