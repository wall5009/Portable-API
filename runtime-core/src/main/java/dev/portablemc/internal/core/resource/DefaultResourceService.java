package dev.portablemc.internal.core.resource;

import dev.portablemc.api.event.EventHandle;
import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.resource.ResourceReloadListener;
import dev.portablemc.api.resource.ResourceService;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Stores resource reload listeners for platform adapters. */
public final class DefaultResourceService implements ResourceService {
  private final List<Entry> serverListeners = new CopyOnWriteArrayList<>();
  private final List<Entry> clientListeners = new CopyOnWriteArrayList<>();

  @Override
  public EventHandle registerServerReloadListener(Identifier id, ResourceReloadListener listener) {
    return add(serverListeners, id, listener);
  }

  @Override
  public EventHandle registerClientReloadListener(Identifier id, ResourceReloadListener listener) {
    return add(clientListeners, id, listener);
  }

  public List<Entry> serverListeners() {
    return List.copyOf(serverListeners);
  }

  public List<Entry> clientListeners() {
    return List.copyOf(clientListeners);
  }

  private static EventHandle add(
      List<Entry> entries, Identifier id, ResourceReloadListener listener) {
    Entry entry = new Entry(id, listener);
    entries.add(entry);
    return () -> entries.remove(entry);
  }

  public record Entry(Identifier id, ResourceReloadListener listener) {
    public Entry {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(listener, "listener");
    }
  }
}
