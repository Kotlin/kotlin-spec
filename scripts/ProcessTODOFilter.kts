#!/bin/env kscript

@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("ru.spbstu:kotlin-pandoc:0.0.5")

import ru.spbstu.pandoc.*

class Visitor(val format: String) : PandocVisitor() {
    override fun visit(iis: List<Inline>, @Suppress("UNUSED") token: Inline?): List<Inline> {
        val res: MutableList<Inline> = mutableListOf()
        val transformed = super.visit(iis, token)
        val it = transformed.iterator()
        for(e in it) {
            when {
                e is Inline.Str && e.text.contains("(TODO") -> {
                    val interm =
                            mutableListOf<Inline>(Inline.Str(e.text.substring(e.text.indexOf("(TODO"))))
                    var tail: Inline? = null
                    var levels = 0
                    loop@ for(e in it)  {
                        when {
                            e is Inline.Str && e.text.contains("(") ->
                                ++levels
                            e is Inline.Str && e.text.contains(")") ->
                                if(levels == 0) {
                                    val index = e.text.indexOf(")")
                                    interm.add(Inline.Str(e.text.substring(0, index + 1)))
                                    tail = if(index != e.text.lastIndex) Inline.Str(e.text.substring(index + 1)) else null
                                    break@loop
                                } else --levels
                        }
                        interm.add(e)
                    }
                    res.add(Inline.Span(Attr(classes = listOf("TODO")), interm))
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
            return when(format) {
                "html" -> Block.Div(Attr(classes = listOf("TODO")), listOf(sup))
                "latex", "beamer" ->
                    Block.Div(Attr(classes = listOf("TODO")),
                            listOf(
                                    Block.RawBlock(Format(format), """\todo[inline]{"""),
                                    sup,
                                    Block.RawBlock(Format(format), """}""")
                            )
                    )
                else -> sup
            }
        }
        return sup
    }

    override fun visit(b: Block.Plain): Block {
        val sup = super.visit(b)
        val first = b.inlines.firstOrNull()
        if(first is Inline.Str && first.text.startsWith("TODO")) {
            return when(format) {
                "html" -> Block.Div(Attr(classes = listOf("TODO")), listOf(sup))
                "latex", "beamer" ->
                    Block.Div(Attr(classes = listOf("TODO")),
                            listOf(
                                    Block.RawBlock(Format(format), """\todo[inline]{"""),
                                    sup,
                                    Block.RawBlock(Format(format), """}""")
                            )
                    )
                else -> sup
            }
        }
        return sup
    }
}

interface CC {
    companion object CCC {

    }
}

makeFilter(Visitor(args[0]))
