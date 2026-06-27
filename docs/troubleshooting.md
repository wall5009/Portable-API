# Troubleshooting

## Gradle Cannot Find Java 21

Use a JDK 21 Gradle JVM or configure Gradle toolchains to point at an installed JDK 21. The repository includes `gradle/gradle-daemon-jvm.properties` with `toolchainVersion=21`; Gradle wrapper launches can use this to select a Java 21 daemon when one is installed.

## Loom Fails Resolving Unpick on Java 17

If project import fails with `net.fabricmc.unpick:unpick` or `unpick-format-utils` requiring JVM runtime version 21, the Gradle daemon is running on Java 17. This happens before Java toolchains can compile any module.

Fix the IDE Gradle JVM:

- IntelliJ IDEA: **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM** = JDK 21.
- Shell: install JDK 21 and run through the checked-in wrapper; the daemon JVM criteria file will select Java 21 when Gradle can discover it.

Do not downgrade source compatibility for this error. The 1.20.1 modules still emit Java 17 bytecode; only the Gradle/Loom runtime needs Java 21.

## Fabric Common Setup Callback Does Not Fire When Expected

Fabric does not expose the same common setup phase as Forge and NeoForge. Use initialization, server lifecycle events, or capability checks for work that must be precisely timed.

## Registry Binding Throws UnsupportedOperationException

Raw registry writes are limited in v1. Use registry suppliers as stable handles and keep actual content registration in target-specific code until typed adapters are added.

## A Loader Dependency Appears in Common

Run:

```powershell
./gradlew.bat verifyCommonPurity
```

Move the loader-specific code into the matching platform module.

## Metadata Fails to Expand

Run `processResources` for the failing module and inspect the generated file under that module's `build/resources/main` directory.
