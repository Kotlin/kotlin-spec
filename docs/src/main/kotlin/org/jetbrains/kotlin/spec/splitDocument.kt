package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.warnings.Warnings
import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.builder.BlockBuilder
import ru.spbstu.pandoc.builder.blocks
import ru.spbstu.pandoc.jackson.constructObjectMapper
import ru.spbstu.pandoc.jackson.readValue
import java.io.File

private class IdMapper(val splitLevel: Int) : PandocVisitor() {
    var currentSection: String = ""
    val entities: MutableMap<String, String> = mutableMapOf()

    override fun visit(b: Block): Block =
            super.visit(b).also {
                if (b is Attributes && b.attr.id.isNotBlank()) entities["#${b.attr.id}"] = currentSection
            }

    override fun visit(i: Inline): Inline =
            super.visit(i).also {
                if (i is Attributes && i.attr.id.isNotBlank()) entities["#${i.attr.id}"] = currentSection
            }

    override fun visit(b: Block.Header): Block {
        if (b.level <= splitLevel) currentSection = b.attr.id
        return super.visit(b)
    }
}

private class LinkFixer(splitLevel: Int, format: String) : PandocVisitor() {
    val ids: IdMapper = IdMapper(splitLevel)
    val ext = if (format !== "html") "pdf" else "html"

    override fun visit(doc: Pandoc): Pandoc {
        doc.accept(ids)
        return super.visit(doc)
    }

    override fun visit(i: Inline.Link): Inline {
        val targetId = i.target.v0
        if (targetId in ids.entities) {
            val sec = ids.entities[targetId]!!
            return i.copy(target = i.target.copy(v0 = "${sec}.${ext}${targetId}"))
        }
        return super.visit(i)
    }
}

val mapper = constructObjectMapper()

data class Section(val title: String, val number: Int?, val blocks: MutableList<Block> = mutableListOf())
data class TOCElement(val title: List<Inline>,
                      val id: String,
                      val numbered: Boolean = true,
                      val subSections: MutableList<TOCElement> = mutableListOf()) {
    fun add(level: Int, title: List<Inline>, id: String, numbered: Boolean = true): Unit = when (level) {
        0 -> subSections += TOCElement(title, id, numbered = numbered)
        else -> {
            when {
                subSections.isEmpty() -> subSections.add(TOCElement(listOf(), ""))
            }
            subSections.last().add(level - 1, title, id, numbered = numbered)
        }
    }

    fun toPandocList(): List<Block> = blocks { toPandocList(this@TOCElement) }
}

fun BlockBuilder.toPandocList(tocElement: TOCElement): Unit = with(tocElement) {
    if (title.isNotEmpty()) {
        plain {
            link(Target("#${id}", "")) {
                classes += "toc-element"
                if (!numbered) classes += "unnumbered"
                id = "toc-element-${this@with.id}"
                +title
            }
        }
    }

    if (subSections.isNotEmpty()) {
        bulletList {
            for (subSec in subSections) {
                item {
                    toPandocList(subSec)
                }
            }
        }
    }
}

class TOCMaker : PandocVisitor() {
    val fakeTop = TOCElement(listOf(), "top")

    /* this is a shallow visitor, not going below level 1 */
    override fun visit(b: Block): Block =
            @Suppress(Warnings.USELESS_CAST)
            if (b is Block.Header) visit(b as Block.Header)
            else b

    override fun visit(b: Block.Header): Block {
        fakeTop.add(b.level, b.text, b.attr.id, numbered = "unnumbered" !in b.attr.classes)
        return b
    }

    fun buildPandocList(): Block =
            fakeTop
                    .toPandocList()
                    .filterIsInstance<Block.BulletList>()
                    .first()
                    .items
                    .first()
                    .let { Block.Div(Attr(id = "TOC"), it) }
}

fun counterName(splitLevel: Int): String? = when (splitLevel) {
    1 -> "part"
    2 -> "chapter"
    3 -> "section"
    4 -> "subsection"
    5 -> "subsubsection"
    6 -> "paragraph"
    else -> null
}

fun setSectionCounterLatex(secNumber: Int, splitLevel: Int): Block {
    val counterName = counterName(splitLevel) ?: return Block.Null
    return Block.RawBlock(Format.TeX, """\setcounter{$counterName}{${secNumber - 1}}""")
}

fun setSectionCounterHtml(secNumber: Int, splitLevel: Int): Block {
    val counterName = counterName(splitLevel) ?: return Block.Null
    return Block.RawBlock(Format.HTML, """<span style="visibility: hidden; counter-reset: $counterName ${secNumber - 1};"></span>""")
}

class Splitter(val outputDirectory: File, val format: String, val generateTOC: Boolean, val splitLevel: Int = 2) : PandocVisitor() {
    override fun visit(doc: Pandoc): Pandoc {
        val linkFixer = LinkFixer(splitLevel, format)
        val newDoc = linkFixer.visit(doc)
        val generatedTOC = when {
            generateTOC -> TOCMaker()
                    .apply { visit(doc) }
                    .buildPandocList()
                    .let { linkFixer.visit(it) }
            else -> null
        }

        val blockIt = newDoc.blocks.iterator()

        val preamble = mutableListOf<Block>()
        val sections = mutableListOf<Section>()

        var secNumber = 0

        for (block in blockIt) {
            if (block is Block.Header && block.level <= splitLevel) {
                if ("unnumbered" !in block.attr.classes && block.level == splitLevel) {
                    sections.add(Section(block.attr.id, ++secNumber))
                } else {
                    sections.add(Section(block.attr.id, null))
                }

                sections.last().blocks.add(block)
                break
            }
            preamble += block
        }

        for (block in blockIt) {
            if (block is Block.Header && block.level <= splitLevel) {
                if ("unnumbered" !in block.attr.classes && block.level == splitLevel) {
                    sections.add(Section(block.attr.id, ++secNumber))
                } else {
                    sections.add(Section(block.attr.id, null))
                }
            }
            sections.last().blocks.add(block)
        }

        for ((sectionTitle, sectionNumber, sectionBlocks) in sections) {
            val secDoc = newDoc.copy(
                    blocks = blocks {
                        +preamble

                        if (generatedTOC != null) +generatedTOC

                        when {
                            sectionNumber == null -> {
                            }
                            format == "html" -> +setSectionCounterHtml(sectionNumber, splitLevel)
                            else -> +setSectionCounterLatex(sectionNumber, splitLevel)
                        }

                        +sectionBlocks
                    }
            )
            File(outputDirectory, "$sectionTitle.json").bufferedWriter().use {
                mapper.writeValue(it, secDoc)
            }
        }
        return doc // not actually a filter
    }
}

private object SplitDocument : CliktCommand() {
    val format: String by option("-f", "--format", help = "Format to use").defaultLazy { "html" }
    val outputDirectory: File by option().convert { File(it) }.defaultLazy { File(".") }
    val generateTOC: Boolean by option().flag("--toc")

    override fun run() {
        val doc: Pandoc = mapper.readValue(System.`in`)
        outputDirectory.mkdirs()
        Splitter(outputDirectory, format, generateTOC).visit(doc)
    }
}

fun main(args: Array<String>) = SplitDocument.main(args)
