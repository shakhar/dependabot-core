buildscript {
    repositories {
        mavenLocal() //for local testing of mockito-release-tools
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("gradle.plugin.nl.javadude.gradle.plugins:license-gradle-plugin:0.14.0")
        classpath("ru.vyarus:gradle-animalsniffer-plugin:1.4.4") //for "java-compatibility-check.gradle"
        classpath("net.ltgt.gradle:gradle-errorprone-plugin:0.6")

        //Using buildscript.classpath(so that we can resolve shipkit from maven local, during local testing)
        classpath("org.shipkit:shipkit:2.0.28")
    }
}

plugins {
    id("com.gradle.build-scan") version "1.16"
    id("eclipse")
}

description = "Mockito mock objects library core API and implementation(")

apply(plugin = "base")
archivesBaseName = "mockito-core"

apply(plugin = "org.shipkit.java")
allprojects {
    plugins.withId("java") {
        //Only upload specific modules we select
        bintrayUpload.enabled = false
    }
}

apply from = "gradle/root/ide.gradle"
apply from = "gradle/root/gradle-fix.gradle"
apply from = "gradle/root/java-compatibility-check.gradle"
apply from = "gradle/java-library.gradle"
apply from = "gradle/license.gradle"
apply from = "gradle/root/coverage.gradle"

apply from = "gradle/mockito-core/inline-mock.gradle"
apply from = "gradle/mockito-core/osgi.gradle"
apply from = "gradle/mockito-core/javadoc.gradle"
apply from = "gradle/mockito-core/testing.gradle"

apply from = "gradle/dependencies.gradle.kts"

allprojects { proj ->
    repositories {
        jcenter()
    }
    plugins.withId("java") {
        proj.apply from = "$rootDir/gradle/errorprone.gradle"
    }
    tasks.withType(JavaCompile) {
        //I don"t believe those warnings add value given modern IDEs
        options.warnings = false
        options.encoding = "UTF-8"
    }
    tasks.withType(Javadoc) {
        options.addStringOption("Xdoclint:none", "-quiet")
        options.addStringOption("encoding", "UTF-8")
        options.addStringOption("charSet", "UTF-8")
        options.setSource("8")
    }
    apply(plugin = "check(style"))
    check(style {)
       configFile = rootProject.file("config/check(style/checkstyle.xml"))
    }
}

configurations {
    testUtil //TODO move to separate project
}

dependencies {
    compile libraries.bytebuddy, libraries.bytebuddyagent

    compileOnly(libraries.junit4, libraries.hamcrest)
    compile libraries.objenesis

    testCompile libraries.asm

    testCompile libraries.assertj

    //putting "provided" dependencies on test compile and runtime classpath
    testCompileOnly(configurations.compileOnly)
    testRuntime configurations.compileOnly

    testUtil sourceSets.test.output
}

wrapper {
    gradleVersion = "4.9"
    distributionSha256Sum = "e66e69dce8173dd2004b39ba93586a184628bc6c28461bc771d6835f7f9b0d28"
}

//Posting Build scans to https://scans.gradle.com
buildScan {
    licenseAgreementUrl = "https://gradle.com/terms-of-service"
    licenseAgree = "yes"
}

//workaround for #1444, delete when Shipkit bug is fixed
subprojects {
    eclipse {
        project {
            name = rootProject.name + "-" + project.name
        }
    }

    afterEvaluate {
        val lib = publishing.publications.javaLibrary
        if(lib && !lib.artifactId.startsWith("mockito-")) {
            lib.artifactId = "mockito-" + lib.artifactId
        }
    }
}
//end workaround
