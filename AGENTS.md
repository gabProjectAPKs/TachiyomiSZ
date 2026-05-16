# TACHIYOMI (SY) — Agent Guide

## Build & test

| Task | Command |
|------|---------|
| Debug APK | `./gradlew assembleDevDebug` |
| Release APK | `./gradlew assembleStandardRelease` (needs Firebase secrets) |
| Format check | `./gradlew spotlessCheck` |
| Auto-format | `./gradlew spotlessApply` |
| Unit tests | `./gradlew testDevDebugUnitTest` |
| Single module | `./gradlew :domain:test` |
| CI gate (PR) | `spotlessCheck assembleDevDebug` |

Formatting is enforced in CI — run `spotlessApply` before committing.

## DI: Injekt + Koin bridge

The app uses **both** Injekt (upstream) and Koin (SY additions). `InjektKoinBridge` connects them. New modules may use either — check surrounding code for which pattern to follow.

## SY markers

Fork-specific additions are fenced with `// SY -->` and `// SY <--` comments in Gradle files and source. Keep these fences intact when modifying SY-specific code.

## Version catalogs

Dependencies are split across **4 TOML files** in `gradle/`:
- `libs.versions.toml` — main third-party
- `androidx.versions.toml` — AndroidX + AGP
- `compose.versions.toml` — Compose BOM
- `kotlinx.versions.toml` — Kotlin + KotlinX
- `sy.versions.toml` — SY-specific (Koin, SQLCipher, Google APIs)

## Build flavors

| Flavor | Firebase | appId |
|--------|----------|-------|
| `standard` | Yes (needs `google-services.json`) | `eu.kanade.tachiyomi` |
| `fdroid` | No | `eu.kanade.tachiyomi` |
| `dev` | No | `eu.kanade.tachiyomi.dev` |

## Testing stack

JUnit 5 (`useJUnitPlatform`), Kotest assertions (`shouldBe`), MockK (`mockk`/`coEvery`/`spyk`), kotlinx-coroutines-test. Tests live under `src/test/` — no instrumentation tests found.

## Generated code

SQLDelight (`.sq` files), Moko resource accessors, and `generateLocalesConfig` run automatically during build. No manual codegen step.

## Code style

- ktlint with IntelliJ IDEA code style (`.editorconfig`)
- 4-space indent for Kotlin, 2-space for most else
- Max line length 120 for Kotlin
- Trailing commas required
- Star imports disabled (threshold = `Int.MAX_VALUE`)
- Many ktlint rules disabled (class-signature, function-naming, filename, parameter comments, etc.)

## Module map

| Module | Purpose |
|--------|---------|
| `:app` | Main APK, entrypoints (`App.kt`, `MainActivity.kt`), UI |
| `:domain` | Business logic, interactors, model interfaces |
| `:data` | Database (SQLDelight), repositories, sync, download |
| `:core:common` | Shared utilities (networking, serialization, I/O) |
| `:source-api` | Extension source API (KMP, Android target only) |
| `:source-local` | Local file source (KMP, Android target only) |
| `:i18n`, `:i18n-sy` | Base + SY translations (Moko resources) |
| `:presentation-core` | Shared Compose components, theming |
| `:presentation-widget` | Homescreen widgets (Glance) |
| `:macrobenchmark` | Baseline profiles, startup benchmarks |

## Key entry points

- `eu.kanade.tachiyomi.App` — Application class, DI init, lifecycle
- `eu.kanade.tachiyomi.ui.main.MainActivity` — Compose + Voyager navigation root

## Misc

- Min SDK 23, target 34, compile 35, JDK 17
- Compose compiler built into Kotlin 2.1.10 (no standalone compiler plugin)
- `-Xcontext-receivers` enabled globally
- Kotlin Multiplatform only uses `androidTarget()` (no iOS)
- Firebase only in `standard` flavor — building without it is fine for dev
