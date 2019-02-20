package org.jetbrains.kotlin.spec.grammar.psi

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

enum class NodeClass {
    TOKEN, RULE, ERROR, OTHER, WHITESPACE
}

data class PsiNode(
        val `class`: NodeClass,
        val type: String? = null,
        val text: String? = null,
        val children: MutableList<PsiNode> = mutableListOf()
)

typealias Position = Pair<Int, Int>
typealias ErrorElements = MutableList<Pair<String, Position>>

object PsiTextParser {
    private const val WHITESPACES_NUMBER_OFFSET = 2
    private val rulePattern = Pattern.compile("""^(?<offset>\s*)(?<type>[A-Z_]+)$""")
    private val tokenPattern = Pattern.compile("""^(?<offset>\s*)(?:PsiElement)\((?<type>[A-z_]+)\)\('(?<text>.*?)'\)$""")
    private val errorElementPattern = Pattern.compile("""^(?<offset>\s*)(?:PsiErrorElement):(?<description>.*?)$""")
    private val whitespaceElementPattern = Pattern.compile("""^(?<offset>\s*)(?:PsiWhiteSpace)\('(?<content>.*?)'\)$""")
    private val otherElementPattern = Pattern.compile("""^(?<offset>\s*)(?<content>.*?)$""")

    fun hasErrorElement(psi: PsiNode): Boolean =
        psi.children.any { it.`class` == NodeClass.ERROR || hasErrorElement(it) }

    fun getErrorElements(
            psi: PsiNode,
            codeBuffer: StringBuffer = StringBuffer(),
            errorElementsText: ErrorElements = mutableListOf()
    ): ErrorElements {
        psi.children.forEach {
            if (it.`class` == NodeClass.ERROR) {
                val codeLines = codeBuffer.lines()
                errorElementsText.add(
                    Pair(it.text!!, Position(codeLines.size, codeLines.last().length + 1))
                )
            }
            if (it.text != null && (it.`class` == NodeClass.TOKEN || it.`class` == NodeClass.WHITESPACE)) {
                codeBuffer.append(
                    if (it.`class` == NodeClass.WHITESPACE) it.text.replace("\\n", System.lineSeparator()) else it.text
                )
            }
            getErrorElements(it, codeBuffer, errorElementsText)
        }

        return errorElementsText
    }

    private fun parse(psiTextAsLines: List<String>): PsiNode {
        val root = PsiNode(NodeClass.RULE, "kotlinFile")
        val nodes = Stack<PsiNode>().apply { push(root) }
        var prevNode = root
        var matcher: Matcher? = null
        val checkStack: (currentDeepLevel: Int) -> Unit = { currentDeepLevel: Int ->
            when {
                currentDeepLevel > nodes.size -> nodes.push(prevNode)
                currentDeepLevel < nodes.size -> nodes.pop()
            }
        }
        val addNode = { offset: String, `class`: NodeClass, type: String?, text: String? ->
            val currentDeepLevel = offset.split(" ".repeat(WHITESPACES_NUMBER_OFFSET)).size

            checkStack(currentDeepLevel)

            PsiNode(`class`, type, text).also {
                nodes.peek().children.add(it)
                prevNode = it
            }
        }
        val matcherFind = { line: String, pattern: Pattern ->
            matcher = pattern.matcher(line)
            matcher?.find() ?: false
        }

        psiTextAsLines.forEach { line ->
            when {
                matcherFind(line, rulePattern) ->
                    addNode(matcher!!.group("offset"), NodeClass.RULE, matcher!!.group("type"), null)
                matcherFind(line, tokenPattern) ->
                    addNode(matcher!!.group("offset"), NodeClass.TOKEN, matcher!!.group("type"), matcher!!.group("text"))
                matcherFind(line, errorElementPattern) ->
                    addNode(matcher!!.group("offset"), NodeClass.ERROR, null, matcher!!.group("description"))
                matcherFind(line, whitespaceElementPattern) ->
                    addNode(matcher!!.group("offset"), NodeClass.WHITESPACE, null, matcher!!.group("content"))
                matcherFind(line, otherElementPattern) ->
                    addNode(matcher!!.group("offset"), NodeClass.OTHER, null, matcher!!.group("content"))
            }
        }

        return root
    }

    fun parse(psiText: String) = parse(psiText.split(System.lineSeparator()).run { subList(1, lastIndex) })
}
