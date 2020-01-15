package org.jetbrains.kotlin.spec

import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.helper.*

private class ModifiedToStringVisitor: PandocVisitor() {

    val builder = StringBuilder()

    override fun visit(i: Inline.Code): Inline = i.also { builder.append("$") } // no code
    override fun visit(i: Inline.Math): Inline = i.also { builder.append("$") } // no math
    override fun visit(i: Inline.RawInline): Inline = i.also { builder.append("$") } // no LaTeX

    override fun visit(i: Inline.Str): Inline {
        builder.append(i.text)
        return super.visit(i)
    }

    override fun visit(i: Inline.Quoted): Inline {
        val quotes = when(i.type) {
            QuoteType.DoubleQuote -> "\""
            QuoteType.SingleQuote -> "\'"
        }
        builder.append(quotes)
        val res = super.visit(i)
        builder.append(quotes)
        return res
    }

    override fun visit(i: Inline.Space): Inline {
        builder.append(" ")
        return super.visit(i)
    }

    override fun visit(i: Inline.SoftBreak): Inline {
        builder.append("\n")
        return super.visit(i)
    }

    override fun visit(i: Inline.LineBreak): Inline {
        builder.append("\n")
        return super.visit(i)
    }
}

object BrokenReferencesReporter: PandocVisitor() {

    var lastHeader: Block? = null
    val re = """\[[^]]+\]\[[^]]+\]""".toRegex()

    fun toCharSequence(b: Block): CharSequence =
        ModifiedToStringVisitor().also { b.accept(it) }.builder

    fun detect(b: Block): Block {
        val match = re.find(toCharSequence(b));

        if(match !== null) {
            System.err.println("Broken link detected in section '${lastHeader?.getContentsAsText()}': " + match.value)
        }

        return b
    }

    override fun visit(b: Block.Para) = detect(b)
    override fun visit(b: Block.Plain) = detect(b)
    override fun visit(b: Block.LineBlock) = detect(b)

    override fun visit(b: Block.Header) = detect(b.also { lastHeader = b })

}

fun main() = makeFilter(BrokenReferencesReporter)