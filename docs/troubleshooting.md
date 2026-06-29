# Troubleshooting

## Gradle Uses Java 17

Loom `1.13.6` requires Gradle to run on Java 21. Set `JAVA_HOME` to a Java 21 JDK and rerun with `--no-daemon` if an old daemon was started.

## API Compatibility Fails

Do not delete public v1.0.0 methods. Add a compatibility shim or deprecate the old method.

## License Headers Fail

Java files must start with the All Rights Reserved header shown in `CONTRIBUTING.md`.

## Generated Resources Fail

Run `:template-common:generatePortableTemplateResources`, then `validateGeneratedResources`.

## Packet Send Is Unsupported

Move loader-specific payload send, tracking, or broadcast behavior into the matching platform module unless it is represented by the portable packet contract.
