package dev.portablemc.api.command;

/** Result from a portable command execution. */
public record CommandResult(int statusCode) {
  public static final CommandResult SUCCESS = new CommandResult(1);
  public static final CommandResult PASS = new CommandResult(0);
}
