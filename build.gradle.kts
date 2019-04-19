import at.phatbl.shellexec.ShellExec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Paths
import java.nio.file.Files

plugins {
    kotlin("jvm") version "1.3.21"
    id("at.phatbl.shellexec") version "1.1.3"
    application
}

group = "org.jetbrains.kotlin.spec"
version = "0.1"

repositories {
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://dl.bintray.com/vorpal-research/kotlin-maven") }
    mavenCentral()
}

java.sourceSets {
    "main" {
        java.srcDir("build-utils/src")
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("ru.spbstu:g4toEBNF:0.0.0.2")
    compile("ru.spbstu:kotlin-pandoc:0.0.5")
    compile("ru.spbstu:simple-diagrammer:0.0.0.2")
    compile("com.github.ajalt:clikt:1.7.0")
}

fun ShellExec.buildBySections(format: String, wrapper: String) {
    val ls = System.lineSeparator()
    val excludeFiles = setOf("grammar.generated", "index")
    val commands = mutableListOf("PROJECT_DIR=$projectDir")
    val buildScriptsDir = "./build-utils/scripts/build"
    val buildTemplate = File("$buildScriptsDir/$wrapper").readText()

    File("docs/kotlin.core").listFiles().forEach {
        val section = it.nameWithoutExtension

        if (it.extension != "md" || excludeFiles.contains(section))
            return@forEach

        commands.add(buildTemplate.replace("<section>", section))
    }

    workingDir = File(buildScriptsDir)
    command = commands.joinToString(ls)

    File("./build/spec/$format/sections").mkdirs()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.create<ShellExec>("buildHtml") {
    dependsOn("convertGrammar")

    doFirst {
        val ls = System.lineSeparator()
        val buildScriptsDir = "./build-utils/scripts/build"
        val buildTemplate = File("$buildScriptsDir/buildHtml.sh").readText()
        val buildDirectory = "./build/spec/html"

        workingDir = File(buildScriptsDir)
        command = "PROJECT_DIR=$projectDir$ls$buildTemplate"

        File(buildDirectory).mkdirs()

        val assetsSourceDirectory = Paths.get("./assets")
        val assetsDestinationDirectory = Paths.get("$buildDirectory/assets").apply { toFile().deleteRecursively() }

        Files.walk(assetsSourceDirectory).forEach { source ->
            if (source.toFile().extension != "md")
                Files.copy(source, assetsDestinationDirectory.resolve(assetsSourceDirectory.relativize(source)))
        }
    }
}

tasks.create<ShellExec>("buildPdf") {
    dependsOn("convertGrammar")

    doFirst {
        val ls = System.lineSeparator()
        val buildScriptsDir = "./build-utils/scripts/build"
        val buildTemplate = File("$buildScriptsDir/buildPdf.sh").readText()

        workingDir = File(buildScriptsDir)
        command = "PROJECT_DIR=$projectDir$ls$buildTemplate"

        File("./build/spec/pdf").mkdirs()
    }
}

tasks.create<ShellExec>("buildPdfBySections") {
    dependsOn("convertGrammar")
    doFirst { buildBySections("pdf", "buildPdfBySections.sh") }
}

tasks.create<ShellExec>("buildHtmlBySections") {
    dependsOn("convertGrammar")
    doFirst { buildBySections("html", "buildHtmlBySections.sh") }
}

tasks.create<JavaExec>("convertGrammar") {
    val inputFile = "docs/kotlin.core/Grammar.g4"
    val outputFile = "docs/kotlin.core/grammar.generated.md"

    inputs.file(inputFile)
    outputs.file(outputFile)

    classpath = java.sourceSets["main"].runtimeClasspath
    main = "org.jetbrains.kotlin.spec.ConvertGrammarKt"
    args = listOf("-i", inputFile, "-o", outputFile)
}

tasks.create<JavaExec>("execute") {
    classpath = java.sourceSets["main"].runtimeClasspath
    main = if (project.hasProperty("mainClass")) project.property("mainClass") as String else ""
    if (project.hasProperty("args")) {
        args = (project.property("args") as String).split(" ")
    }
    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
}
