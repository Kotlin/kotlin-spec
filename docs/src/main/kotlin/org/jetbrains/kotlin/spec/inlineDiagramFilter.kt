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

class SpecInlineDiagramFilterVisitor(
    val defaultFormat: String?,
    val format: Format,
    val imageDirectory: File?,
    val embed: Boolean
) : PandocVisitor() {
    override fun visit(b: Block.CodeBlock): Block {
        if ("diagram" !in b.attr.classes) return super.visit(b)

        val props = b.attr.propertiesMap
        val altText = props["alt-text"]
        val imageFormat = props["format"] ?: defaultFormat ?: "svg"

        // this is a hack, but for some reason the same svg is much smaller in HTML than in LaTeX
        val scaling = if (format.isHTML()) 0.7 else 0.35

        val dprops = DiagramProperties(
                fontSpec = "Fira Mono;Consolas;Monospaced",
                textScale = 1.5,
                diagramScale = scaling
        )
        val diag = Diagram.fromMatrix(
                CharMatrix.read(b.text.reader().buffered()),
                dprops
        )

        return blocks {
            div {
                clazz = "diagram"
                id = b.attr.id
                plain {
                    renderToFile(
                        ImgFormat.fromExtension(imageFormat),
                        diag,
                        altText,
                        imageDirectory,
                        embed,
                        format
                    )
                }
            }
        }.first()
    }
}

private val htmlFormats = setOf("html", "html4", "html5", "revealjs", "s5", "slideous", "slidy")

private fun InlineBuilder.renderToFile(
    imageFormat: ImgFormat,
    diag: Diagram,
    altText: String?,
    imageDirectory: File?,
    embed: Boolean,
    format: Format
) {
    if (format.isHTML() && embed) {
        when(imageFormat) {
            ImgFormat.SVG -> rawInline(format = Format.HTML) {
                "<img src='${ exportAsInlineSVG(diag) }' />"
            }
            ImgFormat.PNG -> rawInline(format = Format.HTML) {
                "<img src='${ exportAsInlinePNG(diag) }' />"
            }
        }
        return
    }
    val file = createTempFile(suffix = imageFormat.suffix, directory = imageDirectory)

    when (imageFormat) {
        ImgFormat.SVG -> exportAsSVG(diag, file)
        ImgFormat.PNG -> exportAsPNG(diag, file)
    }

    image(Target(file.absolutePath, altText ?: file.absolutePath))
}

private object Main : CliktCommand() {
    private val format: Format by argument("Pandoc output format").convert { Format(it) }
    private val imageDirectory: File? by option().convert { File(it) }
    private val embed: Boolean by option().flag()
    private val defaultFormat: String? by option()

    override fun run() {
        makeFilter(SpecInlineDiagramFilterVisitor(defaultFormat, format, imageDirectory, embed))
    }
}

fun main(args: Array<String>) = Main.main(args)
