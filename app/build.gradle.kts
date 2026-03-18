plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.sky.chessplay"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sky.chessplay"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "env"

    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://10.0.2.2:8088/api/v1/\""
            )
            manifestPlaceholders["app_name"] = "Chess Play (Dev)"
        }

        create("prod") {
            dimension = "env"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://api.myapp.com\""
            )
            manifestPlaceholders["appName"] = "ChessPlay"
        }
    }

}

kotlin {
    jvmToolchain(17)
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    // Platform / BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Core
    implementation(libs.core.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidsvg)
    debugImplementation(libs.androidx.ui.tooling)

    // Navigation & Lifecycle
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt (The Fix Version 2.56.2)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Storage
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Junit test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    //Accompanist
    implementation(libs.accompanist.drawable)

    // Third Party
    implementation(libs.coil.compose)
    implementation(libs.tabler.icons)
}

