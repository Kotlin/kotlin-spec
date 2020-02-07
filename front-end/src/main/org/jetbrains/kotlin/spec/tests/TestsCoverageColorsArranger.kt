package org.jetbrains.kotlin.spec.tests

import js.externals.jquery.JQuery
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.utils.TestArea
import org.jetbrains.kotlin.spec.utils.format
import org.jetbrains.kotlin.spec.utils.getValueByObjectPath
import org.jetbrains.kotlin.spec.utils.setValueByObjectPath
import org.w3c.dom.HTMLElement

object TestsCoverageColorsArranger {
    val testTypes = mapOf("pos" to "positive", "neg" to "negative")

    private val testAreas = mapOf("diagnostics" to "Front-end diagnostics tests", "box" to "Codegen box tests")

    const val TEMPLATE = """
            <div class='test-coverage-view'>
                <select name='test-area'>
                    <option value='diagnostics'>Front-end diagnostics tests</option>
                    <option value='box'>Codegen box tests</option>
                </select>
                <select name='test-type'></select>
                <select name='test-number'></select>
            </div>
        """

    private fun insertParagraphNumber(element: JQuery, number: Int, sectionPath: String, paragraphNumber: Int) {
        element.prepend("<span class='paragraph-link' data-link='{1}'>{2}</span>".format(
                "paragraph=${sectionPath.replace(" ", "")},$paragraphNumber", number
        ))
    }

    private fun insertSentenceNumber(element: JQuery, number: Int, sectionPath: String, paragraphNumber: Int, sentenceNumber: Int) {
        element.prepend("<span class='number-info' data-path='{1}' data-link='{2}'>{3}</span>".format(
                "$sectionPath -> paragraph $paragraphNumber -> sentence $sentenceNumber",
                "sentence=${sectionPath.replace(" ", "")},$paragraphNumber,$sentenceNumber",
                number
        ))
    }

    private fun detectUnexpectedBehaviour(testsOfType: Map<String, Map<String, Any>>): Boolean {
        var unexpectedBehaviour = false

        for ((_, test) in testsOfType) {
            if (test["unexpectedBehaviour"] as Boolean) {
                unexpectedBehaviour = true
            }
            (test["cases"].unsafeCast<List<Map<String, Map<String, Any>>>>()).forEach { testCase ->
                if (testCase["infoElements"]?.get("UNEXPECTED BEHAVIOUR") != null) {
                    unexpectedBehaviour = true
                }
            }
        }

        return unexpectedBehaviour
    }

    private fun showSentenceCoverage(sentence: JQuery, sentenceTestInfo: Map<String, Map<String, Map<String, Map<String, String>>>>) {
        val testsByArea = mutableListOf<String>()
        var unexpectedBehaviour = false

        for ((testArea, testsOfArea) in sentenceTestInfo) {
            val testNumberByType = mutableMapOf<String, Int>()

            for ((i, testType) in testsOfArea) {
                var sum = 0
                for ((_, sentence1) in testType) {
                    sum += (sentence1["cases"].unsafeCast<List<Map<String, Map<String, Any>>>>()).size
                }

                testNumberByType[i] = sum
                unexpectedBehaviour = unexpectedBehaviour || detectUnexpectedBehaviour(testType)
            }

            val testNumberByTypeInfo = mutableListOf<String>()
            for ((i, testNumberInfo) in testNumberByType) {
                testNumberByTypeInfo.add(testNumberInfo.toString() + " " + testTypes[i])
            }
            if (testNumberByTypeInfo.size > 0) {
                testsByArea.add("<b>" + testAreas[testArea] + "</b>: " + testNumberByTypeInfo.joinToString(", "))
            }
        }

        if (unexpectedBehaviour) {
            sentence.addClass("unexpected-behaviour")
                    .parent().before("<span class='unexpected-behaviour-marker'></span>")
        }
        sentence.prepend("<span class='coverage-info'>{1}</span>".format(testsByArea.joinToString("<br />")))
                .data("tests", sentenceTestInfo)
                .addClass("covered")
    }

    private fun showParagraphCoverage(paragraph: JQuery, paragraphNumber: Int, tests: Map<TestArea, MutableMap<String, Any>>, sectionPath: String) {
        val sentences = `$`(paragraph).find(".sentence")
        var sentenceCounter = 1
        val paragraphDiagnosticTests =
                tests[TestArea.DIAGNOSTICS]?.let { getValueByObjectPath<Map<String, Map<String, Map<String, String>>>?>(it, sectionPath)?.get("p-$paragraphNumber") }
        val paragraphCodegenBoxTests =
                tests[TestArea.CODEGEN_BOX]?.let { getValueByObjectPath<Map<String, Map<String, Map<String, String>>>?>(it, sectionPath)?.get("p-$paragraphNumber") }

        insertParagraphNumber(`$`(paragraph), paragraphNumber, sectionPath, paragraphNumber)

        sentences.each { _, el ->
            val sentenceTests =
                    mutableMapOf<String, Map<String, Map<String, Map<String, String>>>>()
            val sentence = `$`(el)

            val existingNumberInfo = sentence.find(".number-info")
            if (existingNumberInfo.length.toInt() > 0) {
                existingNumberInfo.remove()
            }

            if (paragraphDiagnosticTests != null) {
                for ((testType, testsByType) in paragraphDiagnosticTests) {
                    val sentenceTestsByTestTypes = testsByType[sentenceCounter.toString()]
                    if (sentenceTestsByTestTypes != null) {
                        setValueByObjectPath(sentenceTests.unsafeCast<MutableMap<String, Any>>(), sentenceTestsByTestTypes, "diagnostics.$testType")
                    }
                }
                if (sentenceTests.isNotEmpty()) {
                    showSentenceCoverage(sentence, sentenceTests)
                }
            }
            if (paragraphCodegenBoxTests != null) {
                for ((testType, testsByType) in paragraphCodegenBoxTests) {
                    val sentenceTestsByTestTypes = testsByType[sentenceCounter.toString()]
                    if (sentenceTestsByTestTypes != null) {
                        setValueByObjectPath(sentenceTests.unsafeCast<MutableMap<String, Any>>(), sentenceTestsByTestTypes, "box.$testType")
                    }
                }
                if (sentenceTests.isNotEmpty()) {
                    showSentenceCoverage(sentence, sentenceTests)
                }
            }

            insertSentenceNumber(sentence, sentenceCounter, sectionPath, paragraphNumber, sentenceCounter)
            sentenceCounter++
        }
    }

    fun showCoverage(paragraphsInfo: List<Map<String, Any>>, tests: Map<TestArea, MutableMap<String, Any>>, sectionsPath: String) {
        paragraphsInfo.forEachIndexed { paragraphIndex, paragraph ->
            val paragraphNumber = paragraphIndex + 1
            val paragraphEl = `$`(paragraph["paragraphElement"] as HTMLElement).apply { addClass("with-tests") }

            showParagraphCoverage(paragraphEl, paragraphNumber, tests, sectionsPath)
        }
    }
}
