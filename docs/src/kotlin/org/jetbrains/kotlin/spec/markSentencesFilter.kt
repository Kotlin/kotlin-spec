package org.jetbrains.kotlin.spec

import ru.spbstu.pandoc.*

private const val SENTENCE_CLASS = "sentence"
private const val PARAGRAPH_CLASS = "paragraph"

private fun <T> Iterable<T>.breakBy(f : (T) -> Boolean): Pair<MutableList<T>, MutableList<T>> {
    val it = iterator()
    val before: MutableList<T> = mutableListOf()
    val after: MutableList<T> = mutableListOf()
    for(t in it) {
        if(f(t)) {
            after += t
            break
        }
        before += t
    }
    after.addAll(it.asSequence())
    return before to after
}

private fun <T> MutableList<T>.unconsed() = first() to subList(1, lastIndex + 1)

private val stopList = setOf(
        "e.g.", "i.e.", "w.r.t.", "ca.",
        "cca.", "etc.", "f.", "ff.",
        "i.a.", "Ph.D.", "Q.E.D.", "vs."
) // et.al. is commonly at the end of sentence => not here

private fun breakSentence(inlines: List<Inline>): Pair<List<Inline>, List<Inline>> {
    fun isEndLine(i: Inline): Boolean = when {
        i is Inline.Str
                && (i.text.endsWith(".") || i.text.endsWith("?") || i.text.endsWith("!"))
                && i.text !in stopList ->
                true
        i is Inline.LineBreak -> true
        else -> false
    }

    val (ac, bc) = inlines.breakBy(::isEndLine)
    if(bc.isEmpty()) return ac to emptyList()
    if(bc.size == 1) return inlines to emptyList()

    val (h, t_) = bc.unconsed()
    val (h2, tail) = t_.unconsed()

    when {
        h2 is Inline.Space
                || h2 is Inline.SoftBreak
                || (h2 is Inline.Span && SENTENCE_CLASS in h2.attr.classes)
                || (h == Inline.Str(".")) && h2 is Inline.Str && h2.text.startsWith(")") -> {
            ac += h
            ac += h2
            return ac to tail
        }
        (h is Inline.Str) && h.text.startsWith(".)")
                || (h is Inline.LineBreak) && h2 is Inline.Str && h2.text.startsWith(".") -> {
            ac += h
            tail.add(0, h2)
            return ac to tail
        }
        else -> {
            tail.add(0, h2)
            val (dc, ec) = breakSentence(tail)
            return (ac + h + dc) to ec
        }
    }
}

private fun splitSentences(inlines: List<Inline>): MutableList<List<Inline>> {
    if(inlines.isEmpty()) return mutableListOf()

    var (sent, rest) = breakSentence(inlines)
    val res = mutableListOf(sent)
    while(rest.isNotEmpty()) {
        val (sent_, rest_) = breakSentence(rest)
        rest = rest_
        res += sent_
    }

    return res
}

private fun process(inlines: List<Inline>): List<Inline> =
        splitSentences(inlines).map { Inline.Span(Attr(classes = listOf(SENTENCE_CLASS)), it) }

private object SpecSentencesFilterVisitor : PandocVisitor() {
    override fun visit(b: Block.Para): Block {
        return Block.Div(
                Attr(classes = listOf(PARAGRAPH_CLASS)),
                listOf(b.copy(inlines = process(b.inlines)))
        )
    }

    override fun visit(b: Block.Plain): Block {
        return b.copy(inlines = process(b.inlines))
    }

    override fun visit(b: Block.Div): Block {
        if(PARAGRAPH_CLASS in b.attr.classes) return b;
        return super.visit(b)
    }

    override fun visit(i: Inline.Span): Inline {
        if(SENTENCE_CLASS in i.attr.classes) return i
        return super.visit(i)
    }
}

fun main() = makeFilter(SpecSentencesFilterVisitor)
