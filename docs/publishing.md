# Publishing

Run:

```powershell
.\gradlew.bat clean fullDistributionBuild
```

The Maven staging repository is written to `build/staging-maven`.

Published API coordinates:

- `dev.portablemc:portable-api-api-core:1.1.0`
- `dev.portablemc:portable-api-api-mc-1201:1.1.0`
- `dev.portablemc:portable-api-api-mc-1211:1.1.0`
- `dev.portablemc:portable-api-platform-fabric-1201:1.1.0`
- `dev.portablemc:portable-api-platform-forge-1201:1.1.0`
- `dev.portablemc:portable-api-platform-fabric-1211:1.1.0`
- `dev.portablemc:portable-api-platform-neoforge-1211:1.1.0`

POM license metadata is `All Rights Reserved`.

Remote Maven publishing uses `MAVEN_PUBLISH_URL`, `MAVEN_USERNAME`, and `MAVEN_PASSWORD`.
