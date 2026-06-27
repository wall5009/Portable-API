package dev.portablemc.internal.core.logging;

import dev.portablemc.api.logging.PortableLogger;
import java.lang.System.Logger.Level;
import java.text.MessageFormat;

/** Portable logger backed by {@link System.Logger}. */
public final class SystemPortableLogger implements PortableLogger {
  private final System.Logger logger;

  public SystemPortableLogger(String name) {
    this.logger = System.getLogger(name);
  }

  @Override
  public void trace(String message, Object... arguments) {
    log(Level.TRACE, message, arguments);
  }

  @Override
  public void debug(String message, Object... arguments) {
    log(Level.DEBUG, message, arguments);
  }

  @Override
  public void info(String message, Object... arguments) {
    log(Level.INFO, message, arguments);
  }

  @Override
  public void warn(String message, Object... arguments) {
    log(Level.WARNING, message, arguments);
  }

  @Override
  public void error(String message, Object... arguments) {
    log(Level.ERROR, message, arguments);
  }

  private void log(Level level, String message, Object... arguments) {
    logger.log(level, format(message, arguments));
  }

  private static String format(String message, Object... arguments) {
    if (arguments == null || arguments.length == 0) {
      return message;
    }
    String pattern = message.replace("{}", "{0}");
    if (arguments.length == 1) {
      return MessageFormat.format(pattern, arguments);
    }
    StringBuilder builder = new StringBuilder(message);
    builder.append(" ");
    for (Object argument : arguments) {
      builder.append("[").append(argument).append("]");
    }
    return builder.toString();
  }
}
