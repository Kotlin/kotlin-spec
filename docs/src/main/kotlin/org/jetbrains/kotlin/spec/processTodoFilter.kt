package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import ru.spbstu.pandoc.*

private fun String.splitAt(index: Int) = substring(0, minOf(index, length)) to substring(minOf(index, length))

class SpecTodoFilterVisitor(val format: Format, val disableTODOS: Boolean) : PandocVisitor() {
    fun makeInlineTODO(contents: List<Inline>): Inline? = when {
        disableTODOS -> null
        format.isLaTeX() -> {
            Inline.Span(
                    Attr(classes = listOf("TODO")),
                    listOf(Inline.RawInline(Format.TeX, """\todo{""")) +
                            contents +
                            Inline.RawInline(Format.TeX, "}")
            )
        }
        else -> Inline.Span(Attr(), listOf(
                Inline.Span(Attr(classes = listOf("TODO")), contents),
                Inline.Span(Attr(classes = listOf("TODO-marker")), listOf(Inline.Str("*")))
        ))
    }

    fun makeBlockTODO(contents: Block): Block = when {
        disableTODOS -> Block.Null
        format.isHTML() -> Block.Div(Attr(classes = listOf("TODO")), listOf(contents))
        format.isLaTeX() ->
            Block.Div(Attr(classes = listOf("TODO")),
                    listOf(
                            Block.RawBlock(Format.TeX, """\todo[inline]{"""),
                            contents,
                            Block.RawBlock(Format.TeX, """}""")
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
                    outer@ for(e3 in app)  {
                        if(e3 is Inline.Str) {
                            for((i, ch) in e3.text.withIndex()) {
                                when(ch) {
                                    '(' -> ++levels
                                    ')' -> if(levels <= 1){
                                        val (h, t) = e3.text.splitAt(i + 1)
                                        interm.add(Inline.Str(h))
                                        if (t.isNotEmpty()) tail = Inline.Str(t)
                                        break@outer
                                    } else --levels
                                }
                            }
                        }
                        interm.add(e3)
                    }
                    makeInlineTODO(interm)?.let { res.add(it) }
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
            if(disableTODOS) return Block.Null
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

object SpecTodoFilter : CliktCommand() {
    val format: Format by argument("Pandoc output format").convert { Format(it) }
    val disableTODOS: Boolean by option().flag("--enable-todos", default = false)

    override fun run() {
        makeFilter(SpecTodoFilterVisitor(format, disableTODOS))
    }
}

fun main(args: Array<String>) = SpecTodoFilter.main(args)
