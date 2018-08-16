#!/bin/env kscript

@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("ru.spbstu:kotlin-pandoc:0.0.5")

import ru.spbstu.pandoc.*

fun String.splitAt(index: Int) = substring(0, minOf(index, length)) to substring(minOf(index, length))

fun String.indexOfOccurence(sub: String, occ: Int): Int {
    var occ_ = occ
    var current = indexOf(sub)
    while(occ_ > 0 && current != -1) {
        current = indexOf(sub, startIndex = minOf(lastIndex, current + 1))
        --occ_
    }
    return current
}

class Visitor(val format: String) : PandocVisitor() {

    fun makeInlineTODO(contents: List<Inline>): Inline = when(format) {
        "latex", "beamer" -> {
            Inline.Span(
                    Attr(classes = listOf("TODO")),
                    listOf(Inline.RawInline(Format(format), """\todo{""")) +
                            contents +
                            Inline.RawInline(Format(format), "}")
            )
        }
        else -> Inline.Span(Attr(), listOf(
            Inline.Span(Attr(classes = listOf("TODO")), contents),
            Inline.Span(Attr(classes = listOf("TODO-marker")), listOf(Inline.Str("*")))
        ))
    }

    fun makeBlockTODO(contents: Block): Block = when(format) {
        "html" -> Block.Div(Attr(classes = listOf("TODO")), listOf(contents))
        "latex", "beamer" ->
            Block.Div(Attr(classes = listOf("TODO")),
                    listOf(
                            Block.RawBlock(Format(format), """\todo[inline]{"""),
                            contents,
                            Block.RawBlock(Format(format), """}""")
                    )
            )
        else -> contents
    }

    override fun visit(iis: List<Inline>, @Suppress("UNUSED") token: Inline?): List<Inline> {
        val res: MutableList<Inline> = mutableListOf()
        val transformed = super.visit(iis, token)
        val it = transformed.iterator()
        for(e in it) {
            when {
                e is Inline.Str && e.text.contains("(TODO") -> {
                    val (e1, e2) = e.text.splitAt(e.text.indexOf("(TODO"))
                    if(e1.isNotEmpty()) res.add(Inline.Str(e1))
                    val interm =
                            mutableListOf<Inline>()
                    var tail: Inline? = null
                    var levels = 0
                    val app = sequenceOf(Inline.Str(e2)) + it.asSequence()
                    outer@ for(e in app)  {
                        if(e is Inline.Str) {
                            for((i, ch) in e.text.withIndex()) {
                                when(ch) {
                                    '(' -> ++levels
                                    ')' -> if(levels <= 1){
                                        val (h, t) = e.text.splitAt(i + 1)
                                        interm.add(Inline.Str(h))
                                        if (t.isNotEmpty()) tail = Inline.Str(t)
                                        break@outer
                                    } else --levels
                                }
                            }
                        }
                        interm.add(e)
                    }
                    res.add(makeInlineTODO(interm))
                    if(tail != null) res.add(tail)
                }
                else -> res.add(e)
            }
        }
        return res
    }

    override fun visit(b: Block.Para): Block {
        val sup = super.visit(b)
        val first = b.inlines.firstOrNull()
        if(first is Inline.Str && first.text.startsWith("TODO")) {
            return makeBlockTODO(sup)
        }
        return sup
    }

    override fun visit(b: Block.Plain): Block {
        val sup = super.visit(b)
        val first = b.inlines.firstOrNull()
        if(first is Inline.Str && first.text.startsWith("TODO")) {
            return makeBlockTODO(sup)
        }
        return sup
    }
}

makeFilter(Visitor(args[0]))
