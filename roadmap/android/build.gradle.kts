plugins {
    id("portfolio.android.application")
    id("portfolio.android.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rsalanga.roadmap"
    defaultConfig {
        applicationId = "com.rsalanga.roadmap"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":roadmap:shared"))
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}