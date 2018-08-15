#!/bin/env kscript

@file:MavenRepository("bintray-vorpal-research-kotlin-maven","https://dl.bintray.com/vorpal-research/kotlin-maven" )
@file:DependsOn("ru.spbstu:kotlin-pandoc:0.0.5")

import ru.spbstu.pandoc.*

class Visitor(val format: String) : PandocVisitor() {
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

makeFilter(Visitor(args[0]))
