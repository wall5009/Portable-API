package dev.portablemc.api.command;

import dev.portablemc.api.runtime.LogicalSide;
import java.util.List;

/** Loader-neutral command invocation context. */
public interface CommandInvocation {
  /** Returns parsed positional arguments not otherwise represented in v1. */
  List<String> arguments();

  /** Returns the logical side for this invocation. */
  LogicalSide side();

  /** Sends a system message to the command source when supported. */
  void sendMessage(String message);
}
