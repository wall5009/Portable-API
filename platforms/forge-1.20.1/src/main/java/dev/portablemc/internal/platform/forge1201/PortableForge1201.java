package dev.portablemc.internal.platform.forge1201;

import dev.portablemc.api.command.CommandInvocation;
import dev.portablemc.api.command.CommandResult;
import dev.portablemc.api.command.CommandSpec;
import dev.portablemc.api.runtime.LoaderKind;
import dev.portablemc.api.runtime.LogicalSide;
import dev.portablemc.api.runtime.PhysicalSide;
import dev.portablemc.internal.bootstrap.PortableBootstrap;
import dev.portablemc.internal.common.mc1201.Minecraft1201RuntimeFactory;
import dev.portablemc.internal.core.CorePortableRuntime;
import dev.portablemc.internal.core.DefaultPlatformInfo;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;

/** Forge 1.20.1 bootstrap. */
@Mod(PortableForge1201.MOD_ID)
public final class PortableForge1201 {
  public static final String MOD_ID = "portable_api";
  private final CorePortableRuntime runtime;

  public PortableForge1201() {
    runtime = Minecraft1201RuntimeFactory.create(platformInfo());
    PortableBootstrap.install(runtime);
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.addListener(this::commonSetup);
    modBus.addListener(this::clientSetup);
    MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
    MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
    MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
  }

  private static DefaultPlatformInfo platformInfo() {
    Set<String> loadedMods =
        ModList.get().getMods().stream()
            .map(mod -> mod.getModId())
            .collect(Collectors.toUnmodifiableSet());
    PhysicalSide side =
        FMLEnvironment.dist == Dist.CLIENT ? PhysicalSide.CLIENT : PhysicalSide.DEDICATED_SERVER;
    return new DefaultPlatformInfo(
        LoaderKind.FORGE,
        "1.20.1",
        Optional.ofNullable(ForgeVersion.getVersion()),
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
                              .execute(new ForgeInvocation(context.getSource(), List.of()));
                      return result.statusCode();
                    }));
  }

  private record ForgeInvocation(CommandSourceStack source, List<String> arguments)
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
