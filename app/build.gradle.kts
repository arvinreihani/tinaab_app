plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.version01"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.version01"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(libs.loggingInterceptor)
    implementation(libs.fragment)
    implementation(libs.mpandroidchart)
//    implementation(libs.anychart)
//    implementation(libs.achartengine)
    // افزودن وابستگی‌های تست
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
