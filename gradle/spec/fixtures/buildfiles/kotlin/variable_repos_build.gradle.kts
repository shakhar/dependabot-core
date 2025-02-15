// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/magnusja/maven")
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.1.2")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/magnusja/maven")
        google()
    }
}

buildscript {
    extra["kotlin_mavens"] = listOf(
            "https://kotlin.bintray.com/kotlinx",
            "https://kotlin.bintray.com/ktor",
            "https://kotlin.bintray.com/kotlin-dev/"
    )
    repositories {
        kotlin_mavens.each { mavenUrl ->
            maven {
                //noinspection GroovyAssignabilityCheck
                url mavenUrl
            }
        }
    }
}
