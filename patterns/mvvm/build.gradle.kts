plugins {
    id("portfolio.android.application")
}

android {
    namespace = "com.rsalanga.mvvm"
    defaultConfig {
        applicationId = "com.rsalanga.mvvm"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":common:deps"))
}
