/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.google.devtools.ksp)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "org.override.atomo"
    compileSdk {
        version = release(36)
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "org.override.atomo"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    target {
        compilerOptions {
            optIn.add("kotlin.RequiresOptIn")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.android)
    implementation(libs.material.icons.ext)
    implementation("androidx.graphics:graphics-core:1.0.4")
    implementation("androidx.graphics:graphics-path:1.1.0-beta01")
    implementation("androidx.graphics:graphics-shapes:1.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Android / utilidades
    implementation(libs.core.splashscreen)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // Compose extras
    implementation(libs.compose.shimmer)
    implementation(libs.material.kolor)
    implementation(libs.qrose)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.compose.material3.android)

    // Supabase (usar BOM)
    implementation(platform(libs.supabase.bom))
    implementation(libs.auth.kt)
    implementation(libs.realtime.kt)
    implementation(libs.postgrest.kt)
    implementation(libs.storage.kt)

    // Kotlin extras
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)

    // Koin (usar BOM)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.navigation3)

    // Ktor
    implementation(platform(libs.ktor.bom))
    implementation(libs.coil.network.ktor3)
    implementation(libs.ktor.client.android)

    // Coil
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.compose)

    // FileKit
    implementation(libs.filekit.core)
    implementation(libs.filekit.dialogs)
    implementation(libs.filekit.dialogs.compose)
    implementation(libs.filekit.coil)

    // Otras
    implementation(libs.compressor)

    // Data
    implementation(libs.datastore.pref)
    implementation(libs.datastore.pref.core)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Google
    implementation(libs.googleid)
    implementation(libs.cred.play.services)
    implementation(libs.credentials)

    // Navigation 3 & lifecycle
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}