plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.idcma_project_prm392"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.idcma_project_prm392"
        minSdk = 24
        targetSdk = 36
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

    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Thư viện tiện ích thêm
    implementation("com.squareup.picasso:picasso:2.8") // load ảnh chứng chỉ
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Thư viện WorkManager
    implementation("androidx.work:work-runtime:2.9.0")
    
    // Gson for TypeConverter (convert List<String> to JSON)
    implementation("com.google.code.gson:gson:2.10.1")
}
