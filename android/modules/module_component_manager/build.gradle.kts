import tornaco.project.android.thanox.Configs
import tornaco.project.android.thanox.Configs.resPrefix
import tornaco.project.android.thanox.Libs

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        minSdk = Configs.minSdkVersion
        compileSdk = Configs.compileSdkVersion
        targetSdk = Configs.targetSdkVersion
        testInstrumentationRunner = Configs.testRunner
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = false
        buildConfig = true
        aidl = true
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = true
        dataBinding = true
    }

    resourcePrefix = resPrefix
}
dependencies {
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.AndroidX.androidXCore)

    implementation(Libs.AndroidX.appCompat)
    implementation(Libs.AndroidX.material)
    implementation(Libs.AndroidX.recyclerview)
    implementation(Libs.AndroidX.preference)
    implementation(Libs.AndroidX.constraint)
    implementation(Libs.AndroidX.swipeRefreshLayout)

    implementation(Libs.Others.guavaAndroid)

    implementation(project(":modules:module_common"))
    implementation(project(":android_framework:base"))

    compileOnly(project(":annotation_processors:permission-requester-annotation"))
    add("kapt", project(":annotation_processors:permission-requester-compiler"))
    annotationProcessor(project(":annotation_processors:permission-requester-compiler"))

    implementation(project(":third_party:recyclerview-fastscroll"))
    implementation(project(":third_party:search"))

}