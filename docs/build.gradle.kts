import at.phatbl.shellexec.ShellExec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Paths

plugins {
    application
    id("kotlin") version "1.3.41"
    id("at.phatbl.shellexec")
}

group = "org.jetbrains.kotlin.spec"
version = "0.1"

val htmlBuildDir = "$buildDir/spec/html"
val pdfBuildDir = "$buildDir/spec/pdf"
val resourcesBuildDir = "$htmlBuildDir/resources"
val jsBuildDir = "$resourcesBuildDir/js"
val scriptsDir = "$projectDir/scripts/build"
val ls: String = System.lineSeparator()

fun getScriptText(scriptName: String): String {
    val buildTemplate = File("$scriptsDir/$scriptName.sh").readText()

    return "PROJECT_DIR=$projectDir$ls$buildTemplate"
}

repositories {
    maven { setUrl("https://dl.bintray.com/vorpal-research/kotlin-maven") }
    mavenCentral()
}

java.sourceSets {
    "main" {
        java.srcDir("src/main/kotlin")
    }
}

dependencies {
    compile("ru.spbstu:g4toEBNF:0.0.0.3")
    compile("ru.spbstu:kotlin-pandoc:0.0.9")
    compile("ru.spbstu:simple-diagrammer:0.0.0.6")
    compile("com.github.ajalt:clikt:1.7.0")
    compile("com.zaxxer:nuprocess:1.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.create<ShellExec>("buildPdf") {
    group = "internal"

    dependsOn("convertGrammar")

    doFirst {
        workingDir = File(scriptsDir)
        command = getScriptText("buildPdf")

        File(pdfBuildDir).mkdirs()
    }
}

tasks.create<ShellExec>("buildPdfBySections") {
    group = "internal"

    dependsOn("convertGrammar")

    doFirst {
        workingDir = File(scriptsDir)
        command = getScriptText("buildPdfBySections")

        Paths.get("$pdfBuildDir/sections").toFile().apply { deleteRecursively(); mkdirs() }
    }
}

tasks.create<JavaExec>("convertGrammar") {
    val inputFile = "./src/md/kotlin.core/Grammar.g4"
    val outputFile = "./src/md/kotlin.core/grammar.generated.md"

    inputs.file(inputFile)
    outputs.file(outputFile)

    classpath = java.sourceSets["main"].runtimeClasspath
    main = "org.jetbrains.kotlin.spec.ConvertGrammarKt"
    args = listOf("-i", inputFile, "-o", outputFile)
}

tasks.create<ShellExec>("buildHtml") {
    group = "internal"

    dependsOn("convertGrammar")

    doFirst {
        workingDir = File(scriptsDir)
        command = getScriptText("buildHtml")

        File(htmlBuildDir).mkdirs()
    }
}

tasks.create<ShellExec>("buildHtmlBySections") {
    group = "internal"

    dependsOn("convertGrammar")

    doFirst {
        workingDir = File(scriptsDir)
        command = getScriptText("buildHtmlBySections")

        Paths.get("$htmlBuildDir/sections").toFile().apply { deleteRecursively(); mkdirs() }
    }
}

tasks.create<JavaExec>("execute") {
    group = "internal"

    classpath = java.sourceSets["main"].runtimeClasspath

    main = project.findProperty("mainClass") as? String ?: ""
    args = (project.findProperty("args") as? String)?.split(" ") ?: emptyList()

    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
}
