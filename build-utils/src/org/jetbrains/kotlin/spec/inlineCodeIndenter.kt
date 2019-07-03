package org.jetbrains.kotlin.spec

import ru.spbstu.pandoc.Inline
import ru.spbstu.pandoc.PandocVisitor
import ru.spbstu.pandoc.makeFilter

private object InlineCodeIndenter : PandocVisitor() {
    const val INDENT = "indent"
    override fun visit(i: Inline.Code): Inline {
        val props = i.attr.propertiesMap()
        val indentation = props[INDENT]?.toIntOrNull()
                ?: i.attr.classes.find { it == INDENT }?.let { 1 }
                ?: return i

        return i.copy(
                text = i.text.prependIndent("    ".repeat(indentation)),
                attr = i.attr.copy(
                        properties = i.attr.properties.filter { (k, _) -> k != INDENT },
                        classes = i.attr.classes.filter { it != INDENT }
                )
        )
    }
}

fun main() = makeFilter(InlineCodeIndenter)
