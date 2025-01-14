plugins {
    id("org.springframework.boot") version "2.0.5.RELEASE" apply false
    id("com.google.protobuf") version "0.8.4" apply false
}

allprojects {
    task downloadDependencies {
        doLast {
            configurations.all {
                try {
                    it.files
                } catch (e) {
                    project.logger.info(e.message)
                }
            }
        }
    }
}

configure(subprojects.findAll { !it.name.startsWith("examples/") }) {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven")

    repositories {
        jcenter()
    }

    project.afterEvaluate {
        project.tasks.findByName("install")?.dependsOn(tasks.findByName("assemble"))
    }

    dependencyManagement {
        overriddenByDependencies = false

        imports {
            mavenBom "org.junit:junit-bom:5.3.1"
            mavenBom "org.springframework.boot:spring-boot-dependencies:2.0.5.RELEASE"
            mavenBom "org.testcontainers:testcontainers-bom:1.9.1"
        }

        dependencies {
            dependency "org.projectlombok:lombok:1.18.2"

            dependency "org.lognet:grpc-spring-boot-starter:2.4.2"

            dependency "org.pf4j:pf4j:2.4.0"

            dependencySet(group = "com.google.protobuf", version = "3.6.1") {
                entry "protoc"
                entry "protobuf-java"
                entry "protobuf-java-util"
            }

            dependency "org.apache.kafka:kafka-clients:3.6.1"

            dependency "com.google.auto.service:auto-service:1.0-rc4"

            dependencySet(group = "io.grpc", version = "1.15.1") {
                entry "grpc-netty"
                entry "grpc-core"
                entry "grpc-services"
                entry "grpc-protobuf"
                entry "grpc-stub"
                entry "protoc-gen-grpc-java"
            }

            dependency "com.salesforce.servicelibs:reactor-grpc-stub:0.9.0"

            dependency "org.awaitility:awaitility:3.1.2"
        }
    }
}
