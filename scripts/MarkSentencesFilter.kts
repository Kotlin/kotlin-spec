#!/bin/env kscript

@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("ru.spbstu:kotlin-pandoc:0.0.4")

import ru.spbstu.pandoc.*
import ru.spbstu.pandoc.builder.*

val sentenceClass = "sentence"
val paragraphClass = "paragraph"

fun <T> Iterable<T>.breakBy(f : (T) -> Boolean): Pair<MutableList<T>, MutableList<T>> {
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

fun <T> MutableList<T>.unconsed() = first() to subList(1, lastIndex + 1)

val stopList = setOf(
        "e.g.", "i.e.", "w.r.t.", "ca.",
        "cca.", "etc.", "f.", "ff.",
        "i.a.", "Ph.D.", "Q.E.D.", "vs."
) // et.al. is commonly at the end of sentence => not here

fun breakSentence(inlines: List<Inline>): Pair<List<Inline>, List<Inline>> {
    fun isEndLine(i: Inline): Boolean = when {
        i is Inline.Str
                && (i.text.endsWith(".") || i.text.endsWith("?") || i.text.endsWith("!"))
                && !(i.text in stopList) ->
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
                || (h2 is Inline.Span && sentenceClass in h2.attr.classes)
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

fun splitSentences(inlines: List<Inline>): MutableList<List<Inline>> {
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

fun process(inlines: List<Inline>): List<Inline> =
        splitSentences(inlines).map { Inline.Span(Attr(classes = listOf(sentenceClass)), it) }

makeFilter(object : PandocVisitor() {
    override fun visit(b: Block.Para): Block {
        return Block.Div(
                Attr(classes = listOf(paragraphClass)),
                listOf(b.copy(inlines = process(b.inlines)))
        )
    }

    override fun visit(b: Block.Plain): Block {
        return b.copy(inlines = process(b.inlines))
    }

    override fun visit(b: Block.Div): Block {
        if(paragraphClass in b.attr.classes) return b;
        return super.visit(b)
    }

    override fun visit(i: Inline.Span): Inline {
        if(sentenceClass in i.attr.classes) return i
        return super.visit(i)
    }
})
