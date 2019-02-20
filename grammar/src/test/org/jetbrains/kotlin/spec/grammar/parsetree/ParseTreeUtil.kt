package org.jetbrains.kotlin.spec.grammar.parsetree

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.PredictionContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import org.jetbrains.kotlin.spec.grammar.KotlinLexer
import org.jetbrains.kotlin.spec.grammar.KotlinParser
import org.jetbrains.kotlin.spec.grammar.util.TestUtil
import java.util.HashMap


data class SyntaxError(val message: String?, val line: Int, val charPosition: Int) {
    override fun toString() =  "Line $line:$charPosition $message"
}

class KotlinParserWithLimitedCache(input: TokenStream): KotlinParser(input) {
    companion object {
        private const val MAX_CACHE_SIZE = 10000
        private val cacheField = _sharedContextCache.javaClass.declaredFields.find { it.name == "cache" }!!.apply { isAccessible = true }
        private val cache = cacheField.get(_sharedContextCache) as HashMap<PredictionContext, PredictionContext>
    }

    init {
        if (_sharedContextCache.size() > MAX_CACHE_SIZE) {
            cache.clear()
            reset()
            interpreter.clearDFA()
        }
    }
}

class KotlinLexerWithLimitedCache(input: CharStream): KotlinLexer(input) {
    companion object {
        private const val MAX_CACHE_SIZE = 10000
        private val cacheField = _sharedContextCache.javaClass.declaredFields.find { it.name == "cache" }!!.apply { isAccessible = true }
        private val cache = cacheField.get(_sharedContextCache) as HashMap<PredictionContext, PredictionContext>
    }

    init {
        if (_sharedContextCache.size() > MAX_CACHE_SIZE) {
            cache.clear()
            reset()
            interpreter.clearDFA()
        }
    }
}

object ParseTreeUtil {
    private const val ROOT_RULE = "kotlinFile"

    private fun buildTree(
            parser: KotlinParser,
            tokenTypeMap: Map<String, Int>,
            antlrParseTree: ParseTree,
            kotlinParseTree: KotlinParseTree
    ): KotlinParseTree {
        for (i in 0..antlrParseTree.childCount) {
            val antlrParseTreeNode = antlrParseTree.getChild(i) ?: continue
            val kotlinParseTreeNode = when (antlrParseTreeNode) {
                is TerminalNodeImpl ->
                    KotlinParseTree(
                            KotlinParseTreeNodeType.TERMINAL,
                            KotlinLexer.VOCABULARY.getSymbolicName(antlrParseTreeNode.symbol.type),
                            antlrParseTreeNode.symbol.text.replace(TestUtil.ls, "\\n")
                    )
                else ->
                    KotlinParseTree(
                            KotlinParseTreeNodeType.RULE,
                            parser.ruleNames[(antlrParseTreeNode as RuleContext).ruleIndex]
                    )
            }

            kotlinParseTree.children.add(kotlinParseTreeNode)

            buildTree(parser, tokenTypeMap, antlrParseTreeNode, kotlinParseTreeNode)
        }

        return kotlinParseTree
    }

    private fun getErrorListener(errors: MutableList<SyntaxError>) =
            object : BaseErrorListener() {
                override fun syntaxError(
                        recognizer: Recognizer<*, *>?,
                        offendingSymbol: Any?,
                        line: Int,
                        charPositionInLine: Int,
                        msg: String?,
                        e: RecognitionException?
                ) {
                    errors.add(SyntaxError(msg, line, charPositionInLine))
                }
            }

    fun parse(sourceCode: String): Pair<KotlinParseTree, Pair<List<SyntaxError>, List<SyntaxError>>> {
        val lexerErrors = mutableListOf<SyntaxError>()
        val parserErrors = mutableListOf<SyntaxError>()
        val lexer = KotlinLexerWithLimitedCache(ANTLRInputStream(sourceCode))
        val tokens = CommonTokenStream(
                lexer.apply {
                    removeErrorListeners()
                    addErrorListener(getErrorListener(lexerErrors))
                }
        )
        val kotlinParser = KotlinParserWithLimitedCache(tokens)
        val parseTree = kotlinParser.apply {
            removeErrorListeners()
            addErrorListener(getErrorListener(parserErrors))
        }.kotlinFile() as ParseTree
        val kotlinParseTree = buildTree(
                kotlinParser,
                lexer.tokenTypeMap,
                parseTree,
                KotlinParseTree(
                        KotlinParseTreeNodeType.RULE,
                        kotlinParser.ruleNames[kotlinParser.ruleIndexMap[ROOT_RULE]!!]
                )
        )

        return Pair(kotlinParseTree, Pair(lexerErrors, parserErrors))
    }
}
