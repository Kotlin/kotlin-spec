import at.phatbl.shellexec.ShellExec
import java.io.File
import java.net.URI
import java.util.regex.Pattern

plugins {
    idea
    id("org.jetbrains.intellij") version "1.17.3"
    antlr
    `maven-publish`
    kotlin("jvm")
    id("at.phatbl.shellexec")
}

group = "org.jetbrains.kotlin.spec.grammar"
version = "0.1"

val archivePrefix = "kotlin-grammar-parser"

repositories {
    maven {
        url = URI("https://maven.vorpal-research.science")
    }
    mavenCentral()
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

sourceSets {
    main {
        java.srcDir("src/main")
    }
    test {
        java.srcDir("src/test")
    }
}

dependencies {
    implementation("junit:junit:4.13.2")
    antlr("org.antlr:antlr4:4.13.1")
}

tasks.compileKotlin {
    dependsOn("generateGrammarSource")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

intellij {
    version.set("IC-2022.1")
}

tasks.withType<AntlrTask> {
    outputDirectory =
        File("${project.rootDir}/grammar/src/main/java/org/jetbrains/kotlin/spec/grammar/parser").also { it.mkdirs() }

    arguments.add("-package")
    arguments.add("org.jetbrains.kotlin.spec.grammar")
}

tasks.create("removeFailedMarkerFiles") {
    doFirst {
        File("${project.rootDir}/${project.name}/testData")
            .walkTopDown()
            .filter { it.isFile && it.extension == "failed" }
            .forEach { it.delete() }
    }
}

tasks.withType<Test> {
    dependsOn("removeFailedMarkerFiles")

    workingDir = File("${project.rootDir}/${project.name}")
    ignoreFailures = project.hasProperty("teamcity")

    systemProperties["TEST_PATH_FILTER"] = System.getProperty("TEST_PATH_FILTER")
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
    doFirst {
        workingDir = File("$projectDir")
        command = File("$projectDir/scripts/build/downloadCompilerTests.sh").readText()
    }
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
        //language=RegExp
        val individualDiagnostic = """[^!,]*"""
        val rangeStartOrEndPattern = Pattern.compile("(<!(([^>])|((?<!\\!)\\>))+!>)|(<!>)")
        val filePattern = Pattern.compile("""// ?FILE: ?""")
        val ls = System.lineSeparator()
        val sourceCodeByFilePattern =
            Pattern.compile("""^(?<filename>.*?)\.(?<extension>kts?|java)($ls)*(?<code>[\s\S]*)$""")

        File("${project.rootDir}/${project.name}/testData/diagnostics").walkTopDown().forEach {
            println("Processing file $it")
            if (it.name.endsWith(".fir.kt")) {
                it.delete()
            } else if (it.extension == "kt") {
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
                            File("${it.parent}/${it.nameWithoutExtension}.$filename.$extension").writeText(
                                rangeStartOrEndPattern.matcher(code).replaceAll("")
                            )
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

val instrumentTestCodeTask = tasks.named("instrumentTestCode")

// tasks.named("inspectClassesForKotlinIC") {
//     dependsOn(instrumentTestCodeTask)
// }

tasks.withType<Jar> {
    dependsOn(instrumentTestCodeTask)

    archiveFileName.set("$archivePrefix-${archiveVersion.get()}.jar")

    manifest {
        attributes(
            mapOf(
                "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }
            )
        )
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })
}
