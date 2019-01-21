import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.util.regex.Pattern

plugins {
    kotlin("jvm") version "1.3.11"
    idea
    id("org.jetbrains.intellij") version "0.4.1"
    antlr
}

group = "org.jetbrains.kotlin"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

java.sourceSets {
    "main" {
        java.srcDir("src/main")
    }
    "test" {
        java.srcDir("src/test")
    }
}

idea {
    module {
        excludeDirs = File("testData").walkTopDown().filter { !it.name.endsWith("antlrtree.txt") }.toSet()
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("junit:junit:4.12")
    antlr("org.antlr:antlr4:4.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions {
        freeCompilerArgs = listOf("-Xnew-inference")
    }
}

tasks.withType<AntlrTask> {
    outputDirectory = File("${project.projectDir}/src/main/java/org/jetbrains/kotlin/grammar/parser").also { it.mkdirs() }

    arguments.add("-package")
    arguments.add("org.jetbrains.kotlin.grammar.parser")

    listOf("KotlinLexer.g4", "KotlinParser.g4", "UnicodeClasses.g4").forEach {
        val targetFile = File("${project.projectDir}/src/main/antlr/$it")

        File("${project.projectDir}/../$it").copyTo(targetFile, true)
        targetFile.writeText(
                "/* DO NOT MODIFY! This file was copied from `src/grammar` folder. */" + System.lineSeparator().repeat(2) +
                        targetFile.readText()
        )
    }
}

tasks.create("removeCompilerTestData") {
    doFirst {
        File("testData").walkTopDown().forEach {
            if (!it.name.endsWith("antlrtree.txt"))
                it.delete()
        }
    }
}

tasks.create("prepareDiagnosticsCompilerTests") {
    doFirst {
        val individualDiagnostic = """(\w+;)?(\w+:)?(\w+)(?:\(.*?[^\\]\))?"""
        val rangeStartOrEndPattern = Pattern.compile("(<!$individualDiagnostic(,\\s*$individualDiagnostic)*!>)|(<!>)")
        val filePattern = Pattern.compile("""// ?FILE: ?""")
        val ls = System.lineSeparator()
        val sourceCodeByFilePattern = Pattern.compile("""^(?<filename>.*?)\.(?<extension>kts?|java)($ls)*(?<code>[\s\S]*)$""")

        File("testData/diagnostics").walkTopDown().forEach {
            if (it.extension == "kt") {
                val sourceCode = it.readText()
                val sourceCodeByFiles = sourceCode.split(filePattern)

                if (sourceCodeByFiles.size > 1) {
                    sourceCodeByFiles.forEachIndexed { index, content ->
                        if (index == 0)
                            return@forEachIndexed

                        val matcher = sourceCodeByFilePattern.matcher(content).apply { find() }
                        val filename = matcher.group("filename").replace("/", "_")
                        val extension = matcher.group("extension")
                        val code = matcher.group("code")

                        if (extension == "kt" || extension == "kts")
                            File("${it.parent}/${it.nameWithoutExtension}.$filename.$extension").writeText(rangeStartOrEndPattern.matcher(code).replaceAll(""))
                    }
                    it.delete()
                } else {
                    it.writeText(rangeStartOrEndPattern.matcher(it.readText()).replaceAll(""))
                }
            } else if (!it.name.endsWith("antlrtree.txt")) {
                it.delete()
            }
        }
    }
}
