@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://maven.pkg.github.com/ommiao/Library")
            val propertiesFile = file("local.properties")
            val properties = java.util.Properties()
            properties.load(propertiesFile.inputStream())
            credentials {
                username = properties["gpr.usr"] as String
                password = properties["gpr.key"] as String
            }
        }
    }
}
rootProject.name = "Library"
include(":example")
include(":theme")
include(":colorpicker")
include(":overscroll")
include(":navigation")
