package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import ru.spbstu.grammarConverter.parseRules
import ru.spbstu.grammarConverter.toMarkdown
import java.io.File

private fun convertGrammar(grammarFilePath: String, outputMarkdownFilePath: String) {
    val grammarFile = File(grammarFilePath).inputStream()
    val outputMarkdownFile = File(outputMarkdownFilePath).outputStream()

    grammarFile.use {
        outputMarkdownFile.bufferedWriter().use { writer ->
            val rules = parseRules(grammarFile)
            writer.append(rules.toMarkdown(true)).appendln()
        }
    }
}

private object Driver : CliktCommand() {
    val inputFilePath by option("-i", "--input", help="path to grammar file in ANTLR format (.g4)").required()
    val outputFilePath by option("-o", "--output", help="path to output file in markdown format (.md)").required()
    override fun run() = convertGrammar(inputFilePath, outputFilePath)
}

fun main(args: Array<String>) = Driver.main(args)
