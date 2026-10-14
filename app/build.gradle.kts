plugins {
    id("portfolio.android.application")
}

android {
    namespace = "com.rsalanga.portfolio"
    defaultConfig {
        applicationId = "com.rsalanga.portfolio"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
