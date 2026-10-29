plugins {
    id("portfolio.android.application")
    id("portfolio.android.compose")
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
}