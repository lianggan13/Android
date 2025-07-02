plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.yunda.safe.plct"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yunda.safe.plct"
        minSdk = 24
        //     minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "v0.0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        debug {
//            buildConfigField "String"; "API_URL"; '"https://debug.example.com/api"'
//        }

        release {
//            isMinifyEnabled = false
            isMinifyEnabled = true // 启用混淆，减少 APK 大小并保护代码。
            isShrinkResources = true // 移除未使用的资源文件，以减小 APK 大小
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.room.common)
    implementation(libs.room.ktx)
    implementation(libs.recyclerview)
    implementation(libs.preference)
    implementation(libs.work.runtime.ktx)
//    implementation(libs.flycotablayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    kapt(libs.room.compiler)

    // https://github.com/elvishew/xLog
    implementation(libs.xlog)
    implementation("com.elvishew:xlog-libcat:1.0.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}