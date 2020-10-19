buildscript {
    extra["kotlinVersion"] = "1.2.61"
}

allprojects {
    repositories {
        maven("https://maven.fabric.io/public")
        maven("https://jitpack.io")
        google()
        jcenter()
    }
}

extra.apply {
    set("compileSdkVersion", 27)
    set("buildToolsVersion", "27.0.3")

    // Support
    set("supportVersion", "27.1.1")

    // set("commentedVersion", "27.1.1")

    set("findPropertyVersion", project.findProperty("findPropertyVersion") ?: "27.1.1")

    set("hasPropertyVersion", if(project.hasProperty("hasPropertyVersion")) project.getProperty("hasPropertyVersion") else "27.1.1")
}

extra.set("javaVersion", 11)
