package dev.portablemc.internal.core.command;

import dev.portablemc.api.command.CommandService;
import dev.portablemc.api.command.CommandSpec;
import dev.portablemc.api.event.EventHandle;
import dev.portablemc.api.runtime.Capability;
import dev.portablemc.api.runtime.CapabilityReport;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Stores command declarations until the platform command event fires. */
public final class DefaultCommandService implements CommandService {
  private final CapabilityReport capabilities;
  private final List<CommandSpec> commands = new CopyOnWriteArrayList<>();

  public DefaultCommandService(CapabilityReport capabilities) {
    this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
  }

  @Override
  public EventHandle register(CommandSpec command) {
    capabilities.require(Capability.COMMANDS);
    Objects.requireNonNull(command, "command");
    commands.add(command);
    return () -> commands.remove(command);
  }

  public List<CommandSpec> commands() {
    return List.copyOf(commands);
  }
}
