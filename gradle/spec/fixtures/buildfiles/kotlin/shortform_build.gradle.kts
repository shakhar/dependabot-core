import org.jetbrains.kotlin.config.KotlinCompilerVersion

group "de.fhaachen"
version "1.0-SNAPSHOT"

buildscript {
    extra["kotlinVersion"] = "1.1.4-3"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.jengelman.gradle.plugins:shadow:2.0.2")
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

// Here to ensure we don"t parse SCM URLs as dependency declarations
scm {
    url "scm:git@github.com:mapfish/mapfish-print.git"
    connection "scm:git@github.com:mapfish/mapfish-print.git"
    developerConnection "scm:git@github.com:mapfish/mapfish-print.git"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
    implementation("com.sparkjava:spark-core:2.5.4@jar")
    implementation("org.slf4j:slf4j-simple:1.7.21")
    implementation("com.github.jeremyh:jBCrypt:master-SNAPSHOT")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation("mysql:mysql-connector-java:5.1.6")
    implementation("com.github.heremaps:oksse:be5d2cd6deb8cf3ca2c9a740bdacec816871d4f7")

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
