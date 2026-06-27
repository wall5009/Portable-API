package dev.portablemc.api.command;

import java.util.Objects;
import java.util.regex.Pattern;

/** Minimal stable command declaration for v1. */
public record CommandSpec(String literal, int permissionLevel, CommandExecutor executor) {
  private static final Pattern LITERAL = Pattern.compile("[a-z0-9_./-]+");

  public CommandSpec {
    Objects.requireNonNull(literal, "literal");
    Objects.requireNonNull(executor, "executor");
    if (!LITERAL.matcher(literal).matches()) {
      throw new IllegalArgumentException("Invalid command literal: " + literal);
    }
    if (permissionLevel < 0) {
      throw new IllegalArgumentException("permissionLevel must be non-negative");
    }
  }

  /** Creates a user-level literal command. */
  public static CommandSpec literal(String literal, CommandExecutor executor) {
    return new CommandSpec(literal, 0, executor);
  }
}
