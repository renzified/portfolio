plugins {
    id("portfolio.kmp.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    android {
        namespace = "com.rsalanga.roadmap.shared.data"
    }

    // The iOS app links a single framework, so it re-exports the domain module.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(project(":roadmap:shared:domain"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":roadmap:shared:domain"))
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64").forEach { configuration ->
        add(configuration, libs.androidx.room.compiler)
    }
}
