package org.jetbrains.kotlin.spec

import com.xenomachina.argparser.ArgParser
import ru.spbstu.grammarConverter.*
import java.io.File

private fun convertGrammar(grammarFilePath: String, outputMarkdownFilePath: String) {
    val z = File(grammarFilePath).absolutePath
    val grammarFile = File(grammarFilePath).inputStream()
    val outputMarkdownFile = File(outputMarkdownFilePath).outputStream()

    grammarFile.use {
        outputMarkdownFile.bufferedWriter().use { writer ->
            val rules = parseRules(grammarFile)
            writer.append(rules.toMarkdown(true)).appendln()
        }
    }
}

fun main(args: Array<String>) {
    val parser = ArgParser(args)
    val inputFilePath by parser.storing("-i", "--input", help="path to grammar file in ANTLR format (.g4)")
    val outputFilePath by parser.storing("-o", "--output", help="path to output file in markdown format (.md)")

    convertGrammar(inputFilePath, outputFilePath)
}
