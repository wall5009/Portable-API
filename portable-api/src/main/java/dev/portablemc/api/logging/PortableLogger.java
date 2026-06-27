package dev.portablemc.api.logging;

/** Loader-neutral logger facade. */
public interface PortableLogger {
  /** Logs at trace level. */
  void trace(String message, Object... arguments);

  /** Logs at debug level. */
  void debug(String message, Object... arguments);

  /** Logs at info level. */
  void info(String message, Object... arguments);

  /** Logs at warning level. */
  void warn(String message, Object... arguments);

  /** Logs at error level. */
  void error(String message, Object... arguments);
}
