// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
    }
}

subprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/magnusja/maven")
        google()
    }
}
