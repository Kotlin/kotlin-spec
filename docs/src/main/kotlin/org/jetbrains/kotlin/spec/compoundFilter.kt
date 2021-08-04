package org.jetbrains.kotlin.spec

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import ru.spbstu.pandoc.Format
import ru.spbstu.pandoc.Pandoc
import ru.spbstu.pandoc.PandocVisitor
import ru.spbstu.pandoc.jackson.constructObjectMapper
import ru.spbstu.pandoc.makeFilter
import java.io.File

operator fun PandocVisitor.plus(otherVisitor: PandocVisitor) = object : PandocVisitor() {
    override fun visit(doc: Pandoc): Pandoc {
        return otherVisitor.visit(this@plus.visit(doc))
    }
}

private object CompoundFilter : CliktCommand() {
    val format: Format by argument("Pandoc output format").convert { Format(it) }
    val split: Boolean by option().flag("--no-split")
    val disableTODOS: Boolean by option().flag("--enable-todos")
    val imageDirectory: File? by option().file(fileOkay = false)
    val embed: Boolean by option().flag()
    val defaultFormat: String? by option()

    val disableStaticMath by option().flag("--enable-static-math")
    val katex by option()

    val outputDirectory: File by option().file(fileOkay = false).defaultLazy { File(".") }
    val generateTOC: Boolean by option().flag("--no-toc")

    override fun run() {
        outputDirectory.mkdirs()
        imageDirectory?.mkdirs()
        var visitor = listOf(
            SpecTodoFilterVisitor(format, disableTODOS),
            SpecSentencesFilterVisitor,
            SpecCopyPasteFilterVisitor,
            SpecInlineDiagramFilterVisitor(defaultFormat, format, imageDirectory, embed),
            MathInCode,
            BrokenReferencesReporter
        ).reduce { a, b -> a + b }

        if (!disableStaticMath && format.isHTML())
            visitor += InlineKatex(katex)

        val om = constructObjectMapper()
        val ii = om.readValue<Pandoc>(System.`in`)

        if (split) {
            visitor += Splitter(outputDirectory, format.format, generateTOC)
            visitor.visit(ii)
        } else {
            om.writeValue(System.out, visitor.visit(ii))
        }
    }
}

fun main(args: Array<String>) = CompoundFilter.main(args)
