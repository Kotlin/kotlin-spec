package org.jetbrains.kotlin.spec

import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.helper.*

object BrokenReferencesReporter: PandocVisitor() {

    var lastHeader: Block? = null

    fun detect(b: Block): Block {
        val match = """\[[^]]+\]\[[^]]+\]""".toRegex().find(b.getContentsAsText());

        if(match !== null) {
            System.err.println("Broken link detected in section '${lastHeader?.getContentsAsText()}': " + match.value)
        }

        return b
    }

    override fun visit(b: Block.Para) = detect(b)
    override fun visit(b: Block.Plain) = detect(b)
    override fun visit(b: Block.LineBlock) = detect(b)

    override fun visit(b: Block.Header) = b.also { lastHeader = b }

}

fun main() = makeFilter(BrokenReferencesReporter)