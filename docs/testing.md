# Testing

## Local Test Commands

```powershell
./gradlew.bat test
./gradlew.bat validatePlatformDescriptors
./gradlew.bat verifyCommonPurity
./gradlew.bat validateAllTargets
```

## Test Coverage

- `portable-api` tests validate pure API value objects and bootstrap behavior.
- `runtime-core` tests validate context creation and command declaration storage.
- `validatePlatformDescriptors` verifies all four targets expose equivalent required v1 surfaces.
- `verifyCommonPurity` prevents loader API dependencies and imports from leaking into API, runtime-core, and common modules.
- Platform and validation builds verify compile compatibility with all requested loader/version combinations.

## Launch Validation

The validation modules are intentionally blank. They are present only to compile and launch against the intermediary API on each supported target.
