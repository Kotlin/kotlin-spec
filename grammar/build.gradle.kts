import at.phatbl.shellexec.ShellExec
import java.io.File
import java.util.regex.Pattern

plugins {
    idea
    id("org.jetbrains.intellij") version "0.4.1"
    antlr
    `maven-publish`
}

group = "org.jetbrains.kotlin.spec.grammar"
version = "0.1"

apply(plugin = "kotlin")

val jar: Jar by tasks
val archivePrefix = "kotlin-grammar-parser"

repositories {
    maven { setUrl("https://dl.bintray.com/vorpal-research/kotlin-maven") }
    mavenCentral()
    jcenter()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = archivePrefix
            version = version as String

            from(components["java"])
        }
    }
}

java.sourceSets {
    "main" {
        java.srcDir("src/main")
    }
    "test" {
        java.srcDir("src/test")
    }
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
    }
}

dependencies {
    compile("junit:junit:4.12")
    antlr("org.antlr:antlr4:4.+")
}

tasks.withType<AntlrTask> {
    outputDirectory = File("${project.rootDir}/grammar/src/main/java/org/jetbrains/kotlin/grammar/parser").also { it.mkdirs() }

    arguments.add("-package")
    arguments.add("org.jetbrains.kotlin.spec.grammar")
}

tasks.withType<Test> {
    workingDir = File("${project.rootDir}/${project.name}")
    ignoreFailures = project.hasProperty("teamcity")
}

tasks.create("removeCompilerTestData") {
    doFirst {
        File("${project.rootDir}/${project.name}/testData").walkTopDown().forEach {
            if (!it.name.endsWith("antlrtree.txt"))
                it.delete()
        }
    }
}

tasks.create<ShellExec>("downloadCompilerTests") {
    command = """svn export https://github.com/JetBrains/kotlin/trunk/compiler/testData/psi testData/psi --force
        |svn export https://github.com/JetBrains/kotlin/trunk/compiler/testData/diagnostics/tests testData/diagnostics --force""".trimMargin()
}

tasks.create("syncWithCompilerTests") {
    doFirst {
        File("${project.rootDir}/${project.name}/testData").walkTopDown().forEach {
            if (it.name.endsWith("antlrtree.txt") && !File("${it.parent}/${it.name.substringBefore(".antlrtree.txt")}.kt").exists())
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

        File("${project.rootDir}/${project.name}/testData/diagnostics").walkTopDown().forEach {
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

jar.archiveName = "$archivePrefix-$version.jar"

jar.manifest {
    attributes(
        mapOf(
            "Class-Path" to configurations.runtime.files.joinToString(" ") { it.name }
        )
    )
}

jar.from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
