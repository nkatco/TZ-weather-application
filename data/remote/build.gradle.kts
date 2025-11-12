plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "nikita.lusenkov.data.remote"
    compileSdk = 35
    defaultConfig {
        minSdk = 24

        val apiKey = project.findProperty("WEATHER_API_KEY") as String?
            ?: throw GradleException("Missing WEATHER_API_KEY in gradle.properties")
        buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(libs.bundles.network)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.coroutines.core)
}