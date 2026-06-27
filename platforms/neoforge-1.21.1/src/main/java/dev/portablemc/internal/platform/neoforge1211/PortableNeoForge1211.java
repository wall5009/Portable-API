package dev.portablemc.internal.platform.neoforge1211;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** NeoForge 1.21.1 bootstrap. */
@Mod(PortableNeoForge1211.MOD_ID)
public final class PortableNeoForge1211 {
  public static final String MOD_ID = "portable_api";
  private final CorePortableRuntime runtime;

  public PortableNeoForge1211(IEventBus modBus) {
    runtime = Minecraft1211RuntimeFactory.create(platformInfo());
    PortableBootstrap.install(runtime);
    modBus.addListener(this::commonSetup);
    modBus.addListener(this::clientSetup);
    NeoForge.EVENT_BUS.addListener(this::serverStarting);
    NeoForge.EVENT_BUS.addListener(this::serverStarted);
    NeoForge.EVENT_BUS.addListener(this::serverStopping);
    NeoForge.EVENT_BUS.addListener(this::registerCommands);
  }

  private static DefaultPlatformInfo platformInfo() {
    Set<String> loadedMods =
        ModList.get().getMods().stream()
            .map(mod -> mod.getModId())
            .collect(Collectors.toUnmodifiableSet());
    PhysicalSide side =
        FMLEnvironment.dist == Dist.CLIENT ? PhysicalSide.CLIENT : PhysicalSide.DEDICATED_SERVER;
    return new DefaultPlatformInfo(
        LoaderKind.NEOFORGE,
        "1.21.1",
        Optional.empty(),
        side,
        FMLEnvironment.production,
        loadedMods);
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(runtime::fireCommonSetup);
  }

  private void clientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(runtime::fireClientSetup);
  }

  private void serverStarting(ServerStartingEvent event) {
    runtime.fireServerStarting();
  }

  private void serverStarted(ServerStartedEvent event) {
    runtime.fireServerStarted();
  }

  private void serverStopping(ServerStoppingEvent event) {
    runtime.fireServerStopping();
  }

  private void registerCommands(RegisterCommandsEvent event) {
    runtime.commandSpecs().forEach(command -> registerCommand(event, command));
  }

  private static void registerCommand(RegisterCommandsEvent event, CommandSpec command) {
    event
        .getDispatcher()
        .register(
            Commands.literal(command.literal())
                .requires(source -> source.hasPermission(command.permissionLevel()))
                .executes(
                    context -> {
                      CommandResult result =
                          command
                              .executor()
                              .execute(new NeoForgeInvocation(context.getSource(), List.of()));
                      return result.statusCode();
                    }));
  }

  private record NeoForgeInvocation(CommandSourceStack source, List<String> arguments)
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
