package org.jetbrains.kotlin.spec

import ru.spbstu.pandoc.*

object MathInCode : PandocVisitor() {
    override fun visit(b: Block.CodeBlock): Block {
        b.attr.classes.any { it.endsWith("+math") } || return super.visit(b)
        val newAttr = b.attr.copy(classes = b.attr.classes.map { it.replace("+math", "") })
        val codeLines = b.text.lines()
        val res = mutableListOf<Inline>()

        for(line in codeLines) {
            if("$$$" !in line) res += Inline.Code(attr = newAttr, text = line)
            else {
                for(chunk in line.split("$$$").chunked(2)) {
                    res += Inline.Code(attr = newAttr, text = chunk[0])
                    if(chunk.size > 1) {
                        res += Inline.Math(MathType.InlineMath, text = chunk[1].trimEnd().removeSuffix("$$$"))
                    }
                }
            }
            res += Inline.LineBreak
        }

        return Block.Plain(res)
    }
}

fun main() = makeFilter(MathInCode)
