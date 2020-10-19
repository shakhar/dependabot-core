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

import org.apache.tools.ant.filters.ReplaceTokens

build.dependsOn shadowJar

dependencies {
    implementation(project(":ChatMenuAPI"))

    implementation(group = "co.aikar", name = "acf-paper", version = "0.5.0-SNAPSHOT", changing: true)
    implementation(group = "com.google.inject", name = "guice", version = "4.2.0")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
    implementation(group = "de.davidbilge", name = "jskill", version = "1.1-SNAPSHOT")
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
