package com.rsalanga.portfolio

import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Applies ktlint and detekt. Rule tweaks live in the root `.editorconfig` (ktlint)
 * and `config/detekt/detekt.yml` (detekt, layered on top of the default config).
 */
internal fun Project.configureCodeQuality() {
    pluginManager.apply(libs.pluginId("ktlint"))
    pluginManager.apply(libs.pluginId("detekt"))

    extensions.configure<KtlintExtension> {
        version.set(libs.findVersion("ktlint").get().requiredVersion)
        android.set(true)
        // KSP (e.g. Room) adds its output to the source sets; don't lint generated code.
        val generatedDir = layout.buildDirectory.dir("generated").get().asFile
        filter {
            exclude { it.file.startsWith(generatedDir) }
        }
    }

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig.set(true)
        parallel.set(true)
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    }
}
