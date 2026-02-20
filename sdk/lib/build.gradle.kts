/*!
 * Copyright (c) Myra Security GmbH 2026.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish")
}

val sdkVersion by extra("1.0.0")
val sdkName by extra("eu-captcha-android")

android {
    namespace = "eu.eucaptcha.android.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 16
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")
        buildConfigField("String", "SDK_NAME", "\"$sdkName\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

afterEvaluate {
    tasks.register("copyAar", Copy::class) {
        from(layout.buildDirectory.dir("outputs/aar"))
        into(layout.projectDirectory.dir("../aar-repo"))
        include("*.aar")
    }

    mavenPublishing {
        coordinates("eu.eucaptcha.android", sdkName, sdkVersion)

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
        signAllPublications()

        pom {
            name.set("EU Captcha Android SDK")
            description.set("A SDK for integrating EU Captcha into your Android apps.")
            url.set("https://github.com/Myra-Security-GmbH/eu-captcha-android-sdk")
            inceptionYear.set("2024")

            licenses {
                license {
                    name.set("Mozilla Public License Version 2.0")
                    url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                }
            }

            developers {
                developer {
                    id.set("eucaptcha")
                    name.set("EU Captcha Developers")
                    email.set("dev@eu-captcha.eu")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/Myra-Security-GmbH/eu-captcha-android-sdk.git")
                developerConnection.set("scm:git:ssh://github.com/Myra-Security-GmbH/eu-captcha-android-sdk.git")
                url.set("https://github.com/Myra-Security-GmbH/eu-captcha-android-sdk")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.androidx.rules)
    testImplementation(libs.junit)
    testImplementation(libs.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.core)
}
