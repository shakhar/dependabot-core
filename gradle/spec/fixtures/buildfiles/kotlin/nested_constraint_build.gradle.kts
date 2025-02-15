group "me.minidigger"

apply(plugin = "com.github.johnrengelman.shadow")

task copyToServer(type: Copy) {
    from shadowJar
    into testServerFolder
}

shadowJar {
    mergeServiceFiles()
    configurations = listOf(project.configurations.compile)

    //relocate "org.bstats", "com.voxelgameslib.voxelgameslib.metrics" TODO relocate bstats

    manifest {
        attributes "Implementation-Version": project.version + "@" + revision
    }
}

build.dependsOn shadowJar

dependencies {
    implementation(project(":ChatMenuAPI"))

    implementation("org.springframework:spring-web")

    constraints {
        implementation("org.springframework:spring-web:5.0.2.RELEASE")
    }
}

task createPom() {
    pom {
        project {
            groupId "com.voxelgameslib"
            artifactId "dependencies"
            version version
        }
    }.writeTo("pom.xml")
}
