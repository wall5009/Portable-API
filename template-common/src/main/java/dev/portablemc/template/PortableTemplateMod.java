/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.template;

import dev.portablemc.api.PortableMod;
import dev.portablemc.api.PortableModContext;
import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.command.PortableCommandArgumentType;
import dev.portablemc.api.command.PortableCommandNode;
import dev.portablemc.api.command.PortableCommandTree;
import dev.portablemc.api.config.PortableConfigEntry;
import dev.portablemc.api.config.PortableConfigHandle;
import dev.portablemc.api.config.PortableTypedConfigSpec;
import dev.portablemc.api.content.PortableBlockRegistration;
import dev.portablemc.api.content.PortableBlockSettings;
import dev.portablemc.api.content.PortableCreativeTabKey;
import dev.portablemc.api.content.PortableItemDefinition;
import dev.portablemc.api.content.PortableItemSettings;
import dev.portablemc.api.content.PortableRegistryHandle;
import dev.portablemc.api.datagen.PortableLanguageBuilder;
import dev.portablemc.api.datagen.PortableLootTableBuilders;
import dev.portablemc.api.datagen.PortableModelBuilders;
import dev.portablemc.api.datagen.PortablePackMetadataBuilder;
import dev.portablemc.api.datagen.PortableRecipeBuilders;
import dev.portablemc.api.datagen.PortableTagBuilder;
import dev.portablemc.api.network.NetworkPhase;
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PortablePacketCodec;
import dev.portablemc.api.network.PortablePacketReader;
import dev.portablemc.api.network.PortablePacketType;
import dev.portablemc.api.network.PortablePacketWriter;
import java.util.List;
import java.util.Map;

/**
 * The entire shared implementation of the template mod.
 *
 * <p>The class intentionally contains only the minimum needed to prove the
 * repository layout: content registration, creative-tab entries, a command, a
 * config file, lifecycle hooks, a network channel declaration, and generated
 * resources. Real projects should copy this module and grow from here.</p>
 */
public final class PortableTemplateMod implements PortableMod {
    /** Stable mod id used by every loader target. */
    public static final String MOD_ID = "portable_template";

    @Override
    public void initialize(final PortableModContext context) {
        PortableBlockRegistration block = context.content().registerSimpleBlock(
                "portable_block",
                PortableBlockSettings.stoneLike(),
                PortableItemSettings.defaults()
        );
        PortableRegistryHandle<PortableItemDefinition> item =
                context.content().registerItem("portable_item", PortableItemSettings.defaults());

        context.content().addToCreativeTab(PortableCreativeTabKey.BUILDING_BLOCKS, block.item());
        context.content().addToCreativeTab(PortableCreativeTabKey.INGREDIENTS, item);

        PortableTypedConfigSpec.Builder configBuilder = PortableTypedConfigSpec.builder(MOD_ID + ".properties")
                .headerComment("Portable Template configuration");
        PortableConfigEntry<Boolean> enabled = configBuilder.booleanValue("enabled", true, "Enables the template interaction.");
        PortableConfigEntry<Integer> testValue = configBuilder.intValue("testValue", 3, 0, 16, "Small bounded test value.");
        PortableConfigHandle config = context.config().registerTyped(configBuilder.build());

        PortablePacketType<PingPacket> ping = context.networking().packetType(
                "ping",
                NetworkPhase.PLAY,
                PacketDirection.CLIENT_TO_SERVER,
                1,
                64,
                PingPacket.CODEC
        );
        PortablePacketType<PongPacket> pong = context.networking().packetType(
                "pong",
                NetworkPhase.PLAY,
                PacketDirection.SERVER_TO_CLIENT,
                1,
                128,
                PongPacket.CODEC
        );
        context.networking().registerClientToServer(ping, (packet, packetContext) -> packetContext.execution().executeOnMainThread(() -> {
            context.logger().info("Portable Template received ping " + packet.value());
            packetContext.sender().ifPresent(sender -> sender.send(pong, new PongPacket("pong:" + packet.value())));
        }));
        context.networking().registerServerToClient(pong, (packet, packetContext) ->
                packetContext.execution().executeOnMainThread(() -> context.logger().info(packet.message())));

        PortableCommandNode.Builder root = PortableCommandTree.literal("portable_template").requiresPermission(0);
        root.thenLiteral("status").executes(commandContext -> {
            commandContext.reply("Portable Template is running on "
                    + context.platform().loader() + " " + context.platform().minecraftVersion().id()
                    + " with enabled=" + config.snapshot().get(enabled));
            return 1;
        });
        root.thenLiteral("set")
                .thenArgument("enabled", PortableCommandArgumentType.BOOLEAN)
                .executes(commandContext -> {
                    commandContext.reply("Requested enabled=" + commandContext.argument("enabled", Boolean.class)
                            + "; edit the config file and reload to persist changes.");
                    return 1;
                });
        root.thenLiteral("value").executes(commandContext -> {
            commandContext.reply("Configured testValue=" + config.snapshot().get(testValue));
            return 1;
        });
        context.commands().registerTree(new PortableCommandTree(root.build()));

        context.lifecycle().onCommonSetup(() -> context.logger().info("Portable Template common setup completed."));
        context.lifecycle().onClientSetup(() -> context.logger().debug("Portable Template client setup completed."));
        context.lifecycle().onServerStarting(server -> context.logger().info("Portable Template server starting at "
                + server.worldDirectory()));
        context.lifecycle().onServerStarted(server -> context.logger().debug("Portable Template server started."));
        context.lifecycle().onServerStopping(server -> context.logger().debug("Portable Template server stopping."));
        context.lifecycle().onServerStopped(server -> context.logger().debug("Portable Template server stopped."));
        context.lifecycle().onReload(() -> context.logger().info("Portable Template reload hook fired."));
        config.addReloadListener(snapshot -> context.logger().info("Portable Template config reloaded."));
        context.lifecycle().onDataGeneration(data -> {
            PortableIdentifier blockId = PortableIdentifier.of(MOD_ID, "portable_block");
            PortableIdentifier itemId = PortableIdentifier.of(MOD_ID, "portable_item");
            data.writeAsset("lang/en_us.json", new PortableLanguageBuilder()
                    .add("block.portable_template.portable_block", "Portable Block")
                    .add("item.portable_template.portable_item", "Portable Item")
                    .add("itemGroup.portable_template.main", "Portable Template")
                    .toJson());
            data.writeAsset("models/block/portable_block.json",
                    PortableModelBuilders.cubeAllBlock(PortableIdentifier.parse("minecraft:block/stone", "minecraft")));
            data.writeAsset("models/item/portable_block.json",
                    PortableModelBuilders.blockItem(PortableIdentifier.of(MOD_ID, "block/portable_block")));
            data.writeAsset("models/item/portable_item.json",
                    PortableModelBuilders.generatedItem(PortableIdentifier.parse("minecraft:item/iron_ingot", "minecraft")));
            data.writeAsset("blockstates/portable_block.json",
                    PortableModelBuilders.simpleBlockState(PortableIdentifier.of(MOD_ID, "block/portable_block")));
            data.writeData("tags/blocks/mineable/pickaxe.json", new PortableTagBuilder().add(blockId).toJson());
            data.writeData("tags/items/portable_items.json", new PortableTagBuilder().add(itemId).toJson());
            data.writeData("recipes/portable_item.json", PortableRecipeBuilders.shapeless(
                    List.of(PortableIdentifier.parse("minecraft:iron_ingot", "minecraft")),
                    itemId,
                    1
            ));
            data.writeData("recipes/portable_block.json", PortableRecipeBuilders.shaped(
                    List.of("II", "II"),
                    Map.of('I', PortableIdentifier.parse("minecraft:iron_ingot", "minecraft")),
                    blockId,
                    1
            ));
            data.writeData("loot_tables/blocks/portable_block.json", PortableLootTableBuilders.selfDroppingBlock(blockId));
            data.writeRoot("pack.mcmeta", new PortablePackMetadataBuilder()
                    .packFormat(15)
                    .description("Portable Template generated resources")
                    .toJson());
        });
    }

    private record PingPacket(int value) {
        private static final PortablePacketCodec<PingPacket> CODEC = new PortablePacketCodec<>() {
            @Override
            public void encode(final PortablePacketWriter writer, final PingPacket packet) {
                writer.writeInt(packet.value());
            }

            @Override
            public PingPacket decode(final PortablePacketReader reader) {
                return new PingPacket(reader.readInt());
            }
        };
    }

    private record PongPacket(String message) {
        private static final PortablePacketCodec<PongPacket> CODEC = new PortablePacketCodec<>() {
            @Override
            public void encode(final PortablePacketWriter writer, final PongPacket packet) {
                writer.writeString(packet.message(), 96);
            }

            @Override
            public PongPacket decode(final PortablePacketReader reader) {
                return new PongPacket(reader.readString(96));
            }
        };
    }
}
