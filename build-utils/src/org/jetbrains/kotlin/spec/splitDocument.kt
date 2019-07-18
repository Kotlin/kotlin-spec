package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import ru.spbstu.pandoc.*
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

private class LinkFixer(splitLevel: Int) : PandocVisitor() {
    val ids: IdMapper = IdMapper(splitLevel)

    override fun visit(doc: Pandoc): Pandoc {
        doc.accept(ids)
        return super.visit(doc)
    }

    override fun visit(i: Inline.Link): Inline {
        val targetId = i.target.v0
        if (targetId in ids.entities) {
            val sec = ids.entities[targetId]!!
            return i.copy(target = i.target.copy(v0 = "${sec}.html${targetId}"))
        }
        return super.visit(i)
    }
}

val mapper = constructObjectMapper()

data class Section(val title: String, val blocks: MutableList<Block> = mutableListOf())

private class Splitter(val outputDirectory: File, val splitLevel: Int = 2) : PandocVisitor() {
    override fun visit(doc: Pandoc): Pandoc {
        val newDoc = LinkFixer(splitLevel).visit(doc)

        val blockIt = newDoc.blocks.iterator()

        val preamble = mutableListOf<Block>()
        val sections = mutableListOf<Section>()

        for (block in blockIt) {
            if (block is Block.Header && block.level <= splitLevel) {
                sections.add(Section(block.attr.id))
                sections.last().blocks.add(block)
                break
            }
            preamble += block
        }

        for (block in blockIt) {
            if (block is Block.Header && block.level <= splitLevel) {
                sections.add(Section(block.attr.id))
            }
            sections.last().blocks.add(block)
        }

        for ((sectionTitle, sectionBlocks) in sections) {
            val secDoc = newDoc.copy(blocks = preamble + sectionBlocks)
            File(outputDirectory, "$sectionTitle.json").bufferedWriter().use {
                mapper.writeValue(it, secDoc)
            }
        }
        return doc // not actually a filter
    }
}

private object SplitDocument : CliktCommand() {
    val outputDirectory: File by option().convert { File(it) }.defaultLazy { File(".") }

    override fun run() {
        val doc: Pandoc = mapper.readValue(System.`in`)
        outputDirectory.mkdirs()
        Splitter(outputDirectory).visit(doc)
    }
}

fun main(args: Array<String>) = SplitDocument.main(args)
