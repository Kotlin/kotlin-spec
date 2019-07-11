package org.jetbrains.kotlin.spec

import ru.spbstu.pandoc.*

// all the environments
// - supported by mathJax
// - illegal inside $$...$$ in LaTeX
private val environments = listOf(
        "align",
        "align*",
        "alignat",
        "alignat*",
        "eqnarray",
        "eqnarray*",
        "equation",
        "equation*",
        "gather",
        "gather*",
        "multline",
        "multline*",
        "split"
// maybe there are others?
).map { "\\begin{$it}" }

private class MathCleaner(val format: Format): PandocVisitor() {
    override fun visit(i: Inline.Math): Inline = when(i.type) {
        MathType.InlineMath -> i
        MathType.DisplayMath -> {
            if(format.isLaTeX() && environments.any { it in i.text })
                Inline.RawInline(Format.TeX, i.text)
            else i
        }
    }
}

fun main(args: Array<String>) = makeFilter(MathCleaner(Format(args[0])))
