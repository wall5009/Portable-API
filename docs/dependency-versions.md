# Dependency Version Audit

Verified on 2026-06-29 against the official metadata listed below. These pins intentionally stay on the fixed Portable API target matrix instead of chasing unrelated newer Minecraft lines.

| Property | Pinned version | Verification source | Result |
|---|---:|---|---|
| `minecraft_1201` | `1.20.1` | Mojang version selected by Forge/Fabric target line | Supported target |
| `minecraft_1211` | `1.21.1` | Mojang version selected by Fabric/NeoForge target line | Supported target |
| `fabric_loader` | `0.19.3` | `https://maven.fabricmc.net/net/fabricmc/fabric-loader/maven-metadata.xml` | Present |
| `fabric_api_1201` | `0.92.9+1.20.1` | `https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml` | Present |
| `fabric_api_1211` | `0.116.12+1.21.1` | `https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml` | Present |
| `fabric_loom` | `1.13.6` | `https://maven.fabricmc.net/fabric-loom/fabric-loom.gradle.plugin/maven-metadata.xml` | Present |
| `forge_1201` | `1.20.1-47.4.20` | `https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml` | Present |
| `neoforge_1211` | `21.1.234` | `https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml` | Present |
| `neoform_1211` | `1.21.1-20240808.144430` | `https://maven.neoforged.net/releases/net/neoforged/neoform/maven-metadata.xml` | Present |
| `moddevgradle` | `2.0.141` | `https://maven.neoforged.net/releases/net/neoforged/moddev/net.neoforged.moddev.gradle.plugin/maven-metadata.xml` | Present |
| `junit` | `6.1.1` | `https://repo1.maven.org/maven2/org/junit/junit-bom/maven-metadata.xml` | Present |
| Checkstyle tool | `10.26.1` | `https://repo1.maven.org/maven2/com/puppycrawl/tools/checkstyle/maven-metadata.xml` | Present |
| Gradle Wrapper | `8.14.3` | `https://services.gradle.org/distributions/gradle-8.14.3-bin.zip` | URL returns HTTP 200 |

Later releases exist for several tools and loader lines. They were not adopted here because Portable API `1.1.0` is scoped to Minecraft `1.20.1` and `1.21.1`, with Java 17 bytecode for the 1.20.1 artifacts and Java 21 bytecode for the 1.21.1 artifacts.

Gradle dependency verification is committed in `gradle/verification-metadata.xml`. Trusted-artifact exceptions are limited to Fabric Loom outputs generated inside the Gradle user cache: the layered mapping jar, remapped Fabric API derivatives, and merged Minecraft jars. Downloaded third-party artifacts remain checksum-verified before Loom transforms them.
