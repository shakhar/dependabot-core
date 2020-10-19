buildscript {
    extra["kafkaScalaMajorVersion"] = "2.11"
    extra["kafkaScalaMinorVersion"] = "12"

    repositories {
        mavenCentral()
    }
}

val kafkaScalaMajorVersion: String by extra
val kafkaScalaMinorVersion: String by extra

dependencies {
  implementation("org.scala-lang:scala-library:${kafkaScalaMajorVersion}.${kafkaScalaMinorVersion}")

  implementation("com.google.guava:guava:17.0")
  implementation("org.osgi:org.osgi.core:6.0.0")

  implementation("org.apache.maven.shared:maven-invoker:3.0.1")
  implementation("ch.vorburger:fswatch:1.1.0")
  // TODO implementation("org.osgi:org.osgi.service.component.annotations:1.3.0")

  // We need to force latest slf4j-api(here so that we can use SubstituteLogger in LoggingOutputStreamTest)
  implementation("org.slf4j:slf4j-api:1.7.25")
  testImplementation("junit:junit:4.12")
  testImplementation("org.awaitility:awaitility:3.1.2")
  testRuntime "org.slf4j:slf4j-simple:1.7.25"
}
