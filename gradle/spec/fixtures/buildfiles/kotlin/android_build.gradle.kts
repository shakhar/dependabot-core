apply(plugin = "com.android.application")

android {
    compileSdkVersion(26)
    buildToolsVersion "27.0.3"
    useLibrary "org.apache.http.legacy"
    flavorDimensions "versionCode" = versionCode"
    defaultConfig {
        applicationId = "com.samourai.wallet"
        minSdkVersion(21)
        targetSdkVersion(26)
        versionCode = 100
        versionName = "0.98.50"
        // Enabling multidex support.
        multiDexEnabled = true
    }
    buildTypes {
        named("release"){
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
        }
    }
    lintOptions {
        check(ReleaseBuilds false)
        // Or, if you prefer, you can continue to check(for errors in release builds,)
        // but continue the build even when errors are found:
        isAbortOnError = false
    }
    packagingOptions {
        exclude "META-INF/DEPENDENCIES.txt"
        exclude "META-INF/LICENSE.txt"
        exclude "META-INF/NOTICE.txt"
        exclude "META-INF/NOTICE"
        exclude "META-INF/LICENSE"
        exclude "META-INF/DEPENDENCIES"
        exclude "META-INF/notice.txt"
        exclude "META-INF/license.txt"
        exclude "META-INF/dependencies.txt"
        exclude "META-INF/LGPL2.1"
    }
    productFlavors {
        production {
            minSdkVersion(21)
            applicationId = "com.samourai.wallet"
            targetSdkVersion(26)
            versionCode = 100
            versionName = "0.98.50"
            resValue "string", "app_name", "Samourai"
            resValue "string", "version_name", "0.98.50"
        }
        staging {
            minSdkVersion(21)
            applicationId = "com.samourai.wallet.staging"
            targetSdkVersion(26)
            versionCode = 100
            versionName = "0.98.50"
            resValue "string", "app_name", "Samourai Staging"
            resValue "string", "version_name", "0.98.50"
        }
    }
}

dependencies {
    api(fileTree(include: listOf("*.jar"), dir = "libs"))
    implementation(("com.google.zxing:core:3.3.0") {)
        transitive = true
    }
    implementation("com.android.support:multidex:1.0.0")
    implementation("net.i2p.android.ext:floatingactionbutton:1.9.0")
    implementation("org.thoughtcrime.ssl.pinning:AndroidPinning:1.0.0")
    implementation("com.neovisionaries:nv-websocket-client:1.9")
    implementation("com.baoyz.swipemenulistview:library:1.2.1")
    implementation("org.json:json:20140107")
    implementation("commons-io:commons-io:2.5")
    implementation("commons-codec:commons-codec:1.4")
    implementation("org.apache.commons:commons-lang3:3.3")
    implementation("com.google.guava:guava:20.0")
    implementation("com.google.code.findbugs:jsr305:1.3.9")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("org.bouncycastle:bcprov-jdk15on:1.55")
    implementation("com.madgag.spongycastle:prov:1.54.0.0")
    implementation("com.lambdaworks:scrypt:1.4.0")
    implementation("info.guardianproject.netcipher:netcipher:2.0.0-alpha1")
    implementation("info.guardianproject.netcipher:netcipher-okhttp3:2.0.0-alpha1")
    implementation("com.github.mjdev:libaums:0.5.5")
    implementation("de.mindpipe.android:android-logging-log4j:1.0.3")
    implementation("com.github.magnusja:java-fs:0.1.3")
    implementation("com.android.support:support-v4:21.0.3")
    implementation("com.android.support:appcompat-v7:23.1.1")
    implementation("com.yanzhenjie.zbar:camera:1.0.0")
    implementation("com.badly.specified:dep:.*")
}
