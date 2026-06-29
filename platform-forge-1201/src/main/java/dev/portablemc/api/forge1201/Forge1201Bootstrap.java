/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.forge1201;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Forge bootstrap for Minecraft 1.20.1 mods using Portable API.
 */
public final class Forge1201Bootstrap {
    private Forge1201Bootstrap() {
    }

    /**
     * Initializes a shared mod from a Forge {@code @Mod} constructor.
     *
     * @param modId owning mod id
     * @param modEventBus mod event bus supplied by Forge
     * @param mod shared mod initializer
     */
    public static void initialize(final String modId, final IEventBus modEventBus, final PortableMod mod) {
        Objects.requireNonNull(modEventBus, "modEventBus");
        Objects.requireNonNull(mod, "mod");
        ForgeRuntime runtime = new ForgeRuntime(modId, modEventBus);
        DefaultPortableModContext context = createContext(modId, runtime);
        runtime.installEventBridges(context, modEventBus);
        mod.initialize(context);
    }

    private static DefaultPortableModContext createContext(final String modId, final ForgeRuntime runtime) {
        RuntimeSide side = FMLEnvironment.dist == Dist.CLIENT ? RuntimeSide.CLIENT : RuntimeSide.DEDICATED_SERVER;
        PlatformInfo platform = new PlatformInfo(
                LoaderKind.FORGE,
                MinecraftVersion.MC_1_20_1,
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

    private static final class ForgeRuntime implements PortableContentAdapter, PortableCommandAdapter, PortableNetworkingAdapter {
        private final String modId;
        private final DeferredRegister<Block> blocks;
        private final DeferredRegister<Item> items;
        private final SimpleChannel networkChannel;
        private final List<PortableCreativeTabEntry> creativeTabEntries = new ArrayList<>();
        private final List<PortableCommand> commands = new ArrayList<>();
        private final List<PortableCommandTree> commandTrees = new ArrayList<>();
        private final List<PortablePacketRegistration<?>> packetRegistrations = new ArrayList<>();
        private final Map<RegistrationKey, PortablePacketRegistration<?>> registrations = new ConcurrentHashMap<>();
        private final java.util.Map<dev.portablemc.api.PortableIdentifier, Supplier<? extends Item>> itemSuppliers =
                new java.util.concurrent.ConcurrentHashMap<>();

        private ForgeRuntime(final String modId, final IEventBus modEventBus) {
            this.modId = Objects.requireNonNull(modId, "modId");
            this.blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
            this.items = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
            this.networkChannel = NetworkRegistry.newSimpleChannel(
                    Minecraft1201Adapters.resourceLocation(dev.portablemc.api.PortableIdentifier.of(modId, "portable")),
                    () -> "1.1.0",
                    ignored -> true,
                    ignored -> true
            );
            this.networkChannel.messageBuilder(ForgeWirePacket.class, 0)
                    .encoder(ForgeWirePacket::encode)
                    .decoder(ForgeWirePacket::decode)
                    .consumerMainThread((packet, context) -> handleWirePacket(packet, context.get()))
                    .add();
            this.blocks.register(modEventBus);
            this.items.register(modEventBus);
        }

        private void installEventBridges(final DefaultPortableModContext context, final IEventBus modEventBus) {
            modEventBus.addListener((FMLCommonSetupEvent event) -> context.lifecycle().fireCommonSetup());
            modEventBus.addListener((FMLClientSetupEvent event) -> context.lifecycle().fireClientSetup());
            modEventBus.addListener(this::onCreativeTab);
            MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
            MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
                if (event.phase == TickEvent.Phase.END) {
                    context.lifecycle().fireClientTick();
                }
            });
            MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) ->
                    context.lifecycle().fireServerStarting(serverContext(event.getServer())));
            MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent event) ->
                    context.lifecycle().fireServerStarted(serverContext(event.getServer())));
            MinecraftForge.EVENT_BUS.addListener((ServerStoppingEvent event) ->
                    context.lifecycle().fireServerStopping(serverContext(event.getServer())));
            MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) ->
                    context.lifecycle().fireServerStopped(serverContext(event.getServer())));
            MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
                if (event.phase == TickEvent.Phase.END) {
                    context.lifecycle().fireServerTick(serverContext(event.getServer()));
                }
            });
        }

        private static PortableServerContext serverContext(final MinecraftServer server) {
            return new PortableServerContext(server.getWorldPath(LevelResource.ROOT));
        }

        @Override
        public PortableBlockRegistration registerSimpleBlock(
                final PortableBlockDefinition blockDefinition,
                final PortableItemDefinition itemDefinition
        ) {
            RegistryObject<Block> block = blocks.register(
                    blockDefinition.id().path(),
                    () -> Minecraft1201Adapters.createBlock(blockDefinition)
            );
            RegistryObject<Item> item = items.register(
                    itemDefinition.id().path(),
                    () -> Minecraft1201Adapters.createBlockItem(block.get(), itemDefinition)
            );
            itemSuppliers.put(itemDefinition.id(), item);
            return new PortableBlockRegistration(
                    new PortableRegistryHandle<>(blockDefinition.id(), PortableBlockDefinition.class),
                    new PortableRegistryHandle<>(itemDefinition.id(), PortableItemDefinition.class)
            );
        }

        @Override
        public PortableRegistryHandle<PortableItemDefinition> registerItem(final PortableItemDefinition itemDefinition) {
            RegistryObject<Item> item = items.register(itemDefinition.id().path(), () -> Minecraft1201Adapters.createItem(itemDefinition));
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
            registrations.put(new RegistrationKey(registration.type().id(), registration.type().direction()), registration);
            System.getLogger(modId).log(System.Logger.Level.DEBUG, "Registered portable packet " + registration.type().id());
        }

        @Override
        public void sendToServer(final PortableEncodedPacket packet) {
            if (packet.direction() != PacketDirection.CLIENT_TO_SERVER) {
                throw new IllegalArgumentException("Packet " + packet.id() + " is not client-to-server");
            }
            networkChannel.sendToServer(new ForgeWirePacket(PortablePacketWireFormat.encode(packet)));
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
            networkChannel.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new ForgeWirePacket(PortablePacketWireFormat.encode(encoded))
            );
        }

        private void handleWirePacket(final ForgeWirePacket packet, final NetworkEvent.Context context) {
            try {
                PortablePacketWireFormat.Header header = PortablePacketWireFormat.inspect(packet.wireBytes());
                PacketDirection expectedDirection = context.getDirection() == NetworkDirection.PLAY_TO_SERVER
                        ? PacketDirection.CLIENT_TO_SERVER : PacketDirection.SERVER_TO_CLIENT;
                if (header.direction() != expectedDirection) {
                    throw new PortablePacketException("Packet direction " + header.direction()
                            + " does not match Forge reception direction " + context.getDirection());
                }
                PortablePacketRegistration<?> registration = registrations.get(new RegistrationKey(header.id(), header.direction()));
                if (registration == null) {
                    throw new PortablePacketException("No portable packet registration for " + header.direction() + " " + header.id());
                }
                PortableEncodedPacket encoded = PortablePacketWireFormat.decode(registration, packet.wireBytes());
                Optional<PortablePacketSender> sender = Optional.ofNullable(context.getSender()).map(ForgePacketSender::new);
                registration.dispatch(encoded, new PortablePacketContext(
                        header.direction(),
                        sender,
                        work -> context.enqueueWork(work)
                ));
            } catch (PortablePacketException exception) {
                System.getLogger(modId).log(System.Logger.Level.WARNING, "Rejected malformed portable packet", exception);
            }
        }

        private void onCreativeTab(final BuildCreativeModeTabContentsEvent event) {
            for (PortableCreativeTabEntry entry : creativeTabEntries) {
                if (event.getTabKey().equals(Minecraft1201Adapters.creativeTabKey(entry.tab()))) {
                    event.accept(itemSuppliers.get(entry.itemId()).get());
                }
            }
        }

        private void onRegisterCommands(final RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            for (PortableCommand command : commands) {
                dispatcher.register(Minecraft1201CommandAdapters.literalCommand(command));
            }
            for (PortableCommandTree tree : commandTrees) {
                dispatcher.register(Minecraft1201CommandAdapters.commandTree(tree));
            }
        }

        private final class ForgePacketSender implements PortablePacketSender {
            private final ServerPlayer player;

            private ForgePacketSender(final ServerPlayer player) {
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

        private record RegistrationKey(dev.portablemc.api.PortableIdentifier id, PacketDirection direction) {
            private RegistrationKey {
                Objects.requireNonNull(id, "id");
                Objects.requireNonNull(direction, "direction");
            }
        }

        private static final class ForgeWirePacket {
            private static final int MAX_WIRE_BYTES = 1_050_624;
            private final byte[] wireBytes;

            private ForgeWirePacket(final byte[] wireBytes) {
                this.wireBytes = Objects.requireNonNull(wireBytes, "wireBytes").clone();
                if (this.wireBytes.length > MAX_WIRE_BYTES) {
                    throw new PortablePacketException("Portable packet wire payload exceeds " + MAX_WIRE_BYTES + " bytes");
                }
            }

            private byte[] wireBytes() {
                return wireBytes.clone();
            }

            private static void encode(final ForgeWirePacket packet, final FriendlyByteBuf buffer) {
                buffer.writeByteArray(packet.wireBytes());
            }

            private static ForgeWirePacket decode(final FriendlyByteBuf buffer) {
                return new ForgeWirePacket(buffer.readByteArray(MAX_WIRE_BYTES));
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
