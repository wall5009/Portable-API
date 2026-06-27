package dev.portablemc.internal.core.registry;

import dev.portablemc.api.registry.CreativeTabService;
import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.registry.RegistrySupplier;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Stores creative tab population requests for platform adapters. */
public final class DefaultCreativeTabService implements CreativeTabService {
  private final List<Entry> entries = new CopyOnWriteArrayList<>();

  @Override
  public EventHandleLike add(Identifier tabId, RegistrySupplier<?> entry) {
    Entry request = new Entry(tabId, entry);
    entries.add(request);
    return () -> entries.remove(request);
  }

  public List<Entry> entries() {
    return List.copyOf(entries);
  }

  public record Entry(Identifier tabId, RegistrySupplier<?> entry) {
    public Entry {
      Objects.requireNonNull(tabId, "tabId");
      Objects.requireNonNull(entry, "entry");
    }
  }
}
