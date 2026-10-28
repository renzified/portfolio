# Portfolio Android Project

A multi-module Android portfolio project demonstrating various architecture patterns and modern Android development practices using Jetpack Compose, Kotlin, and Gradle Kotlin DSL.

## Compatibility Matrix & Version Catalog

This project maintains strict version alignment across modules using a centralized Gradle version catalog (`gradle/libs.versions.toml`) and convention plugins.

| Dependency / Tool | Version | Compatibility Notes |
| :--- | :--- | :--- |
| **Gradle** | `9.6.0` | Build tool (configured via Gradle wrapper) |
| **Android Gradle Plugin (AGP)** | `9.4.1` | Build system |
| **Kotlin** | `2.4.10` | Language & compiler (via Compose compiler plugin) |
| **Compose BOM** | `2026.09.00` | Jetpack Compose Bill of Materials |
| **Material 3** | `1.5.0-alpha26` | Material Design components |
| **Activity Compose** | `1.13.0` | Compose integration for Activities |
| **Core KTX** | `1.19.1` | AndroidX Core KTX |
| **AppCompat** | `1.8.0` | AppCompat library |
| **Material** | `1.14.0` | Material Components for Android |

### Android SDK & JVM Target Configuration
Configured centrally in [AndroidConfig.kt](build-logic/convention/src/main/kotlin/com/rsalanga/portfolio/AndroidConfig.kt):

- **Compile SDK:** 37
- **Target SDK:** 37
- **Min SDK:** 26 (Android 8.0 Oreo)
- **JVM Target:** 11

---

## Project Structure

- `:common:deps` — Dependency aggregator exposing shared AndroidX libraries transitively.
- `:common:uikit` — Shared UI components and design system elements.
- `:patterns:<name>` — Standalone application modules demonstrating specific architecture patterns (e.g., `:patterns:mvvm`).
- `build-logic/` — Custom Gradle convention plugins enforcing build consistency (`portfolio.android.application`, `portfolio.android.library`, `portfolio.android.compose`).

---

## Getting Started & Build Commands

Use `./gradlew` (macOS/Linux) or `gradlew.bat` (Windows):

```sh
# Build a pattern app
./gradlew :patterns:mvvm:assembleDebug

# Install a pattern app on a connected device/emulator
./gradlew :patterns:mvvm:installDebug

# Run unit tests for a module
./gradlew :common:uikit:testDebugUnitTest

# Run Android lint
./gradlew :common:uikit:lintDebug
```
