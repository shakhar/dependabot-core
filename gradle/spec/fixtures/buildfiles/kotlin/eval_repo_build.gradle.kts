// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/#(if(true) 'should' else 'eval')/maven")
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.1.2")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
