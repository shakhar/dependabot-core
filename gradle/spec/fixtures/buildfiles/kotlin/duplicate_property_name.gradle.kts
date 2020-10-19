buildscript {
    ext {
        kotlinVersion = "1.3.50"
        spek_version = "2.0.6"
    }

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:$publish_version")
    }
}

configurations {
    dependabot
}

dependencies {
    dependabot "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    dependabot "org.spekframework.spek2:spek-dsl-jvm:$spek_version"
}

task removeRepo(type: Delete) {
    delete gradle.ext.repo
}

listOf(
    "spek",
).each {
    buildscript {
    ext {
        kotlinVersion = "1.3.50"
        spek_version = "2.0.6"
    }

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:$publish_version")
    }
}

configurations {
    dependabot
}

dependencies {
    dependabot "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    dependabot "org.spekframework.spek2:spek-dsl-jvm:$spek_version"
}

task removeRepo(type: Delete) {
    delete gradle.ext.repo
}

listOf(
    "spek",
).each {
    val generateHardCodedDependencies = project.task("generateHardCodedDependencies", group = "build") {
        doLast {
            val folder = new File("${project.projectDir}/src/main/groovy/ch/dependabot/gradle/$projectName/generated")
            mkdir folder
            new File(folder, "Dependencies.groovy").text =
                """package ch.dependabot.gradle.${projectName}.generated
                |
                |class Dependencies {
                |   public static final spek_version = "$project.spek_version"
                |}
                """.stripMargin("|")
        }
    }
    project.compileGroovy.dependsOn generateHardCodedDependencies
}

        doLast {
            buildscript {
    ext {
        kotlinVersion = "1.3.50"
        spek_version = "2.0.6"
    }

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:$publish_version")
    }
}

configurations {
    dependabot
}

dependencies {
    dependabot "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    dependabot "org.spekframework.spek2:spek-dsl-jvm:$spek_version"
}

task removeRepo(type: Delete) {
    delete gradle.ext.repo
}

listOf(
    "spek",
).each {
    val generateHardCodedDependencies = project.task("generateHardCodedDependencies", group = "build") {
        doLast {
            val folder = new File("${project.projectDir}/src/main/groovy/ch/dependabot/gradle/$projectName/generated")
            mkdir folder
            new File(folder, "Dependencies.groovy").text =
                """package ch.dependabot.gradle.${projectName}.generated
                |
                |class Dependencies {
                |   public static final spek_version = "$project.spek_version"
                |}
                """.stripMargin("|")
        }
    }
    project.compileGroovy.dependsOn generateHardCodedDependencies
}

            mkdir folder
            new File(folder, "Dependencies.groovy").text =
                """package ch.dependabot.gradle.${projectName}.generated
                |
                |class Dependencies {
                |   public static val spek_version: final = "$project.spek_version"
                |}
                """.stripMargin("|")
        }
    }
    project.compileGroovy.dependsOn generateHardCodedDependencies
}
