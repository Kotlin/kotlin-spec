val pdfBuildDir = "${project.parent?.buildDir}/spec/pdf"
val scriptsDir = "${project.parent?.projectDir}/scripts/build"

tasks.create<Exec>("build") {
    group = "internal"

    inputs.dir("${project.parent?.projectDir}/src/md/kotlin.core")
    outputs.files("${pdfBuildDir}/kotlin-spec.pdf")

    dependsOn(":docs:prepareShell")
    dependsOn(":docs:convertGrammar")
    dependsOn(":docs:filtersJar")

    environment["PROJECT_DIR"] = project.parent?.projectDir!!

    doFirst {
        workingDir = File(scriptsDir)
        commandLine = listOf("bash", "$scriptsDir/buildPdf.sh")

        File(pdfBuildDir).mkdirs()
    }
}
