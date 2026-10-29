package com.rsalanga.portfolio

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroid(commonExtension: CommonExtension) {
    val javaVersion = JavaVersion.toVersion(AndroidConfig.JVM_TARGET)

    commonExtension.compileSdk {
        version = release(AndroidConfig.COMPILE_SDK)
    }
    commonExtension.defaultConfig.minSdk = AndroidConfig.MIN_SDK
    commonExtension.defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    commonExtension.compileOptions.sourceCompatibility = javaVersion
    commonExtension.compileOptions.targetCompatibility = javaVersion
    // Pre-existing lint issues are recorded here so CI only fails on new ones.
    commonExtension.lint.baseline = file("lint-baseline.xml")

    dependencies {
        add("testImplementation", libs.library("junit"))
        add("androidTestImplementation", libs.library("androidx-junit"))
        add("androidTestImplementation", libs.library("androidx-espresso-core"))
    }
}
