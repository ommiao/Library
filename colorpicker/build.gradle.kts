import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    with(Libs) {
        api(composeUi)
        api(composeMaterial)
        api(accompanistSystemUi)
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

artifacts {
    archives(sourcesJar)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "cn.ommiao.library"
                artifactId = "color-picker"
                version = "1.0.0-alpha06"
                artifact(sourcesJar)
            }
        }
        repositories {
            maven {
                url = uri("https://maven.pkg.github.com/ommiao/Library")
                credentials {
                    username = gradleLocalProperties(rootDir).getProperty("gpr.usr")
                    password = gradleLocalProperties(rootDir).getProperty("gpr.key")
                }
            }
        }
    }
}
