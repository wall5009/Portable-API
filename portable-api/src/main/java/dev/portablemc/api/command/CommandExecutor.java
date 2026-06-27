package dev.portablemc.api.command;

/** Executes a portable command. */
@FunctionalInterface
public interface CommandExecutor {
  /** Executes the command. */
  CommandResult execute(CommandInvocation invocation);
}
