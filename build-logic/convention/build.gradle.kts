plugins {
    `kotlin-dsl`
}

group = "com.rsalanga.portfolio.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
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
        register("androidCompose") {
            id = "portfolio.android.compose"
            implementationClass = "com.rsalanga.portfolio.AndroidComposeConventionPlugin"
        }
        register("kmpLibrary") {
            id = "portfolio.kmp.library"
            implementationClass = "com.rsalanga.portfolio.KmpLibraryConventionPlugin"
        }
    }
}
