package org.jetbrains.kotlin.spec.grammar

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import org.jetbrains.kotlin.spec.grammar.parsetree.ParseTreeUtil
import org.jetbrains.kotlin.spec.grammar.psi.PsiTextParser
import org.jetbrains.kotlin.spec.grammar.util.DiagnosticTestData
import org.jetbrains.kotlin.spec.grammar.util.PsiTestData
import org.jetbrains.kotlin.spec.grammar.util.TestDataFileHeader
import org.jetbrains.kotlin.spec.grammar.util.TestUtil
import org.jetbrains.kotlin.spec.grammar.util.TestUtil.assertEqualsToFile
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import java.util.regex.Pattern

@RunWith(Parameterized::class)
class TestRunner {
    @Parameterized.Parameter
    lateinit var filename: String

    companion object {
        private const val ERROR_EXPECTED_MARKER = "WITH_ERRORS"
        private const val MUTE_MARKER = "MUTE"

        private val antlrTreeFileHeaderPattern =
                Pattern.compile("""^File: .*?.kts? - (?<hash>[0-9a-f]{32})(?<markers> \((?<marker>$ERROR_EXPECTED_MARKER|$MUTE_MARKER)\))?$""")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun getTestFiles() = File(TestUtil.TESTS_DIR).let { testsDir ->
            testsDir.walkTopDown().filter { it.extension == "kt" }.map { arrayOf(it.relativeTo(testsDir).path) }.toList()
        }
    }

    @Test
    fun doTest() {
        val testFile = File(filename)
        val testData = TestUtil.getTestData(testFile)
        val header = if (testData.antlrParseTreeText.exists())
                antlrTreeFileHeaderPattern.matcher(testData.antlrParseTreeText.readText().lines().first()).run {
                    if (!find())
                        return@run null

                    val marker = group("marker")
                    val hash = group("hash")

                    TestDataFileHeader(marker, hash)
                }
            else null

        if (header != null)
            assumeFalse(header.marker == MUTE_MARKER || header.hash != testData.sourceCodeHash)

        val (parseTree, errors) = ParseTreeUtil.parse(testData.sourceCode)
        val (lexerErrors, parserErrors) = errors
        val errorExpected = header?.marker == ERROR_EXPECTED_MARKER
        val dumpParseTree = parseTree.stringifyTree("File: ${testFile.name} - ${testData.sourceCodeHash}" + (if (errorExpected) " ($ERROR_EXPECTED_MARKER)" else ""))

        assertEqualsToFile("Expected and actual ANTLR parsetree are not equals.", testData.antlrParseTreeText, dumpParseTree)

        val lexerHasErrors = lexerErrors.isNotEmpty()
        val parserHasErrors = parserErrors.isNotEmpty()

        println("HAS ANTLR LEXER ERRORS: ${if (lexerHasErrors) "YES" else "NO"}")
        lexerErrors.forEach { println("    - $it") }
        println("HAS ANTLR PARSER ERRORS: ${if (parserHasErrors) "YES" else "NO"}")
        parserErrors.forEach { println("    - $it") }

        when (testData) {
            is PsiTestData -> {
                val psi = PsiTextParser.parse(testData.psiParseTreeText)
                val psiErrorElements = PsiTextParser.getErrorElements(psi)
                val psiHasErrorElements = psiErrorElements.isNotEmpty()
                val verdictsEquals = (lexerHasErrors || parserHasErrors) == psiHasErrorElements

                println("HAS PSI ERROR ELEMENTS: ${if (psiHasErrorElements) "YES" else "NO"}")
                psiErrorElements.forEach { println("    - Line ${it.second.first}:${it.second.second} ${it.first}") }
                assertTrue((verdictsEquals && !errorExpected) || (errorExpected && !psiHasErrorElements))
            }
            is DiagnosticTestData -> {
                val hasAnyErrors = lexerHasErrors || parserHasErrors
                assertTrue((!hasAnyErrors && !errorExpected) || errorExpected && hasAnyErrors)
            }
        }
    }
}
