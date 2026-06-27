package dev.portablemc.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.portablemc.api.runtime.SemanticVersion;
import org.junit.jupiter.api.Test;

final class SemanticVersionTest {
  @Test
  void ordersByMajorMinorPatchThenQualifier() {
    assertTrue(SemanticVersion.release(1, 1, 0).compareTo(SemanticVersion.release(1, 0, 9)) > 0);
    assertTrue(
        new SemanticVersion(1, 0, 0, "beta").compareTo(SemanticVersion.release(1, 0, 0)) > 0);
    assertEquals("1.0.0", SemanticVersion.release(1, 0, 0).toString());
    assertEquals("1.0.0-rc1", new SemanticVersion(1, 0, 0, "rc1").toString());
  }
}
