#!/bin/env kscript

@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("ru.spbstu:g4toEBNF:0.0.0.1")

import ru.spbstu.grammarConverter.*
import java.io.File

try {
    val inputFile = File(args[0]).inputStream()
    val outputFile = File(args[1]).outputStream()

    inputFile.use {
        outputFile.bufferedWriter().use { w ->
            val rules = parseRules(inputFile)
            w.append(rules.toMarkdown(true)).appendln()
        }
    }
} catch (ex: Exception) {
    System.err.println("Usage: ConvertGrammar <inputFile> <outputFile>")
}
