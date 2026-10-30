package com.rsalanga.portfolio

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Kotlin Multiplatform library targeting Android and iOS. Modules still set
 * their own `namespace` inside `kotlin { android { ... } }`.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("kotlin-multiplatform"))
            pluginManager.apply(libs.pluginId("android-kotlin-multiplatform-library"))
            configureCodeQuality()

            extensions.configure<KotlinMultiplatformExtension> {
                extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                    compileSdk = AndroidConfig.COMPILE_SDK
                    minSdk = AndroidConfig.MIN_SDK
                    compilerOptions {
                        jvmTarget.set(JvmTarget.fromTarget(AndroidConfig.JVM_TARGET.toString()))
                    }
                    // Instrumented tests live in src/androidDeviceTest.
                    withDeviceTest {
                        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }
                }

                iosArm64()
                iosSimulatorArm64()
            }

            dependencies {
                add("commonTestImplementation", libs.library("kotlin-test"))
                add("androidDeviceTestImplementation", libs.library("androidx-junit"))
                add("androidDeviceTestImplementation", libs.library("androidx-test-runner"))
            }
        }
    }
}
