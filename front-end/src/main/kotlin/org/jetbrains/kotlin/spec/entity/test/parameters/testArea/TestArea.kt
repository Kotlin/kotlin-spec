package org.jetbrains.kotlin.spec.entity.test.parameters.testArea

import org.jetbrains.kotlin.spec.entity.test.TestCase
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.SpecTestsParser.implementationTestInfoPattern
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.SpecTestsParser.topLevelDirectivePattern

enum class TestArea(
        val path: String,
        val description: String,
        val shortName: String
) {
    Diagnostics("diagnostics", "Front-end diagnostics tests", "diagnostics"),
    CodegenBox("codegen/box", "Codegen box tests", "box");

    fun parseTest(data: String): List<TestCase> {
        return when (this) {
            CodegenBox -> parseCodegenBoxSpecTest(data)
            Diagnostics -> parseDiagnosticsTest(data)
        }
    }

    companion object {
        val SPEC_MODULE_DIR = """// MODULE:"""
        val SPEC_FILE_DIR = """// FILE:"""

        private fun parseCodegenBoxSpecTest(testFileCode: String): List<TestCase> {
            val data = getTestCasesCode(testFileCode)
            val testCase = TestCase(data.replace(SpecTestsParser.testInfoPattern, ""))
            return listOf(testCase)
        }


        private fun parseDiagnosticsTest(testFileCode: String): List<TestCase> {
            val data = getTestCasesCode(testFileCode)

            val list = mutableListOf<TestCase>()
            var testCaseMatches = SpecTestsParser.testCaseInfoPattern.find(data)
            var startPosition = 0
            while (testCaseMatches != null) {
                val infoElementGroup = testCaseMatches.groups[1] ?: testCaseMatches.groups[4]
                val infoElements = infoElementGroup?.value ?: continue
                val codeGroup = testCaseMatches.groups[2] ?: testCaseMatches.groups[5]
                val code = (codeGroup)?.value?.replace(SpecTestsParser.diagnosticTagRegexp, "") ?: continue
                val testCaseMatchesGroup = (testCaseMatches.groups[3] ?: testCaseMatches.groups[6]) ?: continue

                startPosition += testCaseMatches.range.last - (testCaseMatchesGroup).value.length

                //todo this is a temp filtering of tests until kotlin playground functionality is enhanced
                if (!code.contains(SPEC_FILE_DIR) && !code.contains(SPEC_MODULE_DIR))
                    list.add(TestCase(code = code, infoElements = SpecTestsParser.parseTestInfoElements(infoElements)))

                testCaseMatches = SpecTestsParser.testCaseInfoPattern.find(data.substring(startPosition))
            }
            return list
        }

        private fun getTestCasesCode(testFileCode: String) = testFileCode
                .replace(SpecTestsParser.diagnosticTagRegexp, "")
                .replace(implementationTestInfoPattern, "")
                .replace(topLevelDirectivePattern, "")

        fun getByShortName(name: String): TestArea {
            values().forEach { testArea ->
                if (testArea.shortName == name)
                    return testArea
            }
            throw Exception("TestArea by short name $name is not found")
        }
    }
}

