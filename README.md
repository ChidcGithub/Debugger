# Debugger

> [!TIP] This project is fully written by DeepSeek for test purposes.

A real-time Android `logcat` viewer built with **Kotlin + Jetpack Compose** (frontend) and **Rust** (backend engine), communicating via **JNI**.

## Architecture

```
┌─────────────────────────────────────┐
│  Android App (Kotlin / Jetpack Compose)
│  ┌─────────┐ ┌──────────┐ ┌───────┐
│  │ LogList │ │LogDetail │ │Export │
│  │ Screen  │ │  Screen  │ │Screen │
│  └────┬────┘ └──────────┘ └───────┘
│       │      LogViewModel
│       │      RustBridge (JNI)
├───────┼─────────────────────────────┤
│  JNI  │  (JSON strings, callbacks)  │
├───────┼─────────────────────────────┤
│  Rust Core (libdebugger_core.so)
│  ┌────────┐ ┌──────┐ ┌──────────┐
│  │ logcat │→│parser│→│ storage  │
│  │capture │ │ .rs  │ │ (SQLite) │
│  └────────┘ └──────┘ └─────┬────┘
│  ┌──────────┐ ┌────────┐   │
│  │classifier│ │exporter│←──┘
│  │  .rs     │ │  .rs   │
│  └──────────┘ └────────┘
└─────────────────────────────────────┘
```

**Data flow:** `logcat` process → Rust parses lines → SQLite storage → JSON callback → Kotlin ViewModel → Compose UI

## Features

- **Real-time capture** — spawns `logcat -v threadtime -b all`, streams entries as they arrive
- **Dual-layer filtering** — SQL WHERE clause (Rust) for paginated queries + in-memory client-side filter for instant UI response
- **Log level filter** — toggle Verbose / Debug / Info / Warn / Error / Fatal
- **Keyword search** — searches both tag and message fields
- **PID filter** — filter by process ID
- **Export** — TXT, JSON, CSV formats, saved to Downloads via MediaStore
- **Log detail view** — full message with monospace font, copy to clipboard
- **Statistics** — total count, per-level breakdown, top 10 tags
- **Material 3** — dynamic color on Android 12+, dark/light theme

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM (AndroidViewModel + StateFlow) |
| Backend Engine | Rust (cdylib) |
| Database | SQLite (rusqlite, WAL mode) |
| Bridge | JNI (jni crate + Kotlin `external` declarations) |
| Serialization | serde_json (Rust) ↔ org.json (Kotlin) |
| Build (Android) | Gradle KTS, AGP 8.7.3, Kotlin 2.0.21 |
| Build (Rust) | Cargo, NDK cross-compile for arm64-v8a |

## Project Structure

```
Debugger/
├── app/                          # Android application
│   ├── build.gradle.kts
│   └── src/main/java/com/debugger/app/
│       ├── MainActivity.kt       # Single-Activity, screen navigation
│       ├── DebuggerApplication.kt
│       ├── bridge/RustBridge.kt  # JNI declarations + callbacks
│       ├── model/LogEntry.kt     # Log entry data class
│       ├── viewmodel/LogViewModel.kt
│       └── ui/
│           ├── screens/          # LogList, LogDetail, Export
│           ├── components/       # LogItem, FilterBar, FloatingActions
│           └── theme/            # Material 3 theme, colors, typography
├── rust-core/                    # Rust native library
│   ├── Cargo.toml
│   └── src/
│       ├── lib.rs                # JNI entry points, Java callbacks
│       ├── parser.rs             # LogEntry + logcat line regex
│       ├── logcat.rs             # Spawn/manage logcat subprocess
│       ├── classifier.rs         # LogFilter → SQL WHERE builder
│       ├── exporter.rs           # TXT / JSON / CSV export
│       └── storage.rs            # SQLite init, CRUD, stats
├── gradle/
│   └── libs.versions.toml        # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Building

### Prerequisites

- Android Studio (or Android SDK 26+, NDK)
- Rust toolchain (`rustup`)
- `cargo-ndk` for cross-compilation

### Build Rust native library

```bash
cd rust-core
cargo ndk -t arm64-v8a -o ../app/src/main/jniLibs build --release
```

### Build Android APK

```bash
./gradlew assembleDebug
```

## License

MIT
