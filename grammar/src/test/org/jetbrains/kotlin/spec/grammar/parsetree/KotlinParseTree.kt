package org.jetbrains.kotlin.spec.grammar.parsetree

import org.jetbrains.kotlin.spec.grammar.util.TestUtil.ls
import java.util.regex.Pattern

enum class KotlinParseTreeNodeType { RULE, TERMINAL }

class KotlinParseTree(
        private val type: KotlinParseTreeNodeType,
        private val name: String,
        private val text: String? = null,
        val children: MutableList<KotlinParseTree> = mutableListOf()
) {
    private fun stringifyTree(builder: StringBuilder, node: KotlinParseTree, depth: Int = 1): StringBuilder =
            builder.apply {
                node.children.forEach { child ->
                    when (child.type) {
                        KotlinParseTreeNodeType.RULE -> append("  ".repeat(depth) + child.name + ls)
                        KotlinParseTreeNodeType.TERMINAL -> append(
                                "  ".repeat(depth) + child.name +
                                "(\"" + child.text!!.replace(ls, Pattern.quote(ls)) + "\")" + ls
                        )
                    }
                    stringifyTree(builder, child, depth + 1)
                }
            }

    fun stringifyTree(root: String) = root + ls + stringifyTree(StringBuilder(), this)
}
