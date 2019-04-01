package org.jetbrains.kotlin.spec.grammar.util

import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.rt.execution.junit.FileComparisonFailure
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Assume.assumeTrue
import java.io.File
import java.nio.file.Files

data class TestDataFileHeader(
        val marker: String?,
        val hash: String
)

sealed class TestData(
        val sourceCode: String,
        val sourceCodeHash: String,
        val antlrParseTreeText: File
)

class PsiTestData(
        sourceCode: String,
        sourceCodeHash: String,
        antlrParseTreeText: File,
        val psiParseTreeText: String
) : TestData(sourceCode, sourceCodeHash, antlrParseTreeText)

class DiagnosticTestData(
        sourceCode: String,
        sourceCodeHash: String,
        antlrParseTreeText: File
) : TestData(sourceCode, sourceCodeHash, antlrParseTreeText)

object TestUtil {
    const val TESTS_DIR = "./testData"

    val ls: String = System.lineSeparator()

    private fun String.trimTrailingWhitespacesAndAddNewlineAtEOF() =
            split(ls).map(String::trimEnd).joinToString(ls).let {
                result -> if (result.endsWith(ls)) result else result + ls
            }

    fun assertEqualsToFile(message: String, expectedFile: File, actual: String, forceApplyChanges: Boolean) {
        val actualText = StringUtil.convertLineSeparators(actual.trim { it <= ' ' }).trimTrailingWhitespacesAndAddNewlineAtEOF()

        if (!expectedFile.exists()) {
            FileUtil.writeToFile(expectedFile, actualText)
            println("Expected data file did not exist. Generating: $expectedFile")
            assumeTrue(false)
        }
        val expected = FileUtil.loadFile(expectedFile, CharsetToolkit.UTF8, true)
        val expectedText = StringUtil.convertLineSeparators(expected.trim { it <= ' ' }).trimTrailingWhitespacesAndAddNewlineAtEOF()

        if (!Comparing.equal(expectedText, actualText)) {
            if (forceApplyChanges) {
                FileUtil.writeToFile(expectedFile, actualText)
                println("Changes are forced applied for $expectedFile")
                assumeTrue(false)
            } else {
                throw FileComparisonFailure(message + ": " + expectedFile.name, expected, actual, expectedFile.absolutePath)
            }
        }
    }

    fun getTestData(testFile: File): TestData {
        val relativePath = "${testFile.parent}/${testFile.nameWithoutExtension}"
        val testPathPrefix = "$TESTS_DIR/$relativePath"
        val sourceCodeFile = File("$testPathPrefix.kt")
        val sourceCode = sourceCodeFile.readText()
        val antlrTreeFile = File("$testPathPrefix.antlrtree.txt")
        val isPsiTest = File("$testPathPrefix.txt").exists()
        val fileContentHash = Files.newInputStream(sourceCodeFile.toPath()).use { DigestUtils.md5Hex(it) }

        println("Source code path: ${sourceCodeFile.absolutePath}")
        println("ANTLR tree path: ${antlrTreeFile.absolutePath}")

        return if (isPsiTest)
            PsiTestData(sourceCode, fileContentHash, antlrTreeFile, File("$testPathPrefix.txt").readText())
        else
            DiagnosticTestData(sourceCode, fileContentHash, antlrTreeFile)
    }
}
