rootProject.name = "gradle-profiler"

include("chrome-trace")
project(":chrome-trace").projectDir = new File(rootDir, "subprojects/chrome-trace")

include("beam-examples-java")
project(":beam-examples-java").dir = file("examples/java")
