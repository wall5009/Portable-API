package dev.portablemc.api.runtime;

import java.util.Objects;

/** Semantic version used for Portable API compatibility declarations. */
public record SemanticVersion(int major, int minor, int patch, String qualifier)
    implements Comparable<SemanticVersion> {
  public SemanticVersion {
    if (major < 0 || minor < 0 || patch < 0) {
      throw new IllegalArgumentException("Version numbers must be non-negative");
    }
    qualifier = qualifier == null ? "" : qualifier;
  }

  /** Returns a release version without a qualifier. */
  public static SemanticVersion release(int major, int minor, int patch) {
    return new SemanticVersion(major, minor, patch, "");
  }

  @Override
  public int compareTo(SemanticVersion other) {
    Objects.requireNonNull(other, "other");
    int majorResult = Integer.compare(major, other.major);
    if (majorResult != 0) {
      return majorResult;
    }
    int minorResult = Integer.compare(minor, other.minor);
    if (minorResult != 0) {
      return minorResult;
    }
    int patchResult = Integer.compare(patch, other.patch);
    if (patchResult != 0) {
      return patchResult;
    }
    return qualifier.compareTo(other.qualifier);
  }

  @Override
  public String toString() {
    return major + "." + minor + "." + patch + (qualifier.isBlank() ? "" : "-" + qualifier);
  }
}
