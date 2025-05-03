// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Используем alias из libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false // Объявляем плагин библиотеки
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false // Объявляем KSP
    alias(libs.plugins.google.gms.google.services) apply false // Объявляем Google Services
}
true // Необходимо для некоторых версий Gradle с Kotlin DSL