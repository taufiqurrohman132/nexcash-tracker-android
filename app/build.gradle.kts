plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")


}

android {
    namespace = "com.example.nexcashapplication"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.nexcashapplication"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlin {
//        jvmToolchain(17)
//    }
    buildFeatures {
        compose = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.1"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        // Agar android.util.Log & android.graphics.Color (via toColor()) tidak
        // melempar "Method not mocked" pada unit test JVM murni.
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.engage.core)

    // room
    ksp("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")
    implementation("androidx.room:room-runtime:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("androidx.room:room-paging:2.7.2")

    // data store
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // hilt
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // workmanager — penjadwal latar belakang (reminder harian) + injeksi Hilt ke worker
    implementation("androidx.work:work-runtime-ktx:2.10.1")
    implementation("androidx.hilt:hilt-work:1.3.0")
    ksp("androidx.hilt:hilt-compiler:1.3.0")

    // Navigation 3 core
//    implementation("androidx.navigation3:navigation3-ui:1.0.0")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Kalau pakai ViewModel di tiap screen
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.9.0")
    implementation("androidx.compose.foundation:foundation:1.4.0")

    // coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation(libs.kotlinx.serialization.json)

    implementation("com.google.code.gson:gson:2.14.0")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // chart
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    // date
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")

    // color picker
    implementation("io.mhssn:colorpicker:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // ViewModel test: Dispatchers.setMain, runTest, virtual time (debounce)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.8")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.withType<Test> {
    useJUnitPlatform()
}