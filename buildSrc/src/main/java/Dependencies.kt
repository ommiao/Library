import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

object Versions {
    const val appVersionCode = 1
    const val appVersionName = "1.0.0"

    const val compileSdk = 33
    const val minSdk = 23
    const val targetSdk = 33
    const val compose = "1.3.1"
    const val composeCompiler = "1.3.1"
    const val accompanist = "0.28.0"
    const val composeNavigation = "2.5.3"
}

object Libs {
    const val androidxCore = "androidx.core:core-ktx:1.9.0"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:1.7.10"

    const val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    const val composeActivity = "androidx.activity:activity-compose:1.6.0"

    const val composeNavigation = "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
    const val accompanistSystemUi = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
    const val accompanistInsets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
}

object Modules {
    val DependencyHandler.theme
        get() = project(":theme")

    val DependencyHandler.colorPicker
        get() = project(":colorpicker")

    val DependencyHandler.overscroll
        get() = project(":overscroll")

    val DependencyHandler.navigation
        get() = project(":navigation")
}
