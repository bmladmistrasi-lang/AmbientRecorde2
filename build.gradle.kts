plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "id.bml.ambientrecorder"
    compileSdk = 35
    defaultConfig {
        applicationId = "id.bml.ambientrecorder"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2-test30m"
    }
}
