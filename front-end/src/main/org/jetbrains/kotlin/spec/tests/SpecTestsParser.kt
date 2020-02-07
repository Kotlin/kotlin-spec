package org.jetbrains.kotlin.spec.tests

import org.jetbrains.kotlin.spec.utils.TestArea
import org.jetbrains.kotlin.spec.utils.format

object SpecTestsParser {
    private const val SINGLE_LINE_COMMENT_REGEX = """\/\/\s*{1}"""
    private const val MULTILINE_COMMENT_REGEX = """\/\*\s+?{1}\s+\*\/(?:\n)*"""
    private const val TEST_CASE_CODE_REGEX = """(?<{1}>[\s\S]*?)"""
    private const val ASTERISK_REGEX = """\*"""
    private val testCaseHeaderRegex = SINGLE_LINE_COMMENT_REGEX.format("""[\w\s]+TESTCASE NUMBER""") +
            "|" + MULTILINE_COMMENT_REGEX.format(""" $ASTERISK_REGEX [\w\s]+TESTCASE NUMBER:[\s\S]*?""")

    private const val testCaseInfoElementsRegex = """(?<{1}>{2}TESTCASE NUMBER:{3}*?\n)"""
    private val testCaseInfoRegex = "$TEST_CASE_CODE_REGEX(?<{2}>(?:$testCaseHeaderRegex)|$)"
    private val testCaseInfoSingleLineRegex = SINGLE_LINE_COMMENT_REGEX.format(
            testCaseInfoElementsRegex.format("infoElementsSL", "", """\s*.""")
    ) + testCaseInfoRegex.format("codeSL", "nextDirectiveSL")
    private val testCaseInfoMultilineRegex = MULTILINE_COMMENT_REGEX.format(
            testCaseInfoElementsRegex.format("infoElementsML", " $ASTERISK_REGEX ", """[\s\S]""")
    ) + testCaseInfoRegex.format("codeML", "nextDirectiveML")

    private val testCaseInfoPattern = Regex(
            "(?:{1})|(?:{2})".format(testCaseInfoSingleLineRegex, testCaseInfoMultilineRegex)
    )
    private val testInfoElementPattern = Regex("(?: \\* )?(?<name>[A-Z ]+?)(?::\\s*(?<value>.*?))?\\n", RegexOption.MULTILINE)

    private const val testTypeRegex = "(?<testType>POSITIVE|NEGATIVE)"
    private const val testAreaRegex = "(?<testArea>PSI|DIAGNOSTICS|CODEGEN BOX)"
    val testPathStartingParagraphRegexp = Regex("^p-[1-9]\\d*/(pos|neg)/[1-9]\\d*.[1-9]\\d*.kt$")

    private val testInfoPattern = Regex(
            MULTILINE_COMMENT_REGEX.format(
                    """{1} KOTLIN {2} SPEC TEST \({3}\)\n(?<infoElements>[\s\S]*?\n)""".format(ASTERISK_REGEX, testAreaRegex, testTypeRegex)
            )
    )
    private val implementationTestInfoPattern = Regex(MULTILINE_COMMENT_REGEX.format("""\*\s+RELEVANT SPEC SENTENCES \(spec version: (?<specVersion>\d+\.[0-9]\d*\-[0-9]\d*), test type: (?<testType>pos|neg)\):(?<testSpecSentenceList>(\n\s+\*\s+-\s+.*?)+)"""))
    private val topLevelDirectivePattern = Regex("""^(?://.*?\n)+""")

    private val diagnosticTagRegexp = Regex("""(?:<!>)|(?:<!(.+?(\(".*?"\))?(,\s*)?)+?!>)""", RegexOption.MULTILINE)

    private fun parseTestInfoElements(data: String): Map<String, String?> {
        val testInfoElements = mutableMapOf<String, String?>()
        var matches = testInfoElementPattern.find(data)
        var startPosition = 0

        while (matches != null) {
            val name = matches.groups[1]?.value ?: continue
            val value = matches.groups[2]?.value

            startPosition += matches.range.last
            testInfoElements[name] = value
            matches = testInfoElementPattern.find(data.substring(startPosition))
        }

        return testInfoElements
    }


    fun parseSpecTest(response: Map<String, Any>, testArea: TestArea): Map<String, Any?> {
        val data = response[testArea.content].toString()
                .replace(diagnosticTagRegexp, "")
                .replace(implementationTestInfoPattern, "")
                .replace(topLevelDirectivePattern, "")
        return when (testArea) {
            TestArea.DIAGNOSTICS -> getInfoForImplementationDiagnosticsTest(data)
            TestArea.CODEGEN_BOX -> getInfoForImplementationCodegenBoxTest(data)
        }
    }

    private fun getInfoForImplementationDiagnosticsTest(data: String): Map<String, Any?> {
        val testInfoElements =
                parseTestInfoElements(testInfoPattern.find(data)?.groups?.get(3)?.value
                        ?: throw Exception("Unable to parse test"))

        val test = mapOf(
                "cases" to mutableListOf<Map<String, Any>>(),
                "description" to testInfoElements["DESCRIPTION"],
                "unexpectedBehaviour" to (testInfoElements["UNEXPECTED_BEHAVIOUR"] != null),
                "helpers" to testInfoElements["HELPERS"]?.run { split(", ") }
        )

        var testCaseMatches = testCaseInfoPattern.find(data)

        var startPosition = 0
        while (testCaseMatches != null) {
            val infoElementGroup = testCaseMatches.groups[1] ?: testCaseMatches.groups[4]
            val infoElements = (infoElementGroup)?.value ?: continue
            val codeGroup = testCaseMatches.groups[2] ?: testCaseMatches.groups[5]
            val code = (codeGroup)?.value?.replace(diagnosticTagRegexp, "") ?: continue
            val testCaseMatchesGroup = (testCaseMatches.groups[3] ?: testCaseMatches.groups[6]) ?: continue
            startPosition += testCaseMatches.range.last - (testCaseMatchesGroup)?.value?.length

            test["cases"].unsafeCast<MutableList<Map<String, Any>>>().add(
                    mapOf(
                            "infoElements" to parseTestInfoElements(infoElements),
                            "code" to code,
                            "unexpectedBehaviour" to testInfoElements.containsKey("UNEXPECTED BEHAVIOUR")
                    )
            )
            testCaseMatches = testCaseInfoPattern.find(data.substring(startPosition))
        }
        return test
    }

    private fun getInfoForImplementationCodegenBoxTest(data: String): Map<String, Any?> {
        val testInfoElements =
                parseTestInfoElements(testInfoPattern.find(data)?.groups?.get(3)?.value
                        ?: throw Exception("Unable to parse test"))

        return mapOf(
                "cases" to listOf(
                        mapOf(
                                "infoElements" to null,
                                "code" to data.replace(testInfoPattern, "")
                        )
                ),
                "description" to testInfoElements["DESCRIPTION"],
                "unexpectedBehaviour" to testInfoElements.containsKey("UNEXPECTED BEHAVIOUR"),
                "helpers" to testInfoElements["HELPERS"]?.split(", ")
        )

    }


}
