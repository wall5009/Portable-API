# Setup

Use Java 21 to run Gradle.

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat check
```

Open the repository root in IntelliJ as a Gradle project. The committed `.idea/runConfigurations` entries are Gradle run configurations and use `$PROJECT_DIR$`.

Do not commit generated `runs/`, `.gradle/`, `build/`, workspace state, credentials, caches, or local paths.
