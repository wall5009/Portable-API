package dev.portablemc.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.portablemc.api.registry.Identifier;
import org.junit.jupiter.api.Test;

final class IdentifierTest {
  @Test
  void parsesNamespaceAndPath() {
    Identifier id = Identifier.parse("portable_api:runtime/test");

    assertEquals("portable_api", id.namespace());
    assertEquals("runtime/test", id.path());
    assertEquals("portable_api:runtime/test", id.toString());
  }

  @Test
  void rejectsInvalidIdentifiers() {
    assertThrows(IllegalArgumentException.class, () -> Identifier.parse("missing_namespace"));
    assertThrows(IllegalArgumentException.class, () -> new Identifier("Bad", "path"));
    assertThrows(IllegalArgumentException.class, () -> new Identifier("portable_api", "bad path"));
  }
}
