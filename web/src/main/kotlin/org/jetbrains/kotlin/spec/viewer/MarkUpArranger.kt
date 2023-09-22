package org.jetbrains.kotlin.spec.viewer

import org.jetbrains.kotlin.spec.`$`
import org.jetbrains.kotlin.spec.JQuery
import org.jetbrains.kotlin.spec.utils.format
import org.w3c.dom.HTMLElement


object MarkUpArranger {
    fun showMarkUpForParagraphs(paragraphsInfo: List<Map<String, Any>>, sectionsPath: String) {
        paragraphsInfo.forEachIndexed { paragraphIndex, paragraph ->
            val paragraphNumber = paragraphIndex + 1
            val paragraphEl = (paragraph["paragraphElement"] as? HTMLElement)?.let { `$`(it).apply { addClass("with-tests") } }
                    ?: return@forEachIndexed
            showSentencesNumberForParagraph(paragraphEl, paragraphNumber, sectionsPath)
        }
    }

    private fun showSentencesNumberForParagraph(paragraph: JQuery, paragraphNumber: Int, sectionPath: String) {
        val sentences = `$`(paragraph).find(".sentence")
        var sentenceCounter = 1

        insertParagraphNumber(`$`(paragraph), paragraphNumber, sectionPath, paragraphNumber)

        sentences.each { _, el ->
            val sentence = `$`(el)
            val existingNumberInfo = sentence.find(".number-info")
            if (existingNumberInfo.length.toInt() > 0) {
                existingNumberInfo.remove()
            }
            insertSentenceNumber(sentence, sentenceCounter, sectionPath, paragraphNumber, sentenceCounter)
            sentenceCounter++
        }
    }

    fun insertParagraphNumber(element: JQuery, number: Int, sectionPath: String, paragraphNumber: Int) {
        element.prepend("<span class='paragraph-link' data-link='{1}'>{2}</span>".format(
                "paragraph=${sectionPath.replace(" ", "")},$paragraphNumber", number
        ))
    }

    fun insertSentenceNumber(element: JQuery, number: Int, sectionPath: String, paragraphNumber: Int, sentenceNumber: Int) {
        element.prepend("<span class='number-info' data-path='{1}' data-link='{2}'>{3}</span>".format(
                "$sectionPath -> paragraph $paragraphNumber -> sentence $sentenceNumber",
                "sentence=${sectionPath.replace(" ", "")},$paragraphNumber,$sentenceNumber",
                number
        ))
    }
}