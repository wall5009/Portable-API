package dev.portablemc.api.command;

import dev.portablemc.api.event.EventHandle;

/** Command registration service. */
public interface CommandService {
  /** Registers a command declaration. */
  EventHandle register(CommandSpec command);
}
