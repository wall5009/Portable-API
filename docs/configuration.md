# Configuration

v1.1 typed config files use `PortableTypedConfigSpec`.

Supported value types:

- boolean
- integer, long, double with ranges
- string with a maximum length
- enum by constant name
- bounded list of strings

`PortableConfigHandle` exposes immutable snapshots, load/save/reload, recovery warnings, reload listeners, thread-safe snapshot access, and atomic replacement when supported by the file system.

Malformed values are replaced with defaults and recorded in `warnings()`. Unknown keys are removed on recovery save.

Only `PortableConfigScope.GLOBAL` is portable in v1.1 because every supported loader maps it to the normal config directory with equivalent lifetime.
