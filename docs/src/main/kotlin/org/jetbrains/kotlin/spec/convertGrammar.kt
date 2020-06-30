package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.antlr.v4.Tool
import org.antlr.v4.parse.ANTLRParser
import org.antlr.v4.tool.Grammar
import org.antlr.v4.tool.LexerGrammar
import org.antlr.v4.tool.ast.*
import ru.spbstu.grammarConverter.parseRules
import ru.spbstu.grammarConverter.toMarkdown
import java.io.File
import java.util.*

fun inlineGrammar(grammarDir: String, lexerGrammar: String, parserGrammar: String): String {
    val tool = Tool().apply { libDirectory = grammarDir }
    val lexerGrammarText = File("$grammarDir/$lexerGrammar").readText()
    val parserGrammarSourceLines = File("$grammarDir/$parserGrammar").readLines()

    val lexer = LexerGrammar(tool, tool.parseGrammarFromString(lexerGrammarText)).apply {
        fileName = lexerGrammar
        tool.process(this, false)
    }
    val parser = Grammar(tool, tool.parseGrammarFromString(parserGrammarSourceLines.joinToString(System.lineSeparator()))).apply {
        fileName = parserGrammar
        tool.process(this, false)
    }

    val visitor = GrammarVisitor(lexer, parserGrammarSourceLines).also { parser.ast.visit(it) }

    return visitor.processedParserGrammarSourceLines.joinToString(System.lineSeparator())
}

private fun convertGrammar(outputMarkdownFilePath: String, grammarDir: String, lexerGrammar: String, parserGrammar: String) {
    val parserGrammarWithInlinedLexerRules = inlineGrammar(grammarDir, lexerGrammar, parserGrammar)
    val outputMarkdownFile = File(outputMarkdownFilePath).outputStream()

    outputMarkdownFile.bufferedWriter().use { writer ->
        val rules = parseRules(parserGrammarWithInlinedLexerRules.byteInputStream())
        writer.append(rules.toMarkdown(true)).appendln()
    }
}

private object Driver : CliktCommand() {
    val outputFilePath by option("-o", "--output", help="path to output file in markdown format (.md)").required()
    val grammarDir by option("-d", "--grammar_dir", help="path to dir with lexer and parser grammars").required()
    val lexerGrammar by option("-l", "--lexer_grammar", help="lexer grammar filename").required()
    val parserGrammar by option("-p", "--parser_grammar", help="parser grammar filename").required()

    override fun run() = convertGrammar(outputFilePath, grammarDir, lexerGrammar, parserGrammar)
}

class GrammarVisitor(private val lexer: LexerGrammar, parserGrammarSourceLines: List<String>): GrammarASTVisitor {
    val processedParserGrammarSourceLines: MutableList<String> = parserGrammarSourceLines.toMutableList()
    private val offsets = mutableMapOf<Int, TreeMap<Int, Int>>()

    override fun visit(node: GrammarAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: GrammarRootAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: RuleAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: BlockAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: OptionalBlockAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: PlusBlockAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: StarBlockAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: AltAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: NotAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: PredAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: RangeAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: SetAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }
    override fun visit(node: RuleRefAST) { if (node.children != null) { node.childrenAsArray.map { it.visit(this) } } }

    override fun visit(node: TerminalAST) {
        val lexerRule = lexer.rules[node.token.text]

        if (lexerRule == null || !isSimpleLexerRule(lexerRule.ast.childrenAsArray[1]) || node.token.text in excludedNodes)
            return

        val tokenValue = lexerRule.tokenRefs.singleOrNull() ?: return

        val tokenNameLength = node.token.text.length
        val startPositionInLine = node.charPositionInLine
        val endPositionInLine = startPositionInLine + tokenNameLength
        val lineIndex = node.line - 1

        val offsetForLine = offsets[lineIndex]
        val offset = offsetForLine?.subMap(0, startPositionInLine)?.values?.sum() ?: 0

        processedParserGrammarSourceLines[lineIndex] = processedParserGrammarSourceLines[lineIndex].replaceRange(
                range = (startPositionInLine - offset) until (endPositionInLine - offset),
                replacement = tokenValue
        )
        if (lineIndex !in offsets) {
            offsets[lineIndex] = TreeMap()
        }

        val offsetsTree = offsets[lineIndex]

        if (offsetsTree != null) {
            offsetsTree[startPositionInLine] = node.token.text.length - tokenValue.length
        }
    }

    private fun isSimpleLexerRule(rule: GrammarAST): Boolean {
        if (rule.children != null && rule.childrenAsArray.count { supportedNodes.contains(it.type) } > 1)
            return false

        if (rule.children == null || rule.children.size == 0)
            return rule.type == ANTLRParser.STRING_LITERAL

        return isSimpleLexerRule(rule.childrenAsArray[0])
    }

    companion object {
        private val supportedNodes = setOf(
                ANTLRParser.TOKEN_REF, ANTLRParser.LEXER_CHAR_SET, ANTLRParser.RULE_REF, ANTLRParser.BLOCK,
                ANTLRParser.ALT, ANTLRParser.SET, ANTLRParser.RULE, ANTLRParser.STRING_LITERAL, ANTLRParser.RULEMODIFIERS,
                ANTLRParser.POSITIVE_CLOSURE, ANTLRParser.CLOSURE, ANTLRParser.OPTIONAL
        )

        private val excludedNodes = setOf(
                "AT_NO_WS", "AT_PRE_WS", "AT_POST_WS", "QUEST_NO_WS", "QUEST_WS", "NL", "EOF"
        )
    }
}

fun main(args: Array<String>) = Driver.main(args)
