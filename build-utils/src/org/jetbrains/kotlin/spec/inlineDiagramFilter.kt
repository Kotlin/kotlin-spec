package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.spbstu.diagrams.CharMatrix
import ru.spbstu.diagrams.Diagram
import ru.spbstu.diagrams.DiagramProperties
import ru.spbstu.diagrams.export.exportAsInlinePNG
import ru.spbstu.diagrams.export.exportAsInlineSVG
import ru.spbstu.diagrams.export.exportAsPNG
import ru.spbstu.diagrams.export.exportAsSVG
import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.builder.InlineBuilder
import ru.spbstu.pandoc.builder.blocks
import java.io.File

private enum class ImgFormat(val suffix: String) { PNG(".png"), SVG(".svg");

    companion object {
        fun fromExtension(ext: String) = when (ext) {
            "svg" -> SVG
            "png" -> PNG
            else -> throw IllegalArgumentException("Unknown format: $ext")
        }
    }
}

private val visitor = object : PandocVisitor() {
    override fun visit(b: Block.CodeBlock): Block {
        if ("diagram" !in b.attr.classes) return super.visit(b)

        val props = b.attr.propertiesMap()
        val altText = props["alt-text"]
        val format = props["format"] ?: Main.defaultFormat ?: "svg"

        val dprops = DiagramProperties(
                fontSpec = "Fira Code;Fira Mono;Consolas;Monospaced",
                textScale = 1.5,
                diagramScale = 0.7
        )
        val diag = Diagram.fromMatrix(
                CharMatrix.read(b.text.reader().buffered()),
                dprops
        )

        return blocks {
            div {
                plain {
                    renderToFile(ImgFormat.fromExtension(format), diag, altText)
                }
            }
        }.first()
    }
}

private val htmlFormats = setOf("html", "html4", "html5", "revealjs", "s5", "slideous", "slidy")

private fun InlineBuilder.renderToFile(format: ImgFormat, diag: Diagram, altText: String?) {
    if (Main.format.isHTML() && Main.embed) {
        when(format) {
            ImgFormat.SVG -> rawInline(format = Format("html")) {
                "<img src='${ exportAsInlineSVG(diag) }' />"
            }
            ImgFormat.PNG -> rawInline(format = Format("html")) {
                "<img src='${ exportAsInlinePNG(diag) }' />"
            }
        }
        return
    }
    val file = createTempFile(suffix = format.suffix, directory = Main.imageDirectory)

    when (format) {
        ImgFormat.SVG -> exportAsSVG(diag, file)
        ImgFormat.PNG -> exportAsPNG(diag, file)
    }

    image(Target(file.absolutePath, altText ?: file.absolutePath))
}

private object Main : CliktCommand() {
    val format: Format by argument("Pandoc output format").convert { Format(it) }
    val imageDirectory: File? by option().convert { File(it) }
    val embed: Boolean by option().flag()
    val defaultFormat: String? by option()

    override fun run() {
        makeFilter(visitor)
    }
}

fun main(args: Array<String>) = Main.main(args)
