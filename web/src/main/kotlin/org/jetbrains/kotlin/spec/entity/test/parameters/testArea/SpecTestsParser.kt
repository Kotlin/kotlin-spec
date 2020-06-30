package org.jetbrains.kotlin.spec.entity.test.parameters.testArea

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

    internal val testCaseInfoPattern = Regex(
            "(?:{1})|(?:{2})".format(testCaseInfoSingleLineRegex, testCaseInfoMultilineRegex)
    )
    private val testInfoElementPattern = Regex("(?: \\* )?(?<name>[A-Z ]+?)(?::\\s*(?<value>.*?))?\\n", RegexOption.MULTILINE)

    private const val testTypeRegex = "(?<testType>POSITIVE|NEGATIVE)"
    private const val testAreaRegex = "(?<testArea>PSI|DIAGNOSTICS|CODEGEN BOX)"

    internal val testInfoPattern = Regex(
            MULTILINE_COMMENT_REGEX.format(
                    """{1} KOTLIN {2} SPEC TEST \({3}\)\n(?<infoElements>[\s\S]*?\n)""".format(ASTERISK_REGEX, testAreaRegex, testTypeRegex)
            )
    )
    val implementationTestInfoPattern = Regex(MULTILINE_COMMENT_REGEX.format("""\*\s+RELEVANT SPEC SENTENCES \(spec version: (?<specVersion>\d+\.[0-9]\d*\-[0-9]\d*), test type: (?<testType>pos|neg)\):(?<testSpecSentenceList>(\n\s+\*\s+-\s+.*?)+)"""))
    val topLevelDirectivePattern = Regex("""^(?://.*?\n)+""")

    internal val diagnosticTagRegexp = Regex("""(?:<!>)|(?:<!(.+?(\(".*?"\))?(,\s*)?)+?!>)""", RegexOption.MULTILINE)

    internal fun parseTestInfoElements(data: String): Map<String, String?> {
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
}
