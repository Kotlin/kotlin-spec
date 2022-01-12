import java.nio.file.Paths

val pdfBuildDir = "${project.parent?.buildDir}/spec/pdf"
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
        commandLine = listOf("bash", "$scriptsDir/buildPdfBySections.sh")


        Paths.get("$pdfBuildDir/sections").toFile().apply { deleteRecursively(); mkdirs() }
    }
}
