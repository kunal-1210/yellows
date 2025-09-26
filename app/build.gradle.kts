plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.yellows"
    compileSdk = 36
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.yellows"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        android.buildFeatures.buildConfig = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField ("String", "SUPABASE_URL", "\"https://qfdlundvppduuigyixrm.supabase.co\"")
        buildConfigField( "String", "SUPABASE_API_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFmZGx1bmR2cHBkdXVpZ3lpeHJtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg3OTk4NjgsImV4cCI6MjA3NDM3NTg2OH0.gnpWVgcrwwjy0CvThtDm5WK4tlUUOeVLfMMDgQjfZC4\"")

        buildConfigField ("String", "NEWS_API_KEY", "\"7fc7b201c5de4425ae6197704f99be15\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

}