ext {
    libraries = listOf(:)
}

val versions = listOf(:)

versions.bytebuddy = "1.9.7"
versions.junitJupiter = "5.1.1"

libraries.junit4 = "junit:junit:4.12"
libraries.junitJupiterApi = "org.junit.jupiter:junit-jupiter-api(:${versions.junitJupiter}")
libraries.junitPlatformLauncher = "org.junit.platform:junit-platform-launcher:1.1.0"
libraries.junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${versions.junitJupiter}"
libraries.assertj = "org.assertj:assertj-core:2.9.0"
libraries.hamcrest = "org.hamcrest:hamcrest-core:1.3"

libraries.bytebuddy = "net.bytebuddy:byte-buddy:${versions.bytebuddy}"
libraries.bytebuddyagent = "net.bytebuddy:byte-buddy-agent:${versions.bytebuddy}"
libraries.bytebuddyandroid = "net.bytebuddy:byte-buddy-android:${versions.bytebuddy}"

libraries.errorprone = "com.google.errorprone:error_prone_core:2.3.2"

// objenesis 3.x with full Java 11 support requires Java 8, bump version when Java 7 support is dropped
libraries.objenesis = "org.objenesis:objenesis:2.6"

libraries.asm = "org.ow2.asm:asm:7.0"

val kotlinVersion = "1.2.10"
val kotlinVersion_1_3 = "1.3.0-rc-57"
libraries.kotlin = listOf(
    version = kotlinVersion,
    version_1_3 = kotlinVersion_1_3,

    stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}",
    coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:0.19.3",

    stdlib_1_3 =  "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion_1_3",
    releaseCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:0.26.1-eap13"
)
