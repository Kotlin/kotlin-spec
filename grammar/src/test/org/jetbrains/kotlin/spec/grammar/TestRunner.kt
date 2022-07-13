package org.jetbrains.kotlin.spec.grammar

import com.intellij.testFramework.TestDataPath
import org.jetbrains.kotlin.spec.grammar.parsetree.ParseTreeUtil
import org.jetbrains.kotlin.spec.grammar.psi.PsiTextParser
import org.jetbrains.kotlin.spec.grammar.util.DiagnosticTestData
import org.jetbrains.kotlin.spec.grammar.util.PsiTestData
import org.jetbrains.kotlin.spec.grammar.util.TestDataFileHeader
import org.jetbrains.kotlin.spec.grammar.util.TestUtil
import org.jetbrains.kotlin.spec.grammar.util.TestUtil.assertEqualsToFile
import org.jetbrains.kotlin.spec.grammar.util.TestUtil.testPathPrefix
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.regex.Pattern

@TestDataPath("\$PROJECT_ROOT/grammar/testData/")
@RunWith(com.intellij.testFramework.Parameterized::class)
class TestRunner {
    @org.junit.runners.Parameterized.Parameter
    lateinit var testFilePath: String

    companion object {
        private const val ERROR_EXPECTED_MARKER = "WITH_ERRORS"
        private const val MUTE_MARKER = "MUTE"
        private const val MUTE_PSI_ERRORS_MARKER = "MUTE_PSI_ERRORS"

        private const val FORCE_APPLY_CHANGES = false
        private const val FAIL_ON_DIFFERENT_HASHES_FOR_SOURCE_CODE = true
        private const val DUMP_ERRONEOUS_DATA = true

        /*
         * It can be used to run specific tests instead of running all ones
         * For instance, `Regex("""secondEmptyCatch\.kt$""")`
         */
        private val testPathFilter: Regex? = System.getProperty("TEST_PATH_FILTER")?.let(::Regex)

        private val antlrTreeFileHeaderPattern =
            Pattern.compile("""^File: .*?.kts? - (?<hash>[0-9a-f]{32})(?<markers> \((?<marker>$ERROR_EXPECTED_MARKER|$MUTE_MARKER|$MUTE_PSI_ERRORS_MARKER)\))?$""")

        @org.junit.runners.Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun getTestFiles() = emptyList<Array<Any>>()

        @Suppress("UNUSED")
        @com.intellij.testFramework.Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun getTestFiles(@Suppress("UNUSED_PARAMETER") klass: Class<*>) = File("./testData").let { testsDir ->
            testsDir.walkTopDown()
                .filter { it.extension == "kt" && (testPathFilter == null || it.path.contains(testPathFilter)) }
                .map { arrayOf(it.relativeTo(testsDir).path.replace("/", "$")) }
                .toList()
        }
    }

    @Test
    fun doTest() {
        val testFile = File(testFilePath.replace("$", "/"))
        val testData = TestUtil.getTestData(testFile)
        val header = if (testData.antlrParseTreeText.exists()) {
            antlrTreeFileHeaderPattern.matcher(
                testData.antlrParseTreeText.readText().lines().first()
            ).run {
                if (!find()) return@run null

                val marker = group("marker")
                val hash = group("hash")

                TestDataFileHeader(marker, hash)
            }
        } else null

        if (header != null) {
            if (!FAIL_ON_DIFFERENT_HASHES_FOR_SOURCE_CODE) {
                assumeFalse(header.hash != testData.sourceCodeHash)
            }
            assumeFalse(header.marker == MUTE_MARKER)
        }

        val (parseTree, errors) = ParseTreeUtil.parse(testData.sourceCode)
        val (lexerErrors, parserErrors) = errors

        val isErrorExpected = header?.marker == ERROR_EXPECTED_MARKER
        val isMutedPsiError = header?.marker == MUTE_PSI_ERRORS_MARKER
        val errorExpectedText = (if (isErrorExpected) " ($ERROR_EXPECTED_MARKER)" else "")
        val mutedPsiErrorText = (if (isMutedPsiError) " ($MUTE_PSI_ERRORS_MARKER)" else "")

        val dumpParseTree = parseTree.stringifyTree(
            "File: ${testFile.name} - ${testData.sourceCodeHash}" + errorExpectedText + mutedPsiErrorText
        )

        assertEqualsToFile(
            "Expected and actual ANTLR parsetree are not equals",
            testData.antlrParseTreeText,
            dumpParseTree,
            FORCE_APPLY_CHANGES || (header != null && header.hash != testData.sourceCodeHash),
            DUMP_ERRONEOUS_DATA
        )

        val lexerHasErrors = lexerErrors.isNotEmpty()
        val parserHasErrors = parserErrors.isNotEmpty()

        println("HAS ANTLR LEXER ERRORS: ${if (lexerHasErrors) "YES" else "NO"}")
        lexerErrors.forEach { println("    - $it") }
        println("HAS ANTLR PARSER ERRORS: ${if (parserHasErrors) "YES" else "NO"}")
        parserErrors.forEach { println("    - $it") }

        fun mkFailedMarkerFile() {
            File("${testFile.testPathPrefix}.failed").createNewFile()
        }

        when (testData) {
            is PsiTestData -> {
                val psi = PsiTextParser.parse(testData.psiParseTreeText)
                val psiErrorElements = PsiTextParser.getErrorElements(psi)
                val psiHasErrorElements = psiErrorElements.isNotEmpty()
                val verdictsEquals = (lexerHasErrors || parserHasErrors) == psiHasErrorElements

                println("HAS PSI ERROR ELEMENTS: ${if (psiHasErrorElements) "YES" else "NO"}")
                psiErrorElements.forEach { println("    - Line ${it.second.first}:${it.second.second} ${it.first}") }

                val isOK = (verdictsEquals && !isErrorExpected) ||
                        (isErrorExpected && !psiHasErrorElements) ||
                        (psiHasErrorElements && isMutedPsiError)
                if (!isOK) mkFailedMarkerFile()

                assertTrue(isOK)
            }
            is DiagnosticTestData -> {
                val hasAnyErrors = lexerHasErrors || parserHasErrors

                val isOK = (!hasAnyErrors && !isErrorExpected) ||
                        (isErrorExpected && hasAnyErrors)
                if (!isOK) mkFailedMarkerFile()

                assertTrue(isOK)
            }
        }
    }
}
