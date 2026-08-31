// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // AGP 9 trae soporte de Kotlin integrado: ya no se aplica
    // org.jetbrains.kotlin.android por separado.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
