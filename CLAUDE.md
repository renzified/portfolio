# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Multi-module Android portfolio project (Kotlin, Gradle Kotlin DSL, AGP 9.x). The intent is a set of standalone sample apps demonstrating architecture patterns (under `patterns/`), built on shared library modules (under `common/`). The project is at an early scaffold stage.

## Commands

Use `./gradlew` (Git Bash) or `gradlew.bat` (PowerShell/cmd). Module paths follow the directory layout, e.g. `:patterns:mvvm`, `:common:uikit`.

```sh
./gradlew :patterns:mvvm:assembleDebug        # build a pattern app
./gradlew :patterns:mvvm:installDebug         # install on a connected device/emulator
./gradlew :common:uikit:testDebugUnitTest     # JVM unit tests for one module
./gradlew :common:uikit:testDebugUnitTest --tests "com.rsalanga.uikit.ExampleUnitTest"            # single test class
./gradlew :common:uikit:testDebugUnitTest --tests "com.rsalanga.uikit.ExampleUnitTest.addition_isCorrect"  # single test method
./gradlew :common:uikit:connectedDebugAndroidTest  # instrumented tests (needs device)
./gradlew :common:uikit:lintDebug             # Android lint
```

```sh
./gradlew ktlintCheck detekt lint test         # everything CI runs on a PR
./gradlew ktlintFormat                         # auto-fix ktlint violations
./gradlew :roadmap:android:updateLintBaseline --no-configuration-cache  # re-record lint baseline
```

ktlint uses the `android_studio` style with overrides in `.editorconfig`; detekt layers `config/detekt/detekt.yml` over its defaults. Pre-existing Android Lint issues live in each module's `lint-baseline.xml`. Configuration cache is enabled.

CI (`.github/workflows/android-ci.yml`) runs Ktlint, Detekt, Android Lint and Tests as separate checks on every PR to `master`.

## Architecture

### Module graph
- `:common:deps` — dependency aggregator. Exposes shared AndroidX libs via `api(...)` so consumers get them transitively. Add libraries that every module needs here rather than to each module.
- `:common:uikit` — shared UI components; depends on `:common:deps`.
- `:patterns:<name>` — each pattern is its own **application** module with its own `applicationId` (e.g. `com.rsalanga.mvvm`), depending on `:common:*`. New patterns go here as siblings of `mvvm`, and must be registered in `settings.gradle.kts`.
- `app/` exists on disk but is **not included** in `settings.gradle.kts`, so it is not part of the build.

### Build logic (`build-logic/`)
Included build (`pluginManagement { includeBuild("build-logic") }`) providing convention plugins. Module build files should apply these instead of the raw AGP plugins:
- `portfolio.android.application` → `AndroidApplicationConventionPlugin` (sets targetSdk; release build has `optimization.enable = false`)
- `portfolio.android.library` → `AndroidLibraryConventionPlugin`
- `portfolio.android.compose` → `AndroidComposeConventionPlugin` — applied *alongside* one of the above; enables Compose and adds the Compose BOM/material3/tooling deps (plus `activity-compose` for applications)
- `portfolio.kmp.library` → `KmpLibraryConventionPlugin` — Kotlin Multiplatform library (Android via AGP's `com.android.kotlin.multiplatform.library`, plus `iosArm64`/`iosSimulatorArm64`); adds `kotlin-test` to `commonTest`. Modules set `namespace` in `kotlin { android { } }`. Example: `:roadmap:shared`

All three base plugins also call `configureCodeQuality()` (`CodeQualityConvention.kt`), which applies ktlint and detekt; `configureAndroid()` adds the lint baseline and JUnit/Espresso test deps.

Convention plugins resolve plugin IDs and libraries from the version catalog via the `Project.libs` helpers in `VersionCatalog.kt` (`libs.pluginId(...)`, `libs.library(...)`) rather than hardcoding coordinates.

Both call `configureAndroid()` in `AndroidConvention.kt`. SDK levels and JVM target live in one place: `AndroidConfig.kt` (compileSdk/targetSdk 37, minSdk 26, JVM 11). Change them there, not in module build files. Module build files only set `namespace`, `applicationId`/version, and dependencies.

There is no explicit Kotlin Gradle plugin — AGP 9's built-in Kotlin support compiles Kotlin sources. The effective Kotlin version is set by the `kotlin` catalog version: the Compose compiler plugin (declared `apply false` in the root `build.gradle.kts`) pulls KGP up to that version, so the Compose plugin and Kotlin always match.

### Dependencies
All versions are in `gradle/libs.versions.toml`; `build-logic` reads the same catalog. Repositories are centrally declared with `FAIL_ON_PROJECT_REPOS`, so don't add `repositories {}` in module build files.

### R8 keep rules
Keep rules use the AGP 9 layout: `src/main/keepRules/*.keep` (combined automatically), with `consumer-rules.keep` in library modules — not `proguard-rules.pro`.
