package org.jetbrains.kotlin.grammar.util

import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.rt.execution.junit.FileComparisonFailure
import org.junit.Assert
import java.io.File

sealed class TestData(val sourceCode: String, val antlrParseTreeText: File)

class PsiTestData(sourceCode: String, antlrParseTreeText: File, val psiParseTreeText: String) : TestData(sourceCode, antlrParseTreeText)

class DiagnosticTestData(sourceCode: String, antlrParseTreeText: File) : TestData(sourceCode, antlrParseTreeText)


object TestUtil {
    const val TESTS_DIR = "testData"

    val ls = System.lineSeparator()

    private fun String.trimTrailingWhitespacesAndAddNewlineAtEOF() =
            split(ls).map(String::trimEnd).joinToString(ls).let {
                result -> if (result.endsWith(ls)) result else result + ls
            }

    fun assertEqualsToFile(message: String, expectedFile: File, actual: String) {
        val actualText = StringUtil.convertLineSeparators(actual.trim { it <= ' ' }).trimTrailingWhitespacesAndAddNewlineAtEOF()

        if (!expectedFile.exists()) {
            FileUtil.writeToFile(expectedFile, actualText)
            Assert.fail("Expected data file did not exist. Generating: $expectedFile")
        }
        val expected = FileUtil.loadFile(expectedFile, CharsetToolkit.UTF8, true)
        val expectedText = StringUtil.convertLineSeparators(expected.trim { it <= ' ' }).trimTrailingWhitespacesAndAddNewlineAtEOF()

        if (!Comparing.equal(expectedText, actualText))
            throw FileComparisonFailure(message + ": " + expectedFile.name, expected, actual, expectedFile.absolutePath)
    }

    fun getTestData(testFile: File): TestData {
        val relativePath = "${testFile.parent}/${testFile.nameWithoutExtension}"
        val testPathPrefix = "$TESTS_DIR/$relativePath"
        val sourceCode = File("$testPathPrefix.kt").readText()
        val antlrTreeFile = File("testData/$relativePath.antlrtree.txt")
        val isPsiTest = File("$testPathPrefix.txt").exists()

        println(File("$testPathPrefix.kt").absolutePath)
        println(File("$testPathPrefix.antlrtree.txt").absolutePath)

        return if (isPsiTest)
            PsiTestData(sourceCode, antlrTreeFile, File("$testPathPrefix.txt").readText())
        else
            DiagnosticTestData(sourceCode, antlrTreeFile)
    }
}
