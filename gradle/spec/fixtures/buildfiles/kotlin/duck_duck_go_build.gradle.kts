import org.jetbrains.kotlin.config.KotlinCompilerVersion

apply(plugin = "com.android.application")

plugins {
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
apply from = "../versioning.gradle"

ext {
    VERSION_NAME = "5.3.1"
    USE_ORCHESTRATOR = project.hasProperty("orchestrator") ? project.property("orchestrator") : false
}

android {
    compileSdkVersion(27)
    defaultConfig {
        applicationId = "com.duckduckgo.mobile.android"
        minSdkVersion(21)
        targetSdkVersion(27)
        versionCode = buildVersionCode()
        versionName = VERSION_NAME
        testInstrumentationRunner = "com.duckduckgo.app.TestRunner"
        archivesBaseName = "duckduckgo-$versionName" = versionName"

        javaCompileOptions {
            annotationProcessor(Options {)
                arguments = listOf("room.schemaLocation" = "$projectDir/schemas".toString())
            }
        }
        sourceSets {
            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
        }
    }
    signingConfigs {
        release
    }
    buildTypes {
        named("debug"){
        }
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }
    externalNativeBuild {

        cmake {
            path "CMakeLists.txt"
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.returnDefaultValues = true

        if (USE_ORCHESTRATOR) {
            execution "ANDROID_TEST_ORCHESTRATOR"
        }
    }

    apply(plugin = "com.android.application")

plugins {
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
apply from = "../versioning.gradle"

ext {
    VERSION_NAME = "5.3.1"
    USE_ORCHESTRATOR = project.hasProperty("orchestrator") ? project.property("orchestrator") : false
}

android {
    compileSdkVersion(27)
    defaultConfig {
        applicationId = "com.duckduckgo.mobile.android"
        minSdkVersion(21)
        targetSdkVersion(27)
        versionCode = buildVersionCode()
        versionName = VERSION_NAME
        testInstrumentationRunner = "com.duckduckgo.app.TestRunner"
        archivesBaseName = "duckduckgo-$versionName" = versionName"

        javaCompileOptions {
            annotationProcessor(Options {)
                arguments = listOf("room.schemaLocation" = "$projectDir/schemas".toString())
            }
        }
        sourceSets {
            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
        }
    }
    signingConfigs {
        release
    }
    buildTypes {
        named("debug"){
        }
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }
    externalNativeBuild {

        cmake {
            path "CMakeLists.txt"
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.returnDefaultValues = true

        if (USE_ORCHESTRATOR) {
            execution "ANDROID_TEST_ORCHESTRATOR"
        }
    }

    val staticConfigPath = "${System.getenv("HOME")}/jenkins_static/com.duckduckgo.mobile.android"
    val propertiesPath = "${staticConfigPath}/ddg_android_build.properties"
    val propertiesFile = new File(propertiesPath)
    if (propertiesFile.exists()) {
        println "Signing properties found"
        val props = new Properties()
        props.load(new FileInputStream(propertiesFile))
        android.signingConfigs.release.storeFile = file("${staticConfigPath}/${propslistOf("key.store")}")
        android.signingConfigs.release.storePassword = propslistOf("key.store.password")
        android.signingConfigs.release.keyAlias = propslistOf("key.alias")
        android.signingConfigs.release.keyPassword = propslistOf("key.alias.password")
    } else {
        println "Signing properties not found at ${propertiesPath}, releases will NOT succeed"
        android.buildTypes.release.signingConfig = null
    }
}

ext {
    supportLibrary = "27.1.1"
    architectureComponents = "1.0.0"
    architectureComponentsExtensions = "1.1.1"
    androidKtx = "0.3"
    dagger = "2.14.1"
    retrofit = "2.3.0"
    ankoVersion = "0.10.4"
    glide = "4.6.1"
    androidTestRunner = "1.0.2"
}


dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.5.4")
    releaseImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"

    implementation(fileTree(dir = "libs", include: listOf("*.jar")))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:$supportLibrary")
    implementation("com.android.support:design:$supportLibrary")
    implementation("com.android.support.constraint:constraint-layout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofit")
    implementation("io.reactivex.rxjava2:rxjava:2.1.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.jakewharton.timber:timber:4.6.1")
    implementation("com.google.dagger:dagger-android:$dagger")
    implementation("com.google.dagger:dagger-android-support:$dagger")
    releaseImplementation "com.faendir:acra:4.10.0"

    // RxRelay
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    // Anko
    implementation("org.jetbrains.anko:anko-commons:$ankoVersion")
    implementation("org.jetbrains.anko:anko-design:$ankoVersion")

    // Android KTX
    implementation("androidx.core:core-ktx:$androidKtx")

    // ViewModel and LiveData
    implementation("android.arch.lifecycle:extensions:$architectureComponentsExtensions")
    kapt("android.arch.lifecycle:compiler:$architectureComponents")
    testImplementation("android.arch.core:core-testing:$architectureComponents")
    androidTestImplementation("android.arch.core:core-testing:$architectureComponents")

    // Room
    implementation("android.arch.persistence.room:runtime:$architectureComponents")
    kapt("android.arch.persistence.room:compiler:$architectureComponents")
    testImplementation("android.arch.persistence.room:testing:$architectureComponents")
    androidTestImplementation("android.arch.persistence.room:testing:$architectureComponents")

    // Dagger
    kapt("com.google.dagger:dagger-android-processor:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-android-processor:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-compiler:$dagger")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glide")
    kapt("com.github.bumptech.glide:compiler:$glide")

    testImplementation("org.mockito:mockito-core:2.15.0")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testImplementation("junit:junit:4.12")

    androidTestImplementation("com.android.support.test:runner:$androidTestRunner")
    androidTestImplementation("com.android.support.test:rules:$androidTestRunner")
    androidTestUtil "com.android.support.test:orchestrator:1.0.2"
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    androidTestImplementation("org.mockito:mockito-android:2.15.0")
    androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")

}

    apply(plugin = "com.android.application")

plugins {
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
apply from = "../versioning.gradle"

ext {
    VERSION_NAME = "5.3.1"
    USE_ORCHESTRATOR = project.hasProperty("orchestrator") ? project.property("orchestrator") : false
}

android {
    compileSdkVersion(27)
    defaultConfig {
        applicationId = "com.duckduckgo.mobile.android"
        minSdkVersion(21)
        targetSdkVersion(27)
        versionCode = buildVersionCode()
        versionName = VERSION_NAME
        testInstrumentationRunner = "com.duckduckgo.app.TestRunner"
        archivesBaseName = "duckduckgo-$versionName" = versionName"

        javaCompileOptions {
            annotationProcessor(Options {)
                arguments = listOf("room.schemaLocation" = "$projectDir/schemas".toString())
            }
        }
        sourceSets {
            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
        }
    }
    signingConfigs {
        release
    }
    buildTypes {
        named("debug"){
        }
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }
    externalNativeBuild {

        cmake {
            path "CMakeLists.txt"
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.returnDefaultValues = true

        if (USE_ORCHESTRATOR) {
            execution "ANDROID_TEST_ORCHESTRATOR"
        }
    }

    val staticConfigPath = "${System.getenv("HOME")}/jenkins_static/com.duckduckgo.mobile.android"
    val propertiesPath = "${staticConfigPath}/ddg_android_build.properties"
    val propertiesFile = new File(propertiesPath)
    if (propertiesFile.exists()) {
        println "Signing properties found"
        val props = new Properties()
        props.load(new FileInputStream(propertiesFile))
        android.signingConfigs.release.storeFile = file("${staticConfigPath}/${propslistOf("key.store")}")
        android.signingConfigs.release.storePassword = propslistOf("key.store.password")
        android.signingConfigs.release.keyAlias = propslistOf("key.alias")
        android.signingConfigs.release.keyPassword = propslistOf("key.alias.password")
    } else {
        println "Signing properties not found at ${propertiesPath}, releases will NOT succeed"
        android.buildTypes.release.signingConfig = null
    }
}

ext {
    supportLibrary = "27.1.1"
    architectureComponents = "1.0.0"
    architectureComponentsExtensions = "1.1.1"
    androidKtx = "0.3"
    dagger = "2.14.1"
    retrofit = "2.3.0"
    ankoVersion = "0.10.4"
    glide = "4.6.1"
    androidTestRunner = "1.0.2"
}


dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.5.4")
    releaseImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"

    implementation(fileTree(dir = "libs", include: listOf("*.jar")))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:$supportLibrary")
    implementation("com.android.support:design:$supportLibrary")
    implementation("com.android.support.constraint:constraint-layout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofit")
    implementation("io.reactivex.rxjava2:rxjava:2.1.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.jakewharton.timber:timber:4.6.1")
    implementation("com.google.dagger:dagger-android:$dagger")
    implementation("com.google.dagger:dagger-android-support:$dagger")
    releaseImplementation "com.faendir:acra:4.10.0"

    // RxRelay
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    // Anko
    implementation("org.jetbrains.anko:anko-commons:$ankoVersion")
    implementation("org.jetbrains.anko:anko-design:$ankoVersion")

    // Android KTX
    implementation("androidx.core:core-ktx:$androidKtx")

    // ViewModel and LiveData
    implementation("android.arch.lifecycle:extensions:$architectureComponentsExtensions")
    kapt("android.arch.lifecycle:compiler:$architectureComponents")
    testImplementation("android.arch.core:core-testing:$architectureComponents")
    androidTestImplementation("android.arch.core:core-testing:$architectureComponents")

    // Room
    implementation("android.arch.persistence.room:runtime:$architectureComponents")
    kapt("android.arch.persistence.room:compiler:$architectureComponents")
    testImplementation("android.arch.persistence.room:testing:$architectureComponents")
    androidTestImplementation("android.arch.persistence.room:testing:$architectureComponents")

    // Dagger
    kapt("com.google.dagger:dagger-android-processor:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-android-processor:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-compiler:$dagger")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glide")
    kapt("com.github.bumptech.glide:compiler:$glide")

    testImplementation("org.mockito:mockito-core:2.15.0")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testImplementation("junit:junit:4.12")

    androidTestImplementation("com.android.support.test:runner:$androidTestRunner")
    androidTestImplementation("com.android.support.test:rules:$androidTestRunner")
    androidTestUtil "com.android.support.test:orchestrator:1.0.2"
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    androidTestImplementation("org.mockito:mockito-android:2.15.0")
    androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")

}

    apply(plugin = "com.android.application")

plugins {
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
apply from = "../versioning.gradle"

ext {
    VERSION_NAME = "5.3.1"
    USE_ORCHESTRATOR = project.hasProperty("orchestrator") ? project.property("orchestrator") : false
}

android {
    compileSdkVersion(27)
    defaultConfig {
        applicationId = "com.duckduckgo.mobile.android"
        minSdkVersion(21)
        targetSdkVersion(27)
        versionCode = buildVersionCode()
        versionName = VERSION_NAME
        testInstrumentationRunner = "com.duckduckgo.app.TestRunner"
        archivesBaseName = "duckduckgo-$versionName" = versionName"

        javaCompileOptions {
            annotationProcessor(Options {)
                arguments = listOf("room.schemaLocation" = "$projectDir/schemas".toString())
            }
        }
        sourceSets {
            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
        }
    }
    signingConfigs {
        release
    }
    buildTypes {
        named("debug"){
        }
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }
    externalNativeBuild {

        cmake {
            path "CMakeLists.txt"
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.returnDefaultValues = true

        if (USE_ORCHESTRATOR) {
            execution "ANDROID_TEST_ORCHESTRATOR"
        }
    }

    val staticConfigPath = "${System.getenv("HOME")}/jenkins_static/com.duckduckgo.mobile.android"
    val propertiesPath = "${staticConfigPath}/ddg_android_build.properties"
    val propertiesFile = new File(propertiesPath)
    if (propertiesFile.exists()) {
        println "Signing properties found"
        val props = new Properties()
        props.load(new FileInputStream(propertiesFile))
        android.signingConfigs.release.storeFile = file("${staticConfigPath}/${propslistOf("key.store")}")
        android.signingConfigs.release.storePassword = propslistOf("key.store.password")
        android.signingConfigs.release.keyAlias = propslistOf("key.alias")
        android.signingConfigs.release.keyPassword = propslistOf("key.alias.password")
    } else {
        println "Signing properties not found at ${propertiesPath}, releases will NOT succeed"
        android.buildTypes.release.signingConfig = null
    }
}

ext {
    supportLibrary = "27.1.1"
    architectureComponents = "1.0.0"
    architectureComponentsExtensions = "1.1.1"
    androidKtx = "0.3"
    dagger = "2.14.1"
    retrofit = "2.3.0"
    ankoVersion = "0.10.4"
    glide = "4.6.1"
    androidTestRunner = "1.0.2"
}


dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.5.4")
    releaseImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"

    implementation(fileTree(dir = "libs", include: listOf("*.jar")))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:$supportLibrary")
    implementation("com.android.support:design:$supportLibrary")
    implementation("com.android.support.constraint:constraint-layout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofit")
    implementation("io.reactivex.rxjava2:rxjava:2.1.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.jakewharton.timber:timber:4.6.1")
    implementation("com.google.dagger:dagger-android:$dagger")
    implementation("com.google.dagger:dagger-android-support:$dagger")
    releaseImplementation "com.faendir:acra:4.10.0"

    // RxRelay
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    // Anko
    implementation("org.jetbrains.anko:anko-commons:$ankoVersion")
    implementation("org.jetbrains.anko:anko-design:$ankoVersion")

    // Android KTX
    implementation("androidx.core:core-ktx:$androidKtx")

    // ViewModel and LiveData
    implementation("android.arch.lifecycle:extensions:$architectureComponentsExtensions")
    kapt("android.arch.lifecycle:compiler:$architectureComponents")
    testImplementation("android.arch.core:core-testing:$architectureComponents")
    androidTestImplementation("android.arch.core:core-testing:$architectureComponents")

    // Room
    implementation("android.arch.persistence.room:runtime:$architectureComponents")
    kapt("android.arch.persistence.room:compiler:$architectureComponents")
    testImplementation("android.arch.persistence.room:testing:$architectureComponents")
    androidTestImplementation("android.arch.persistence.room:testing:$architectureComponents")

    // Dagger
    kapt("com.google.dagger:dagger-android-processor:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-android-processor:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-compiler:$dagger")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glide")
    kapt("com.github.bumptech.glide:compiler:$glide")

    testImplementation("org.mockito:mockito-core:2.15.0")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testImplementation("junit:junit:4.12")

    androidTestImplementation("com.android.support.test:runner:$androidTestRunner")
    androidTestImplementation("com.android.support.test:rules:$androidTestRunner")
    androidTestUtil "com.android.support.test:orchestrator:1.0.2"
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    androidTestImplementation("org.mockito:mockito-android:2.15.0")
    androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")

}

    if (propertiesFile.exists()) {
        println "Signing properties found"
        apply(plugin = "com.android.application")

plugins {
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
apply from = "../versioning.gradle"

ext {
    VERSION_NAME = "5.3.1"
    USE_ORCHESTRATOR = project.hasProperty("orchestrator") ? project.property("orchestrator") : false
}

android {
    compileSdkVersion(27)
    defaultConfig {
        applicationId = "com.duckduckgo.mobile.android"
        minSdkVersion(21)
        targetSdkVersion(27)
        versionCode = buildVersionCode()
        versionName = VERSION_NAME
        testInstrumentationRunner = "com.duckduckgo.app.TestRunner"
        archivesBaseName = "duckduckgo-$versionName" = versionName"

        javaCompileOptions {
            annotationProcessor(Options {)
                arguments = listOf("room.schemaLocation" = "$projectDir/schemas".toString())
            }
        }
        sourceSets {
            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
        }
    }
    signingConfigs {
        release
    }
    buildTypes {
        named("debug"){
        }
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }
    externalNativeBuild {

        cmake {
            path "CMakeLists.txt"
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.returnDefaultValues = true

        if (USE_ORCHESTRATOR) {
            execution "ANDROID_TEST_ORCHESTRATOR"
        }
    }

    val staticConfigPath = "${System.getenv("HOME")}/jenkins_static/com.duckduckgo.mobile.android"
    val propertiesPath = "${staticConfigPath}/ddg_android_build.properties"
    val propertiesFile = new File(propertiesPath)
    if (propertiesFile.exists()) {
        println "Signing properties found"
        val props = new Properties()
        props.load(new FileInputStream(propertiesFile))
        android.signingConfigs.release.storeFile = file("${staticConfigPath}/${propslistOf("key.store")}")
        android.signingConfigs.release.storePassword = propslistOf("key.store.password")
        android.signingConfigs.release.keyAlias = propslistOf("key.alias")
        android.signingConfigs.release.keyPassword = propslistOf("key.alias.password")
    } else {
        println "Signing properties not found at ${propertiesPath}, releases will NOT succeed"
        android.buildTypes.release.signingConfig = null
    }
}

ext {
    supportLibrary = "27.1.1"
    architectureComponents = "1.0.0"
    architectureComponentsExtensions = "1.1.1"
    androidKtx = "0.3"
    dagger = "2.14.1"
    retrofit = "2.3.0"
    ankoVersion = "0.10.4"
    glide = "4.6.1"
    androidTestRunner = "1.0.2"
}


dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.5.4")
    releaseImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"

    implementation(fileTree(dir = "libs", include: listOf("*.jar")))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:$supportLibrary")
    implementation("com.android.support:design:$supportLibrary")
    implementation("com.android.support.constraint:constraint-layout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofit")
    implementation("io.reactivex.rxjava2:rxjava:2.1.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.jakewharton.timber:timber:4.6.1")
    implementation("com.google.dagger:dagger-android:$dagger")
    implementation("com.google.dagger:dagger-android-support:$dagger")
    releaseImplementation "com.faendir:acra:4.10.0"

    // RxRelay
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    // Anko
    implementation("org.jetbrains.anko:anko-commons:$ankoVersion")
    implementation("org.jetbrains.anko:anko-design:$ankoVersion")

    // Android KTX
    implementation("androidx.core:core-ktx:$androidKtx")

    // ViewModel and LiveData
    implementation("android.arch.lifecycle:extensions:$architectureComponentsExtensions")
    kapt("android.arch.lifecycle:compiler:$architectureComponents")
    testImplementation("android.arch.core:core-testing:$architectureComponents")
    androidTestImplementation("android.arch.core:core-testing:$architectureComponents")

    // Room
    implementation("android.arch.persistence.room:runtime:$architectureComponents")
    kapt("android.arch.persistence.room:compiler:$architectureComponents")
    testImplementation("android.arch.persistence.room:testing:$architectureComponents")
    androidTestImplementation("android.arch.persistence.room:testing:$architectureComponents")

    // Dagger
    kapt("com.google.dagger:dagger-android-processor:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-android-processor:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-compiler:$dagger")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glide")
    kapt("com.github.bumptech.glide:compiler:$glide")

    testImplementation("org.mockito:mockito-core:2.15.0")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testImplementation("junit:junit:4.12")

    androidTestImplementation("com.android.support.test:runner:$androidTestRunner")
    androidTestImplementation("com.android.support.test:rules:$androidTestRunner")
    androidTestUtil "com.android.support.test:orchestrator:1.0.2"
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    androidTestImplementation("org.mockito:mockito-android:2.15.0")
    androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")

}

        props.load(new FileInputStream(propertiesFile))
        android.signingConfigs.release.storeFile = file("${staticConfigPath}/${propslistOf("key.store")}")
        android.signingConfigs.release.storePassword = propslistOf("key.store.password")
        android.signingConfigs.release.keyAlias = propslistOf("key.alias")
        android.signingConfigs.release.keyPassword = propslistOf("key.alias.password")
    } else {
        println "Signing properties not found at ${propertiesPath}, releases will NOT succeed"
        android.buildTypes.release.signingConfig = null
    }
}

ext {
    supportLibrary = "27.1.1"
    architectureComponents = "1.0.0"
    architectureComponentsExtensions = "1.1.1"
    androidKtx = "0.3"
    dagger = "2.14.1"
    retrofit = "2.3.0"
    ankoVersion = "0.10.4"
    glide = "4.6.1"
    androidTestRunner = "1.0.2"
}


dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.5.4")
    releaseImplementation "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"

    implementation(fileTree(dir = "libs", include: listOf("*.jar")))
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:$supportLibrary")
    implementation("com.android.support:design:$supportLibrary")
    implementation("com.android.support.constraint:constraint-layout:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofit")
    implementation("io.reactivex.rxjava2:rxjava:2.1.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.jakewharton.timber:timber:4.6.1")
    implementation("com.google.dagger:dagger-android:$dagger")
    implementation("com.google.dagger:dagger-android-support:$dagger")
    releaseImplementation "com.faendir:acra:4.10.0"

    // RxRelay
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    // Anko
    implementation("org.jetbrains.anko:anko-commons:$ankoVersion")
    implementation("org.jetbrains.anko:anko-design:$ankoVersion")

    // Android KTX
    implementation("androidx.core:core-ktx:$androidKtx")

    // ViewModel and LiveData
    implementation("android.arch.lifecycle:extensions:$architectureComponentsExtensions")
    kapt("android.arch.lifecycle:compiler:$architectureComponents")
    testImplementation("android.arch.core:core-testing:$architectureComponents")
    androidTestImplementation("android.arch.core:core-testing:$architectureComponents")

    // Room
    implementation("android.arch.persistence.room:runtime:$architectureComponents")
    kapt("android.arch.persistence.room:compiler:$architectureComponents")
    testImplementation("android.arch.persistence.room:testing:$architectureComponents")
    androidTestImplementation("android.arch.persistence.room:testing:$architectureComponents")

    // Dagger
    kapt("com.google.dagger:dagger-android-processor:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-android-processor:$dagger")
    kapt(AndroidTest "com.google.dagger:dagger-compiler:$dagger")

    // Glide
    implementation("com.github.bumptech.glide:glide:$glide")
    kapt("com.github.bumptech.glide:compiler:$glide")

    testImplementation("org.mockito:mockito-core:2.15.0")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testImplementation("junit:junit:4.12")

    androidTestImplementation("com.android.support.test:runner:$androidTestRunner")
    androidTestImplementation("com.android.support.test:rules:$androidTestRunner")
    androidTestUtil "com.android.support.test:orchestrator:1.0.2"
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    androidTestImplementation("org.mockito:mockito-android:2.15.0")
    androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")

}
