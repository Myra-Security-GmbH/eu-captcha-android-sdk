plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("org.jetbrains.dokka") version "1.9.0"
}
