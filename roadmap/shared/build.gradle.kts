plugins {
    id("portfolio.kmp.library")
}

kotlin {
    android {
        namespace = "com.rsalanga.roadmap.shared"
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
        }
    }
}
