import java.nio.file.Paths

val htmlBuildDir = "${project.parent?.layout?.buildDirectory?.get()}/spec/html"
val scriptsDir = "${project.parent?.projectDir}/scripts/build"

tasks.create<Exec>("build") {
    group = "internal"

    inputs.dir("${project.parent?.projectDir}/src/md/kotlin.core")

    dependsOn(":docs:prepareShell")
    dependsOn(":docs:convertGrammar")
    dependsOn(":docs:filtersJar")

    environment["PROJECT_DIR"] = project.parent?.projectDir!!

    doFirst {
        workingDir = File(scriptsDir)
        commandLine = listOf("bash", "$scriptsDir/buildHtmlBySections.sh")

        Paths.get(htmlBuildDir).toFile().apply { deleteRecursively(); mkdirs() }
    }
}
