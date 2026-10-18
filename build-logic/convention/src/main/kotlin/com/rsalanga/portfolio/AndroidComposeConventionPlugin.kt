package com.rsalanga.portfolio

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Enables Jetpack Compose on a module that already applies
 * `portfolio.android.application` or `portfolio.android.library`.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.pluginId("kotlin-compose"))

            pluginManager.withPlugin(libs.pluginId("android-application")) {
                extensions.configure<ApplicationExtension> {
                    configureCompose(this)
                }
                dependencies {
                    add("implementation", libs.library("androidx-activity-compose"))
                }
            }
            pluginManager.withPlugin(libs.pluginId("android-library")) {
                extensions.configure<LibraryExtension> {
                    configureCompose(this)
                }
            }
        }
    }
}

private fun Project.configureCompose(commonExtension: CommonExtension) {
    commonExtension.buildFeatures.compose = true

    dependencies {
        val bom = platform(libs.library("androidx-compose-bom"))
        add("implementation", bom)
        add("androidTestImplementation", bom)

        add("implementation", libs.library("androidx-compose-ui"))
        add("implementation", libs.library("androidx-compose-ui-graphics"))
        add("implementation", libs.library("androidx-compose-ui-tooling-preview"))
        add("implementation", libs.library("androidx-compose-material3"))
        add("debugImplementation", libs.library("androidx-compose-ui-tooling"))
        add("debugImplementation", libs.library("androidx-compose-ui-test-manifest"))
        add("androidTestImplementation", libs.library("androidx-compose-ui-test-junit4"))
    }
}
