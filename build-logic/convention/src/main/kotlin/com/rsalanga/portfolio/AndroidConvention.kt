package com.rsalanga.portfolio

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureAndroid(commonExtension: CommonExtension) {
    val javaVersion = JavaVersion.toVersion(AndroidConfig.JVM_TARGET)

    commonExtension.compileSdk {
        version = release(AndroidConfig.COMPILE_SDK)
    }
    commonExtension.defaultConfig.minSdk = AndroidConfig.MIN_SDK
    commonExtension.defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    commonExtension.compileOptions.sourceCompatibility = javaVersion
    commonExtension.compileOptions.targetCompatibility = javaVersion
}
