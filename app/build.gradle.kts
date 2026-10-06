plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jaykoo.adaptivekeyboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jaykoo.adaptivekeyboard"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}
