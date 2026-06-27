package dev.portablemc.api.resource;

import dev.portablemc.api.registry.Identifier;
import java.io.InputStream;
import java.util.Optional;

/** Loader-neutral read-only resource lookup used during reload callbacks. */
public interface ResourceAccess {
  /** Opens a resource stream when present. */
  Optional<InputStream> open(Identifier id);
}
