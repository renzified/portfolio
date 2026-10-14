plugins {
    id("portfolio.android.library")
}

android {
    namespace = "com.rsalanga.deps"
}

dependencies {
    api(libs.androidx.appcompat)
    api(libs.androidx.core.ktx)
}
