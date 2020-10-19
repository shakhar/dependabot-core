group "de.fhaachen"
version "1.0-SNAPSHOT"

buildscript {
    extra["kotlinVersion"] = "1.1.4-3"

    repositories {
        mavenCentral()
    }
}

val kotlinVersion: String by extra

plugins {
    id("kotlin")
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
    implementation("com.sparkjava:spark-core:2.5.4")
    implementation("org.slf4j:slf4j-simple:1.7.21")
    implementation("com.github.jeremyh:jBCrypt:master-SNAPSHOT")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation("mysql:mysql-connector-java:5.1.6")

}

jar {
    manifest {
        attributes "Main-Class" = "de.fhaachen.cryptoclicker.MainKt"
    }
}

shadowJar {
    mergeServiceFiles()
    configurations = listOf(project.configurations.compile)
}

compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
build.dependsOn shadowJar
