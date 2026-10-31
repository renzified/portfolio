plugins {
    id("portfolio.kmp.library")
}

kotlin {
    android {
        namespace = "com.rsalanga.roadmap.shared.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.coroutines.core)
        }
    }
}
