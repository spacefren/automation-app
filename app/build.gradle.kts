import dagger.hilt.android.plugin.util.capitalize
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	// Core
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)

	// Compose
	alias(libs.plugins.kotlin.compose)

	// Hilt / KSP
	id("com.google.devtools.ksp") version "2.1.0-1.0.29"
	id("com.google.dagger.hilt.android")

	// Datastore
	id("com.google.protobuf") version "0.9.4"

	// Serialization
	id("org.jetbrains.kotlin.plugin.serialization")
}

android {
	namespace = "com.jxd20072.automationapp"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.jxd20072.automationapp"
		minSdk = 26
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
		create("trial") {
			signingConfig = signingConfigs.getByName("debug")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

buildscript {
	repositories {
		// other repositories...
		mavenCentral()
	}
	dependencies {
		// other plugins...
		classpath(libs.hilt.android.gradle.plugin)
	}
}

dependencies {
	// Core
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.compose.material)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	testImplementation(libs.junit)

	// Compose
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.navigation.compose)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	// Hilt / KSP
	implementation(libs.hilt.android.v252)
	implementation(libs.hilt.navigation.compose)
	ksp(libs.hilt.android.compiler.v252)
	ksp(libs.hilt.compiler)

	// Datastore
	implementation(libs.androidx.datastore)
	implementation(libs.androidx.datastore.core)
	implementation(libs.protobuf.javalite)
	implementation(libs.protobuf.kotlin.lite)

	// Serialization
	implementation(libs.kotlinx.serialization.json)

	// Coil
	implementation(libs.coil.compose)
	implementation(libs.coil.svg.v304)
	implementation(kotlin("reflect"))
}

androidComponents {
	onVariants(selector().all()) { variant ->
		afterEvaluate {
			val capName = variant.name.capitalize()
			tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
				setSource(tasks.getByName("generate${capName}Proto").outputs)
			}
		}
	}
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:4.26.1"
	}

	// Generates the Java Protobuf-lite code for the Protobufs in this project.
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				java {
					register("java") {
						option("lite")
					}
					register("kotlin") {
						option("lite")
					}
				}
			}
		}
	}
}