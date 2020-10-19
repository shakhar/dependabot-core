import org.jetbrains.kotlin.config.KotlinCompilerVersion

dependencies {
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.sparkjava:spark-core:2.5.4")
    implementation("org.slf4j:slf4j-simple:1.7.21")
    implementation("com.github.jeremyh:jBCrypt:master-SNAPSHOT")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation("mysql:mysql-connector-java:5.1.6")

}
