package dev.portablemc.internal.core.event;

import dev.portablemc.api.event.EventHandle;
import dev.portablemc.api.event.LifecycleEvents;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Thread-safe lifecycle callback registry. */
public final class DefaultLifecycleEvents implements LifecycleEvents {
  private final List<Runnable> commonSetup = new CopyOnWriteArrayList<>();
  private final List<Runnable> clientSetup = new CopyOnWriteArrayList<>();
  private final List<Runnable> serverStarting = new CopyOnWriteArrayList<>();
  private final List<Runnable> serverStarted = new CopyOnWriteArrayList<>();
  private final List<Runnable> serverStopping = new CopyOnWriteArrayList<>();

  @Override
  public EventHandle onCommonSetup(Runnable callback) {
    return add(commonSetup, callback);
  }

  @Override
  public EventHandle onClientSetup(Runnable callback) {
    return add(clientSetup, callback);
  }

  @Override
  public EventHandle onServerStarting(Runnable callback) {
    return add(serverStarting, callback);
  }

  @Override
  public EventHandle onServerStarted(Runnable callback) {
    return add(serverStarted, callback);
  }

  @Override
  public EventHandle onServerStopping(Runnable callback) {
    return add(serverStopping, callback);
  }

  public void fireCommonSetup() {
    fire(commonSetup);
  }

  public void fireClientSetup() {
    fire(clientSetup);
  }

  public void fireServerStarting() {
    fire(serverStarting);
  }

  public void fireServerStarted() {
    fire(serverStarted);
  }

  public void fireServerStopping() {
    fire(serverStopping);
  }

  private static EventHandle add(List<Runnable> callbacks, Runnable callback) {
    Objects.requireNonNull(callback, "callback");
    callbacks.add(callback);
    return () -> callbacks.remove(callback);
  }

  private static void fire(List<Runnable> callbacks) {
    for (Runnable callback : callbacks) {
      callback.run();
    }
  }
}
