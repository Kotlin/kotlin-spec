package org.jetbrains.kotlin.spec.grammar.parsetree

import com.intellij.openapi.util.io.FileUtil
import java.io.File
import java.util.*

data class ComparisonTree(
        val text: String,
        val children: MutableList<ComparisonTree> = mutableListOf()
) {

    fun dump(indent: Int = 0, sb: Appendable = StringBuilder()) {
        sb.append(" ".repeat(indent)).appendln(text)
        for (child in children) child.dump(indent + 2, sb)
    }
    fun dumpChildren(sb: Appendable = StringBuilder()) {
        for (child in children) child.dump(0, sb)
    }

    fun firstDifference(that: ComparisonTree): Pair<ComparisonTree, ComparisonTree>? {
        if (text != that.text || children.size != that.children.size) return this to that
        children.zip(that.children) { here, there ->
            val diff = here.firstDifference(there)
            if (diff != null) return diff
        }
        return null
    }

    fun allDifferences(that: ComparisonTree, collector: MutableList<Pair<ComparisonTree, ComparisonTree>> = mutableListOf()): List<Pair<ComparisonTree, ComparisonTree>> {
        if (text != that.text || children.size != that.children.size) collector.add(this to that)
        else children.zip(that.children) { here, there ->
            here.allDifferences(there, collector)
        }
        return collector
    }

    companion object {
        fun parse(s: List<String>): ComparisonTree {
            data class StackElement(val tree: ComparisonTree, val depth: Int)

            val root = ComparisonTree("<root>")
            val stack = Stack<StackElement>()
            stack.push(StackElement(root, -1))
            for (line in s) {
                val depth = line.takeWhile { it.isWhitespace() }.length
                while(depth <= stack.peek().depth) {
                    stack.pop()
                }
                val newTree = ComparisonTree(line.trim())
                stack.peek().tree.children.add(newTree)
                stack.push(StackElement(newTree, depth))
            }

            return root
        }

        fun parse(f: File) = parse(f.readLines())
    }
}

fun isDiffCompatible_28_1_21(left: ComparisonTree, right: ComparisonTree): Boolean {
    if (left.text == "postfixUnaryExpression" &&
            left.children.firstOrNull()?.children?.firstOrNull()?.text == "callableReference")
        return true
    if (left.text == "genericCallLikeComparison" &&
            left.children.getOrNull(1)?.text == "callSuffix")
        return true
    if (left.text == "parameterWithOptionalType" && right.text == "functionValueParameterWithOptionalType")
        return true
    return false
}

fun regression_28_1_21() {


    File("/home/belyaev/kotlin-spec/grammar/testData")
            .walkTopDown()
            .filter { it.name.endsWith(".actual") }
            .forEach { file ->
                println("Processing: $file")
                val original = File(file.parent, file.name.removeSuffix(".actual"))
                check(original.exists())
                println("Original: $original")
                val source = File(file.parent, original.name.removeSuffix(".antlrtree.txt") + ".kt")
                println("Source: $source")

                val ct1 = ComparisonTree.parse(original)
                val ct2 = ComparisonTree.parse(file)

                val diffs = ct1.allDifferences(ct2)
                print("Diffs compatible?")
                val diffsCompatible = diffs.all { (l, r) -> isDiffCompatible_28_1_21(l, r) }
                println(diffsCompatible)

                if (diffsCompatible) {
                    FileUtil.writeToFile(original, file.readBytes())
                }

            }
}

fun isDiffCompatible_22_3_21(left: ComparisonTree, right: ComparisonTree): Boolean {
    if (left.text == """Identifier("value")""" && right.text == """VALUE("value")""") return true
    return false
}

fun main() {
    File("/home/belyaev/kotlin-spec/grammar/testData")
        .walkTopDown()
        .filter { it.name.endsWith(".actual") }
        .forEach { file ->
            println("Processing: $file")
            val original = File(file.parent, file.name.removeSuffix(".actual"))
            check(original.exists())
            println("Original: $original")
            val source = File(file.parent, original.name.removeSuffix(".antlrtree.txt") + ".kt")
            println("Source: $source")

            val ct1 = ComparisonTree.parse(original)
            val ct2 = ComparisonTree.parse(file)

            val diffs = ct1.allDifferences(ct2)
            print("Diffs compatible?")
            val diffsCompatible = diffs.all { (l, r) -> isDiffCompatible_22_3_21(l, r) }
            println(diffsCompatible)

            if (diffsCompatible) {
                FileUtil.writeToFile(original, file.readBytes())
            }

        }
}

