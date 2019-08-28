package org.jetbrains.kotlin.spec.tests

import org.jetbrains.kotlin.spec.utils.format

object SpecTestsParser {
    private const val SINGLE_LINE_COMMENT_REGEX = "\\/\\/\\s*{1}"
    private const val MULTILINE_COMMENT_REGEX = "\\/\\*\\s+?{1}\\s+\\*\\/(?:\\n)*"
    private const val TEST_CASE_CODE_REGEX = "(?<{1}>[\\s\\S]*?)"
    private const val ASTERISK_REGEX = "\\*"
    private val directiveRegex = SINGLE_LINE_COMMENT_REGEX.format("[\\w\\s]+:") + "|" + MULTILINE_COMMENT_REGEX.format(" $ASTERISK_REGEX [\\w\\s]+:[\\s\\S]*?")

    private const val testCaseInfoElementsRegex = "(?<{1}>{2}TESTCASE NUMBER:{3}*?\\n)"
    private val testCaseInfoRegex = "$TEST_CASE_CODE_REGEX(?<{2}>(?:$directiveRegex)|$)"
    private val testCaseInfoSingleLineRegex = SINGLE_LINE_COMMENT_REGEX.format(
        testCaseInfoElementsRegex.format("infoElementsSL", "", "\\s*.")
    ) + testCaseInfoRegex.format("codeSL", "nextDirectiveSL")
    private val testCaseInfoMultilineRegex = MULTILINE_COMMENT_REGEX.format(
        testCaseInfoElementsRegex.format("infoElementsML", " $ASTERISK_REGEX ", "[\\s\\S]")
    ) + testCaseInfoRegex.format("codeML", "nextDirectiveML")

    private val testCaseInfoPattern = Regex(
        "(?:{1})|(?:{2})".format(testCaseInfoSingleLineRegex, testCaseInfoMultilineRegex)
    )
    private val testInfoElementPattern = Regex("(?: \\* )?(?<name>[A-Z ]+?)(?::\\s*(?<value>.*?))?\\n", RegexOption.MULTILINE)

    private const val testTypeRegex = "(?<testType>POSITIVE|NEGATIVE)"
    private const val testAreaRegex = "(?<testArea>PSI|DIAGNOSTICS|CODEGEN_BOX)"
    val testPathStartingParagraphRegexp = Regex("^p-[1-9]\\d*/(pos|neg)/[1-9]\\d*.[1-9]\\d*.kt$")

    private val testInfoPattern = Regex(
        MULTILINE_COMMENT_REGEX.format(
            """{1} KOTLIN {2} SPEC TEST \({3}\)\n(?<infoElements>[\s\S]*?\n)""".format(ASTERISK_REGEX, testAreaRegex, testTypeRegex)
        )
    )

    private val diagnosticTagRegexp = Regex("(<!>)|(<!(.+?(\\(\".*?\"\\))?(,\\s*)?)+?!>)", RegexOption.MULTILINE)

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

    fun parseTest(data: String): Map<String, Any?> {
        val testMatches = testInfoPattern.find(data)
        val testInfoElements =
                parseTestInfoElements(testMatches?.groups?.get(3)?.value ?: throw Exception("Unable to parse test"))
        val test = mapOf(
            "cases" to mutableListOf<Map<String, Any>>(),
            "description" to testInfoElements["DESCRIPTION"],
            "unexpectedBehaviour" to (testInfoElements["UNEXPECTED_BEHAVIOUR"] != null),
            "helpers" to testInfoElements["HELPERS"]?.run { split(", ") }
        )
        var testCaseMatches = testCaseInfoPattern.find(data)
        var startPosition = 0

        while (testCaseMatches != null) {
            val infoElements = (testCaseMatches.groups[1] ?: testCaseMatches.groups[4])?.value ?: continue
            val code = (testCaseMatches.groups[2] ?: testCaseMatches.groups[5])?.value?.replace(diagnosticTagRegexp, "") ?: continue

            startPosition += testCaseMatches.range.last
            test["cases"].unsafeCast<MutableList<Map<String, Any>>>().add(
                    mapOf(
                        "infoElements" to parseTestInfoElements(infoElements),
                        "code" to code
                    )
            )
            testCaseMatches = testCaseInfoPattern.find(data.substring(startPosition))
        }

        return test
    }
}
