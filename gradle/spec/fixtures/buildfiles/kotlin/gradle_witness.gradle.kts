val taskRequests = getGradle().getStartParameter().getTaskRequests().toString()
val isFoss = taskRequests.contains("Foss") || taskRequests.contains("foss")

buildscript {
    repositories {
        maven(
"https://maven.fabric.io/public"
)
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:3.1.0")
        classpath("io.fabric.tools:gradle:1.24.4")
        classpath(files("libs/gradle-witness.jar"))
    }
}

plugins {
    id("com.android.application")
    id("jacoco-android")
}
if(!isFoss) {
    apply(plugin = "io.fabric")
}
apply(plugin = "witness")

android {
    buildToolsVersion "27.0.3"
    compileSdkVersion(27)
    useLibrary "org.apache.http.legacy"

    defaultConfig {
        applicationId = "org.openhab.habdroid"
        minSdkVersion(14)
        targetSdkVersion(27)
        versionCode = 80
        versionName = "2.2.22-beta"
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        val IS_DEVELOPER = "IS_DEVELOPER"
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            buildConfigField "boolean", IS_DEVELOPER, "false"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
        }
        named("debug"){
            isMinifyEnabled = false
            buildConfigField "boolean", IS_DEVELOPER, "true"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
            pseudoLocalesEnabled true
            testCoverageEnabled true
        }
    }

    flavorDimensions "license", "release"
    productFlavors {
        full {
            dimension "license"
            manifestPlaceholders = listOf( maps_api(_key : project.findProperty("mapsApiKey") ? = "" ))
        }
        foss {
            dimension "license"
        }

        stable {
            dimension "release"
        }
        beta {
            dimension "release"
            applicationIdSuffix = ".beta"
        }
    }
    testOptions {
        unitTests.returnDefaultValues = true
    }
    lintOptions {
        lintConfig file("lint.xml")
        isAbortOnError = false
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

repositories {
    maven(
"https://maven.fabric.io/public"
)
    mavenCentral()
    maven(
"https://jitpack.io"
)
    google()
    jcenter()
}

dependencies {
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:support-v4:27.1.1")
    implementation("com.android.support:recyclerview-v7:27.1.1")
    implementation("com.android.support:design:27.1.1")
    implementation("com.android.support:multidex:1.0.3")
    implementation("org.jmdns:jmdns:3.5.4")
    implementation("com.squareup.okhttp3:okhttp:3.11.0")
    implementation("com.larswerkman:HoloColorPicker:1.5")
    implementation("com.github.BigBadaboom:androidsvg:3511e136498da94018ef9fa438895984ea9b99db")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    // Google Maps
    fullImplementation "com.google.android.gms:play-services-maps:12.0.1"
    // GCM
    fullImplementation "com.google.android.gms:play-services-gcm:12.0.1"

    // About screen
    implementation("com.github.daniel-stoneuk:material-about-library:2.2.5")
    // Used libraries
    implementation(("com.mikepenz:aboutlibraries:6.0.8@aar") {)
        transitive = true
    }

    // Crash reporting
    fullImplementation("com.crashlytics.sdk.android:crashlytics:2.7.1@aar") {
        transitive = true
    }

    fullImplementation "com.google.firebase:firebase-core:12.0.1"

    implementation("com.google.auto.value:auto-value-annotations:1.6.2")
    annotationProcessor("com.google.auto.value:auto-value:1.6.2")
    annotationProcessor("com.ryanharter.auto.value:auto-value-parcel:0.2.6")

    testImplementation("org.mockito:mockito-core:2.22.0")
    testImplementation("junit:junit:4.12")
    testImplementation("org.json:json:20180813")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.11.0")

    // PowerMock
    testImplementation("org.powermock:powermock-core:1.7.4")
    testImplementation("org.powermock:powermock-api-mockito2:1.7.4")
    testImplementation("org.powermock:powermock-module-junit4:1.7.4")

    // Espresso UI tests
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2", {)
        exclude group = "com.android.support", module = "support-annotations"
    }
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-intents:3.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2", {)
        exclude group = "com.android.support", module = "support-annotations"
    }

    androidTestImplementation("tools.fastlane:screengrab:1.1.0")
}

dependencyVerification {
    verify = listOf(
            "com.google.firebase:firebase-core:297f3c1f667e3a288ffcc2d017282679eaec4a54b98b3f538aeb7fbd7dac85a7",
            "com.google.firebase:firebase-analytics:83d08c5c8e58f2aaac8783ae42bc76bedb83435b696e654ecae1b753accced99",
            "com.google.firebase:firebase-analytics-impl:5a9f7c6d01df2fd1e270f812d13e31a72fc4006f548c65beb8370978b17a3732",
            "com.google.firebase:firebase-iid:42f75667d0d9df7c67d70cc0e0a58d2d2d7dbec331ad3c8c4a16945899c5e7dc",
            "com.google.firebase:firebase-common:b36bf28d62fe95069b4eb19eb8ac75c66628bc5045e4b93a783212696b70c419",
            "com.google.android.gms:play-services-tasks:b4c454f75281202789ea244e8c9b4ed15f0c6a0aeed3531ff7bffd9f1d7c4a61",
            "com.google.android.gms:play-services-basement:bfcd288d82e871b626f6325955e80956062f3bcac8d2162216643402d6930c88",
            "com.google.firebase:firebase-analytics-license:4465a2d2b92bddcbc4aa44f5218bd6f700b512b82a4e46821fa768068026b3bb",
            "com.android.support:support-v4:36d8385de1be7791231acb933b757198f97cb53bc7d046e8c4bc403d214caaca",
            "com.google.android.gms:play-services-basement-license:9b16c6e2dc0f9198a036f00f0c66534d5fefc26e94384381284aa867473fa173",
            "com.google.firebase:firebase-common-license:9ebd022ec18e239055de52df77882623cda767734e010d20a70ac5c2ab8b598a",
            "com.google.firebase:firebase-analytics-impl-license:548b70375c927a6b19bc77300c6ed909b94a84417583797dd7b2827e0335a168",
            "com.android.support:support-media-compat:9d8cee7cd40eff22ebdeb90c8e70f5ee96c5bd25cb2c3e3b3940e27285a3e98a",
            "com.android.support:support-fragment:a0ab3369ef40fe199160692f0463a5f63f1277ebfb64dd587c76fdb128d76b32",
            "com.android.support:support-core-utils:4fda6d4eb430971e3b1dad7456988333f374b0f4ba15f99839ca1a0ab5155c8a",
            "com.android.support:support-core-ui:82f538051599335ea881ec264407547cab52be750f16ce099cfb27754fc755ff",
            "com.android.support:support-compat:7d6da01cf9766b1705c6c80cfc12274a895b406c4c287900b07a56145ca6c030",
            "com.google.android.gms:play-services-tasks-license:2dc5a838b1ed8a373d5242dd97173ca6c355f7a6d59fae22c180fed85771b7af",
            "com.google.firebase:firebase-iid-license:cd0df76dabb5bf1a2409373900ebf13f1fad8888b33cf193ddf59a765a800d27",
            "com.android.support:support-annotations:99d6199ad5a09a0e5e8a49a4cc08f818483ddcfd7eedea2f9923412daf982309",
            "android.arch.lifecycle:runtime:e4e34e5d02bd102e8d39ddbc29f9ead8a15a61e367993d02238196ac48509ad8",
            "android.arch.lifecycle:common:86bf301a20ad0cd0a391e22a52e6fbf90575c096ff83233fa9fd0d52b3219121",
            "android.arch.core:common:5192934cd73df32e2c15722ed7fc488dde90baaec9ae030010dd1a80fb4e74e1",
    )
}

if(!isFoss) {
    apply(plugin = "com.google.gms.google-services")
}

val taskRequests = getGradle().getStartParameter().getTaskRequests().toString()
val isFoss = taskRequests.contains("Foss") || taskRequests.contains("foss")

buildscript {
    repositories {
        maven(
"https://maven.fabric.io/public"
)
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:3.1.0")
        classpath("io.fabric.tools:gradle:1.24.4")
        classpath(files("libs/gradle-witness.jar"))
    }
}

plugins {
    id("com.android.application")
    id("jacoco-android")
}
if(!isFoss) {
    apply(plugin = "io.fabric")
}
apply(plugin = "witness")

android {
    buildToolsVersion "27.0.3"
    compileSdkVersion(27)
    useLibrary "org.apache.http.legacy"

    defaultConfig {
        applicationId = "org.openhab.habdroid"
        minSdkVersion(14)
        targetSdkVersion(27)
        versionCode = 80
        versionName = "2.2.22-beta"
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        val IS_DEVELOPER = "IS_DEVELOPER"
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            buildConfigField "boolean", IS_DEVELOPER, "false"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
        }
        named("debug"){
            isMinifyEnabled = false
            buildConfigField "boolean", IS_DEVELOPER, "true"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
            pseudoLocalesEnabled true
            testCoverageEnabled true
        }
    }

    flavorDimensions "license", "release"
    productFlavors {
        full {
            dimension "license"
            manifestPlaceholders = listOf( maps_api(_key : project.findProperty("mapsApiKey") ? = "" ))
        }
        foss {
            dimension "license"
        }

        stable {
            dimension "release"
        }
        beta {
            dimension "release"
            applicationIdSuffix = ".beta"
        }
    }
    testOptions {
        unitTests.returnDefaultValues = true
    }
    lintOptions {
        lintConfig file("lint.xml")
        isAbortOnError = false
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

repositories {
    maven(
"https://maven.fabric.io/public"
)
    mavenCentral()
    maven(
"https://jitpack.io"
)
    google()
    jcenter()
}

dependencies {
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:support-v4:27.1.1")
    implementation("com.android.support:recyclerview-v7:27.1.1")
    implementation("com.android.support:design:27.1.1")
    implementation("com.android.support:multidex:1.0.3")
    implementation("org.jmdns:jmdns:3.5.4")
    implementation("com.squareup.okhttp3:okhttp:3.11.0")
    implementation("com.larswerkman:HoloColorPicker:1.5")
    implementation("com.github.BigBadaboom:androidsvg:3511e136498da94018ef9fa438895984ea9b99db")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    // Google Maps
    fullImplementation "com.google.android.gms:play-services-maps:12.0.1"
    // GCM
    fullImplementation "com.google.android.gms:play-services-gcm:12.0.1"

    // About screen
    implementation("com.github.daniel-stoneuk:material-about-library:2.2.5")
    // Used libraries
    implementation(("com.mikepenz:aboutlibraries:6.0.8@aar") {)
        transitive = true
    }

    // Crash reporting
    fullImplementation("com.crashlytics.sdk.android:crashlytics:2.7.1@aar") {
        transitive = true
    }

    fullImplementation "com.google.firebase:firebase-core:12.0.1"

    implementation("com.google.auto.value:auto-value-annotations:1.6.2")
    annotationProcessor("com.google.auto.value:auto-value:1.6.2")
    annotationProcessor("com.ryanharter.auto.value:auto-value-parcel:0.2.6")

    testImplementation("org.mockito:mockito-core:2.22.0")
    testImplementation("junit:junit:4.12")
    testImplementation("org.json:json:20180813")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.11.0")

    // PowerMock
    testImplementation("org.powermock:powermock-core:1.7.4")
    testImplementation("org.powermock:powermock-api-mockito2:1.7.4")
    testImplementation("org.powermock:powermock-module-junit4:1.7.4")

    // Espresso UI tests
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2", {)
        exclude group = "com.android.support", module = "support-annotations"
    }
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-intents:3.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2", {)
        exclude group = "com.android.support", module = "support-annotations"
    }

    androidTestImplementation("tools.fastlane:screengrab:1.1.0")
}

dependencyVerification {
    verify = listOf(
            "com.google.firebase:firebase-core:297f3c1f667e3a288ffcc2d017282679eaec4a54b98b3f538aeb7fbd7dac85a7",
            "com.google.firebase:firebase-analytics:83d08c5c8e58f2aaac8783ae42bc76bedb83435b696e654ecae1b753accced99",
            "com.google.firebase:firebase-analytics-impl:5a9f7c6d01df2fd1e270f812d13e31a72fc4006f548c65beb8370978b17a3732",
            "com.google.firebase:firebase-iid:42f75667d0d9df7c67d70cc0e0a58d2d2d7dbec331ad3c8c4a16945899c5e7dc",
            "com.google.firebase:firebase-common:b36bf28d62fe95069b4eb19eb8ac75c66628bc5045e4b93a783212696b70c419",
            "com.google.android.gms:play-services-tasks:b4c454f75281202789ea244e8c9b4ed15f0c6a0aeed3531ff7bffd9f1d7c4a61",
            "com.google.android.gms:play-services-basement:bfcd288d82e871b626f6325955e80956062f3bcac8d2162216643402d6930c88",
            "com.google.firebase:firebase-analytics-license:4465a2d2b92bddcbc4aa44f5218bd6f700b512b82a4e46821fa768068026b3bb",
            "com.android.support:support-v4:36d8385de1be7791231acb933b757198f97cb53bc7d046e8c4bc403d214caaca",
            "com.google.android.gms:play-services-basement-license:9b16c6e2dc0f9198a036f00f0c66534d5fefc26e94384381284aa867473fa173",
            "com.google.firebase:firebase-common-license:9ebd022ec18e239055de52df77882623cda767734e010d20a70ac5c2ab8b598a",
            "com.google.firebase:firebase-analytics-impl-license:548b70375c927a6b19bc77300c6ed909b94a84417583797dd7b2827e0335a168",
            "com.android.support:support-media-compat:9d8cee7cd40eff22ebdeb90c8e70f5ee96c5bd25cb2c3e3b3940e27285a3e98a",
            "com.android.support:support-fragment:a0ab3369ef40fe199160692f0463a5f63f1277ebfb64dd587c76fdb128d76b32",
            "com.android.support:support-core-utils:4fda6d4eb430971e3b1dad7456988333f374b0f4ba15f99839ca1a0ab5155c8a",
            "com.android.support:support-core-ui:82f538051599335ea881ec264407547cab52be750f16ce099cfb27754fc755ff",
            "com.android.support:support-compat:7d6da01cf9766b1705c6c80cfc12274a895b406c4c287900b07a56145ca6c030",
            "com.google.android.gms:play-services-tasks-license:2dc5a838b1ed8a373d5242dd97173ca6c355f7a6d59fae22c180fed85771b7af",
            "com.google.firebase:firebase-iid-license:cd0df76dabb5bf1a2409373900ebf13f1fad8888b33cf193ddf59a765a800d27",
            "com.android.support:support-annotations:99d6199ad5a09a0e5e8a49a4cc08f818483ddcfd7eedea2f9923412daf982309",
            "android.arch.lifecycle:runtime:e4e34e5d02bd102e8d39ddbc29f9ead8a15a61e367993d02238196ac48509ad8",
            "android.arch.lifecycle:common:86bf301a20ad0cd0a391e22a52e6fbf90575c096ff83233fa9fd0d52b3219121",
            "android.arch.core:common:5192934cd73df32e2c15722ed7fc488dde90baaec9ae030010dd1a80fb4e74e1",
    )
}

if(!isFoss) {
    apply(plugin = "com.google.gms.google-services")
}


buildscript {
    repositories {
        maven(
"https://maven.fabric.io/public"
)
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:3.1.0")
        classpath("io.fabric.tools:gradle:1.24.4")
        classpath(files("libs/gradle-witness.jar"))
    }
}

plugins {
    id("com.android.application")
    id("jacoco-android")
}
if(!isFoss) {
    apply(plugin = "io.fabric")
}
apply(plugin = "witness")

android {
    buildToolsVersion "27.0.3"
    compileSdkVersion(27)
    useLibrary "org.apache.http.legacy"

    defaultConfig {
        applicationId = "org.openhab.habdroid"
        minSdkVersion(14)
        targetSdkVersion(27)
        versionCode = 80
        versionName = "2.2.22-beta"
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        val taskRequests = getGradle().getStartParameter().getTaskRequests().toString()
val isFoss = taskRequests.contains("Foss") || taskRequests.contains("foss")

named("buildscript"){
    named("repositories"){
        maven(
"https://maven.fabric.io/public"
)
        mavenCentral()
    }
    named("dependencies"){
        classpath("com.google.gms:google-services:3.1.0")
        classpath("io.fabric.tools:gradle:1.24.4")
        classpath(files("libs/gradle-witness.jar"))
    }
}

named("plugins"){
    id("com.android.application")
    id("jacoco-android")
}
named("if(!isFoss)"){
    apply(plugin = "io.fabric")
}
apply(plugin = "witness")

named("android"){
    buildToolsVersion "27.0.3"
    compileSdkVersion(27)
    useLibrary "org.apache.http.legacy"

    named("defaultConfig"){
        applicationId = "org.openhab.habdroid"
        minSdkVersion(14)
        targetSdkVersion(27)
        versionCode = 80
        versionName = "2.2.22-beta"
        multiDexEnabled = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    named("buildTypes"){
        val IS_DEVELOPER = "IS_DEVELOPER"
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            buildConfigField "boolean", IS_DEVELOPER, "false"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
        }
        named("debug"){
            isMinifyEnabled = false
            buildConfigField "boolean", IS_DEVELOPER, "true"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
            pseudoLocalesEnabled true
            testCoverageEnabled true
        }
    }

    flavorDimensions "license", "release"
    named("productFlavors"){
        named("full"){
            dimension "license"
            manifestPlaceholders = listOf( maps_api(_key : project.findProperty("mapsApiKey") ? = "" ))
        }
        named("foss"){
            dimension "license"
        }

        named("stable"){
            dimension "release"
        }
        named("beta"){
            dimension "release"
            applicationIdSuffix = ".beta"
        }
    }
    named("testOptions"){
        unitTests.returnDefaultValues = true
    }
    named("lintOptions"){
        lintConfig file("lint.xml")
        isAbortOnError = false
    }
    named("compileOptions"){
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

named("repositories"){
    maven(
"https://maven.fabric.io/public"
)
    mavenCentral()
    maven(
"https://jitpack.io"
)
    google()
    jcenter()
}

named("dependencies"){
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:support-v4:27.1.1")
    implementation("com.android.support:recyclerview-v7:27.1.1")
    implementation("com.android.support:design:27.1.1")
    implementation("com.android.support:multidex:1.0.3")
    implementation("org.jmdns:jmdns:3.5.4")
    implementation("com.squareup.okhttp3:okhttp:3.11.0")
    implementation("com.larswerkman:HoloColorPicker:1.5")
    implementation("com.github.BigBadaboom:androidsvg:3511e136498da94018ef9fa438895984ea9b99db")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    // Google Maps
    fullImplementation "com.google.android.gms:play-services-maps:12.0.1"
    // GCM
    fullImplementation "com.google.android.gms:play-services-gcm:12.0.1"

    // About screen
    implementation("com.github.daniel-stoneuk:material-about-library:2.2.5")
    // Used libraries
    named("implementation(("com.mikepenz:aboutlibraries:6.0.8@aar")"){)
        transitive = true
    }

    // Crash reporting
    named("fullImplementation("com.crashlytics.sdk.android:crashlytics:2.7.1@aar")"){
        transitive = true
    }

    fullImplementation "com.google.firebase:firebase-core:12.0.1"

    implementation("com.google.auto.value:auto-value-annotations:1.6.2")
    annotationProcessor("com.google.auto.value:auto-value:1.6.2")
    annotationProcessor("com.ryanharter.auto.value:auto-value-parcel:0.2.6")

    testImplementation("org.mockito:mockito-core:2.22.0")
    testImplementation("junit:junit:4.12")
    testImplementation("org.json:json:20180813")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.11.0")

    // PowerMock
    testImplementation("org.powermock:powermock-core:1.7.4")
    testImplementation("org.powermock:powermock-api-mockito2:1.7.4")
    testImplementation("org.powermock:powermock-module-junit4:1.7.4")

    // Espresso UI tests
    named("androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2","){)
        exclude group = "com.android.support", module = "support-annotations"
    }
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-intents:3.0.2")
    named("androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2","){)
        exclude group = "com.android.support", module = "support-annotations"
    }

    androidTestImplementation("tools.fastlane:screengrab:1.1.0")
}

named("dependencyVerification"){
    verify = listOf(
            "com.google.firebase:firebase-core:297f3c1f667e3a288ffcc2d017282679eaec4a54b98b3f538aeb7fbd7dac85a7",
            "com.google.firebase:firebase-analytics:83d08c5c8e58f2aaac8783ae42bc76bedb83435b696e654ecae1b753accced99",
            "com.google.firebase:firebase-analytics-impl:5a9f7c6d01df2fd1e270f812d13e31a72fc4006f548c65beb8370978b17a3732",
            "com.google.firebase:firebase-iid:42f75667d0d9df7c67d70cc0e0a58d2d2d7dbec331ad3c8c4a16945899c5e7dc",
            "com.google.firebase:firebase-common:b36bf28d62fe95069b4eb19eb8ac75c66628bc5045e4b93a783212696b70c419",
            "com.google.android.gms:play-services-tasks:b4c454f75281202789ea244e8c9b4ed15f0c6a0aeed3531ff7bffd9f1d7c4a61",
            "com.google.android.gms:play-services-basement:bfcd288d82e871b626f6325955e80956062f3bcac8d2162216643402d6930c88",
            "com.google.firebase:firebase-analytics-license:4465a2d2b92bddcbc4aa44f5218bd6f700b512b82a4e46821fa768068026b3bb",
            "com.android.support:support-v4:36d8385de1be7791231acb933b757198f97cb53bc7d046e8c4bc403d214caaca",
            "com.google.android.gms:play-services-basement-license:9b16c6e2dc0f9198a036f00f0c66534d5fefc26e94384381284aa867473fa173",
            "com.google.firebase:firebase-common-license:9ebd022ec18e239055de52df77882623cda767734e010d20a70ac5c2ab8b598a",
            "com.google.firebase:firebase-analytics-impl-license:548b70375c927a6b19bc77300c6ed909b94a84417583797dd7b2827e0335a168",
            "com.android.support:support-media-compat:9d8cee7cd40eff22ebdeb90c8e70f5ee96c5bd25cb2c3e3b3940e27285a3e98a",
            "com.android.support:support-fragment:a0ab3369ef40fe199160692f0463a5f63f1277ebfb64dd587c76fdb128d76b32",
            "com.android.support:support-core-utils:4fda6d4eb430971e3b1dad7456988333f374b0f4ba15f99839ca1a0ab5155c8a",
            "com.android.support:support-core-ui:82f538051599335ea881ec264407547cab52be750f16ce099cfb27754fc755ff",
            "com.android.support:support-compat:7d6da01cf9766b1705c6c80cfc12274a895b406c4c287900b07a56145ca6c030",
            "com.google.android.gms:play-services-tasks-license:2dc5a838b1ed8a373d5242dd97173ca6c355f7a6d59fae22c180fed85771b7af",
            "com.google.firebase:firebase-iid-license:cd0df76dabb5bf1a2409373900ebf13f1fad8888b33cf193ddf59a765a800d27",
            "com.android.support:support-annotations:99d6199ad5a09a0e5e8a49a4cc08f818483ddcfd7eedea2f9923412daf982309",
            "android.arch.lifecycle:runtime:e4e34e5d02bd102e8d39ddbc29f9ead8a15a61e367993d02238196ac48509ad8",
            "android.arch.lifecycle:common:86bf301a20ad0cd0a391e22a52e6fbf90575c096ff83233fa9fd0d52b3219121",
            "android.arch.core:common:5192934cd73df32e2c15722ed7fc488dde90baaec9ae030010dd1a80fb4e74e1",
    )
}

named("if(!isFoss)"){
    apply(plugin = "com.google.gms.google-services")
}

        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
            buildConfigField "boolean", IS_DEVELOPER, "false"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
        }
        named("debug"){
            isMinifyEnabled = false
            buildConfigField "boolean", IS_DEVELOPER, "true"
            buildConfigField "java.util.Date", "buildTime", "new java.util.Date(" + System.currentTimeMillis() + "L)"
            pseudoLocalesEnabled true
            testCoverageEnabled true
        }
    e
        }
    }

    flavorDimensions "license", "release"
    productFlavors {
        full {
            dimension "license"
            manifestPlaceholders = listOf( maps_api(_key : project.findProperty("mapsApiKey") ? = "" ))
        }
        foss {
            dimension "license"
        }

        stable {
            dimension "release"
        }
        beta {
            dimension "release"
            applicationIdSuffix = ".beta"
        }
    }
    testOptions {
        unitTests.returnDefaultValues = true
    }
    lintOptions {
        lintConfig file("lint.xml")
        isAbortOnError = false
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

repositories {
    maven(
"https://maven.fabric.io/public"
)
    mavenCentral()
    maven(
"https://jitpack.io"
)
    google()
    jcenter()
}

dependencies {
    implementation("com.android.support:appcompat-v7:27.1.1")
    implementation("com.android.support:support-v4:27.1.1")
    implementation("com.android.support:recyclerview-v7:27.1.1")
    implementation("com.android.support:design:27.1.1")
    implementation("com.android.support:multidex:1.0.3")
    implementation("org.jmdns:jmdns:3.5.4")
    implementation("com.squareup.okhttp3:okhttp:3.11.0")
    implementation("com.larswerkman:HoloColorPicker:1.5")
    implementation("com.github.BigBadaboom:androidsvg:3511e136498da94018ef9fa438895984ea9b99db")
    implementation("com.github.apl-devs:appintro:v4.2.2")
    // Google Maps
    fullImplementation "com.google.android.gms:play-services-maps:12.0.1"
    // GCM
    fullImplementation "com.google.android.gms:play-services-gcm:12.0.1"

    // About screen
    implementation("com.github.daniel-stoneuk:material-about-library:2.2.5")
    // Used libraries
    implementation(("com.mikepenz:aboutlibraries:6.0.8@aar") {)
        transitive = true
    }

    // Crash reporting
    fullImplementation("com.crashlytics.sdk.android:crashlytics:2.7.1@aar") {
        transitive = true
    }

    fullImplementation "com.google.firebase:firebase-core:12.0.1"

    implementation("com.google.auto.value:auto-value-annotations:1.6.2")
    annotationProcessor("com.google.auto.value:auto-value:1.6.2")
    annotationProcessor("com.ryanharter.auto.value:auto-value-parcel:0.2.6")

    testImplementation("org.mockito:mockito-core:2.22.0")
    testImplementation("junit:junit:4.12")
    testImplementation("org.json:json:20180813")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.11.0")

    // PowerMock
    testImplementation("org.powermock:powermock-core:1.7.4")
    testImplementation("org.powermock:powermock-api-mockito2:1.7.4")
    testImplementation("org.powermock:powermock-module-junit4:1.7.4")

    // Espresso UI tests
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2", {)
        exclude group = "com.android.support", module = "support-annotations"
    }
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-intents:3.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2", {)
        exclude group = "com.android.support", module = "support-annotations"
    }

    androidTestImplementation("tools.fastlane:screengrab:1.1.0")
}

dependencyVerification {
    verify = listOf(
            "com.google.firebase:firebase-core:297f3c1f667e3a288ffcc2d017282679eaec4a54b98b3f538aeb7fbd7dac85a7",
            "com.google.firebase:firebase-analytics:83d08c5c8e58f2aaac8783ae42bc76bedb83435b696e654ecae1b753accced99",
            "com.google.firebase:firebase-analytics-impl:5a9f7c6d01df2fd1e270f812d13e31a72fc4006f548c65beb8370978b17a3732",
            "com.google.firebase:firebase-iid:42f75667d0d9df7c67d70cc0e0a58d2d2d7dbec331ad3c8c4a16945899c5e7dc",
            "com.google.firebase:firebase-common:b36bf28d62fe95069b4eb19eb8ac75c66628bc5045e4b93a783212696b70c419",
            "com.google.android.gms:play-services-tasks:b4c454f75281202789ea244e8c9b4ed15f0c6a0aeed3531ff7bffd9f1d7c4a61",
            "com.google.android.gms:play-services-basement:bfcd288d82e871b626f6325955e80956062f3bcac8d2162216643402d6930c88",
            "com.google.firebase:firebase-analytics-license:4465a2d2b92bddcbc4aa44f5218bd6f700b512b82a4e46821fa768068026b3bb",
            "com.android.support:support-v4:36d8385de1be7791231acb933b757198f97cb53bc7d046e8c4bc403d214caaca",
            "com.google.android.gms:play-services-basement-license:9b16c6e2dc0f9198a036f00f0c66534d5fefc26e94384381284aa867473fa173",
            "com.google.firebase:firebase-common-license:9ebd022ec18e239055de52df77882623cda767734e010d20a70ac5c2ab8b598a",
            "com.google.firebase:firebase-analytics-impl-license:548b70375c927a6b19bc77300c6ed909b94a84417583797dd7b2827e0335a168",
            "com.android.support:support-media-compat:9d8cee7cd40eff22ebdeb90c8e70f5ee96c5bd25cb2c3e3b3940e27285a3e98a",
            "com.android.support:support-fragment:a0ab3369ef40fe199160692f0463a5f63f1277ebfb64dd587c76fdb128d76b32",
            "com.android.support:support-core-utils:4fda6d4eb430971e3b1dad7456988333f374b0f4ba15f99839ca1a0ab5155c8a",
            "com.android.support:support-core-ui:82f538051599335ea881ec264407547cab52be750f16ce099cfb27754fc755ff",
            "com.android.support:support-compat:7d6da01cf9766b1705c6c80cfc12274a895b406c4c287900b07a56145ca6c030",
            "com.google.android.gms:play-services-tasks-license:2dc5a838b1ed8a373d5242dd97173ca6c355f7a6d59fae22c180fed85771b7af",
            "com.google.firebase:firebase-iid-license:cd0df76dabb5bf1a2409373900ebf13f1fad8888b33cf193ddf59a765a800d27",
            "com.android.support:support-annotations:99d6199ad5a09a0e5e8a49a4cc08f818483ddcfd7eedea2f9923412daf982309",
            "android.arch.lifecycle:runtime:e4e34e5d02bd102e8d39ddbc29f9ead8a15a61e367993d02238196ac48509ad8",
            "android.arch.lifecycle:common:86bf301a20ad0cd0a391e22a52e6fbf90575c096ff83233fa9fd0d52b3219121",
            "android.arch.core:common:5192934cd73df32e2c15722ed7fc488dde90baaec9ae030010dd1a80fb4e74e1",
    )
}

if(!isFoss) {
    apply(plugin = "com.google.gms.google-services")
}
