// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.dagger.hilt.android") version "2.58" apply false
    id("com.google.devtools.ksp") version "2.3.2" apply false
    id("com.google.protobuf") version "0.9.4" apply false
    kotlin("plugin.serialization") version "2.1.0" apply false
}