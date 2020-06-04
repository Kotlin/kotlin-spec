package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.JQuery
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.entity.SpecParagraph
import org.jetbrains.kotlin.spec.entity.SpecSection
import org.jetbrains.kotlin.spec.entity.SpecSentence
import org.jetbrains.kotlin.spec.entity.test.SpecTest
import org.jetbrains.kotlin.spec.utils.format
import org.w3c.dom.HTMLElement

object SpecCoverageHighlighter {

    const val TEMPLATE = """
            <div class='test-coverage-view'>
                <select name='test-area'>
                    <option value='diagnostics'>Front-end diagnostics tests</option>
                    <option value='box'>Codegen box tests</option>
                </select>
                <select name='test-type'></select>
                <select name='test-link-type'>
                    <option value='main'>Main tests</option>
                    <option value='primary'>Primary tests</option>
                    <option value='secondary'>Secondary tests</option>
                </select>
                <select name='test-number'></select>
            </div>
        """


    private fun List<SpecTest>.hasUnexpectedBehaviour() = any { it.testInfo.unexpectedBehaviour }

    private fun showSentenceCoverage(sentence: JQuery, specSentenceTestSet: SpecSentence) {
        val testsByArea = mutableListOf<String>()
        var unexpectedBehaviour = false

        for ((testArea, listOfTests) in specSentenceTestSet.specTests) {

            val testNumberByTypeInfo = mutableListOf<String>()
            for ((testType, typedTests) in listOfTests.groupBy { it.testPlace.testType }) {
                val filteredTests: List<SpecTest> = typedTests.filter { it.testCases.isNotEmpty() }
                unexpectedBehaviour = unexpectedBehaviour || typedTests.hasUnexpectedBehaviour()

                if (filteredTests.isNotEmpty()) {
                    testNumberByTypeInfo.add(filteredTests.size.toString() + " " + testType.toString())
                    testsByArea.add("<b>" + testArea.description + "</b>: " + testNumberByTypeInfo.joinToString(", "))
                }
            }

        }

        if (unexpectedBehaviour) {
            sentence.addClass("unexpected-behaviour")
                    .parent().before("<span class='unexpected-behaviour-marker'></span>")
        }

        if (testsByArea.isNotEmpty()) {
            sentence.prepend("<span class='coverage-info'>{1}</span>".format(testsByArea.joinToString("<br />")))
                    .data("tests", specSentenceTestSet)
                    .addClass("covered")

        }
    }

    private fun showParagraphCoverage(paragraph: JQuery, sectionPath: String, specParagraphTestSet: SpecParagraph) {
        val sentences = `$`(paragraph).find(".sentence")
        var sentenceCounter = 1
        val paragraphNumber = specParagraphTestSet.paragraph

        MarkUpArranger.insertParagraphNumber(`$`(paragraph), paragraphNumber, sectionPath, paragraphNumber)

        sentences.each { _, el ->
            val sentence = `$`(el)

            val existingNumberInfo = sentence.find(".number-info")
            if (existingNumberInfo.length.toInt() > 0) {
                existingNumberInfo.remove()
            }

            val sentenceTestSet = SpecSentence(specParagraph = specParagraphTestSet,
                    sentence = sentenceCounter)
            showSentenceCoverage(sentence, sentenceTestSet)
            MarkUpArranger.insertSentenceNumber(sentence, sentenceCounter, sectionPath, paragraphNumber, sentenceCounter)
            sentenceCounter++
        }
    }


    fun showCoverageOfParagraphs(paragraphsInfo: List<Map<String, Any>>, specSectionTestSet: SpecSection, sectionsPath: String) {
        paragraphsInfo.forEachIndexed { paragraphIndex, paragraph ->
            val paragraphNumber = paragraphIndex + 1
            val paragraphEl = (paragraph["paragraphElement"] as? HTMLElement)?.let { `$`(it).apply { addClass("with-tests") } }
                    ?: return@forEachIndexed

            val paragraphTestSet = SpecParagraph(specSection = specSectionTestSet,
                    paragraph = paragraphNumber)
            showParagraphCoverage(paragraphEl, sectionsPath, paragraphTestSet)
        }
    }
}
