plugins {
    `kotlin-dsl`
}

group = "com.rsalanga.portfolio.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "portfolio.android.application"
            implementationClass = "com.rsalanga.portfolio.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "portfolio.android.library"
            implementationClass = "com.rsalanga.portfolio.AndroidLibraryConventionPlugin"
        }
    }
}
