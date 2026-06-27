package dev.portablemc.internal.platform.fabric1211;

import dev.portablemc.api.command.CommandInvocation;
import dev.portablemc.api.command.CommandResult;
import dev.portablemc.api.command.CommandSpec;
import dev.portablemc.api.runtime.LoaderKind;
import dev.portablemc.api.runtime.LogicalSide;
import dev.portablemc.api.runtime.PhysicalSide;
import dev.portablemc.internal.bootstrap.PortableBootstrap;
import dev.portablemc.internal.common.mc1211.Minecraft1211RuntimeFactory;
import dev.portablemc.internal.core.CorePortableRuntime;
import dev.portablemc.internal.core.DefaultPlatformInfo;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/** Fabric 1.21.1 bootstrap. */
public final class PortableFabric1211 implements ModInitializer {
  @Override
  public void onInitialize() {
    CorePortableRuntime runtime = Minecraft1211RuntimeFactory.create(platformInfo());
    PortableBootstrap.install(runtime);
    ServerLifecycleEvents.SERVER_STARTING.register(server -> runtime.fireServerStarting());
    ServerLifecycleEvents.SERVER_STARTED.register(server -> runtime.fireServerStarted());
    ServerLifecycleEvents.SERVER_STOPPING.register(server -> runtime.fireServerStopping());
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) ->
            runtime.commandSpecs().forEach(command -> registerCommand(dispatcher, command)));
  }

  private static DefaultPlatformInfo platformInfo() {
    FabricLoader loader = FabricLoader.getInstance();
    Set<String> loadedMods =
        loader.getAllMods().stream()
            .map(mod -> mod.getMetadata().getId())
            .collect(Collectors.toUnmodifiableSet());
    Optional<String> loaderVersion =
        loader
            .getModContainer("fabricloader")
            .map(container -> container.getMetadata().getVersion().getFriendlyString());
    PhysicalSide side =
        loader.getEnvironmentType() == EnvType.CLIENT
            ? PhysicalSide.CLIENT
            : PhysicalSide.DEDICATED_SERVER;
    return new DefaultPlatformInfo(
        LoaderKind.FABRIC,
        "1.21.1",
        loaderVersion,
        side,
        !loader.isDevelopmentEnvironment(),
        loadedMods);
  }

  private static void registerCommand(
      com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher, CommandSpec command) {
    dispatcher.register(
        Commands.literal(command.literal())
            .requires(source -> source.hasPermission(command.permissionLevel()))
            .executes(
                context -> {
                  CommandResult result =
                      command
                          .executor()
                          .execute(new FabricInvocation(context.getSource(), List.of()));
                  return result.statusCode();
                }));
  }

  private record FabricInvocation(CommandSourceStack source, List<String> arguments)
      implements CommandInvocation {
    @Override
    public LogicalSide side() {
      return LogicalSide.SERVER;
    }

    @Override
    public void sendMessage(String message) {
      source.sendSystemMessage(Component.literal(message));
    }
  }
}
