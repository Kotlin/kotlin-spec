package org.jetbrains.kotlin.spec.tests

import org.jetbrains.kotlin.spec.utils.format

class ImplementationTestParser {
    companion object {
        const val MULTILINE_COMMENT_REGEX = """\/\*\s+?%s\s+\*\/(?:\n)*"""
    }

    val testInfoPattern =
            Regex(MULTILINE_COMMENT_REGEX.format("""\*\s+RELEVANT SPEC SENTENCES \(spec version: (?<specVersion>\d+\.[0-9]\d*\-[0-9]\d*), test type: (?<testType>pos|neg)\):(?<testSpecSentenceList>(\n\s+\*\s+-\s+.*?)+)"""))
    val relevantSpecSentencesPattern =
            Regex("""\n\s+\*\s+-\s+(?<specSections>.*?) -> paragraph (?<specParagraph>[1-9]\d*) -> sentence (?<specSentence>[1-9]\d*)""")
}
