package org.jetbrains.kotlin.spec.viewer.links

import js.externals.jquery.JQuery
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.loader.SpecTestsLoader
import org.jetbrains.kotlin.spec.utils.Mode
import org.jetbrains.kotlin.spec.utils.Popup
import org.jetbrains.kotlin.spec.utils.PopupConfig
import org.jetbrains.kotlin.spec.utils.format
import org.w3c.dom.HTMLElement
import kotlin.browser.window

data class SpecPlaceComponents(
        val sectionId: String,
        val paragraphNumber: Int,
        val sentenceNumber: Int? = null
)

object SpecPlaceHighlighter {
    private const val PARAGRAPH_NOT_FOUND = "Paragraph \"{1}\" of section \"{2}\" is not found."
    private const val SENTENCE_NOT_FOUND = "Sentence \"{1}\" is not found."
    private const val ESCAPED_SECTION_ID_SEPARATOR = """\."""

    private fun findParagraph(sectionId: String, paragraphNumber: Int): JQuery? {
        val sectionElement = `$`("#${sectionId.replace(".", ESCAPED_SECTION_ID_SEPARATOR)}")
        val paragraphsInfo = SpecTestsLoader.getParagraphsInfo(sectionElement) ?: return null

        return if (paragraphsInfo.size > paragraphNumber - 1) {
            `$`(paragraphsInfo[paragraphNumber - 1]["paragraphElement"] as HTMLElement)
        } else {
            window.alert(PARAGRAPH_NOT_FOUND.format(paragraphNumber, sectionId))
            null
        }
    }

    private fun findSentence(paragraphElement: JQuery, sentenceNumber: Int): JQuery? {
        val sentences = paragraphElement.find(".sentence")

        return if (sentences.length.toInt() > sentenceNumber - 1) {
            `$`(sentences[sentenceNumber - 1]!!)
        } else {
            window.alert(SENTENCE_NOT_FOUND.format(sentenceNumber))
            null
        }
    }

    private fun highlightParagraph(sectionId: String, paragraphNumber: Int) {
        val paragraph = findParagraph(sectionId, paragraphNumber) ?: return

        paragraph.addClass("highlighted")

        paragraph[0]?.scrollIntoView()
        `$`(window).scrollTop(`$`(window).scrollTop().toInt() - 80)
    }

    fun highlightParagraph(paragraphToBeHighlighted: String) =
            highlightParagraph(getParagraphInfo(paragraphToBeHighlighted.trimEnd()))

    fun highlightParagraph(sentencePath: SpecPlaceComponents) {
        highlightParagraph(sentencePath.sectionId, sentencePath.paragraphNumber)
    }

    private fun highlightSentence(sectionId: String, paragraphNumber: Int, sentenceNumber: Int) {
        val paragraphElement = findParagraph(sectionId, paragraphNumber) ?: return
        val sentence = findSentence(paragraphElement, sentenceNumber) ?: return

        sentence.addClass("highlighted")
        sentence[0]?.scrollIntoView()
        `$`(window).scrollTop(`$`(window).scrollTop().toInt() - 80)
    }

    fun highlightSentence(sentenceToBeHighlighted: String) =
            highlightSentence(getSentenceInfoFromUrl(sentenceToBeHighlighted))

    fun highlightSentence(sentencePath: SpecPlaceComponents) {
        highlightSentence(sentencePath.sectionId, sentencePath.paragraphNumber, sentencePath.sentenceNumber ?: return)
    }

    fun highlightSection(sectionId: String) {
        val sectionElement = `$`("#${sectionId.replace(".", """\\.""")}")

        sectionElement.addClass("highlighted")
        sectionElement[0]?.scrollIntoView()
        `$`(window).scrollTop(`$`(window).scrollTop().toInt() - 80)
    }

    private fun getSentenceInfoFromUrl(sentenceToBeHighlighted: String): SpecPlaceComponents {
        val splittedSentencePath = sentenceToBeHighlighted.split(Regex("""\s*,\s*"""))
        val splittedSentencePathSize = splittedSentencePath.size

        return SpecPlaceComponents(
                splittedSentencePath[splittedSentencePathSize - 3],
                splittedSentencePath[splittedSentencePathSize - 2].toInt(),
                splittedSentencePath[splittedSentencePathSize - 1].toInt()
        )
    }

    fun getSentenceInfoFromSearchField(sentenceToBeHighlighted: String): SpecPlaceComponents {
        val splittedSentencePath = sentenceToBeHighlighted.split(Regex("""\s*->\s*"""))
        val splittedSectionPath = splittedSentencePath[0].split(Regex("""\s*,\s*"""))

        return SpecPlaceComponents(
                splittedSectionPath.last(),
                splittedSentencePath[1].replace("paragraph ", "").toInt(),
                if (splittedSentencePath.size == 3) splittedSentencePath[2].replace("sentence ", "").toInt() else null
        )
    }

    private fun getParagraphInfo(paragraphToBeHighlighted: String): SpecPlaceComponents {
        val splittedSentencePath = paragraphToBeHighlighted.split(Regex("""\s*,\s*"""))
        val splittedSentencePathSize = splittedSentencePath.size

        return SpecPlaceComponents(
                splittedSentencePath[splittedSentencePathSize - 2],
                splittedSentencePath[splittedSentencePathSize - 1].toInt()
        )
    }

    private fun getLink(searchComponent: String) = window.location.run {
        "$protocol//$hostname$pathname?$searchComponent$hash"
    }

    private fun getSectionLink(sectionId: String) = window.location.run {
        "$protocol//$hostname$pathname${if (search.isNotEmpty()) "?$search" else ""}#$sectionId"
    }

    fun onSentenceGetLinkClick(element: JQuery, mode: Mode) {
        Popup(
                PopupConfig(
                        title = "Sentence info",
                        content = """<div class="sentence-links-popup">{1}</div>""".format(
                                buildString {
                                    if (mode == Mode.Dev) {
                                        append("""<div class="sentence-links-row">
                                                    <span class="link-sentence-description-link-type">Link for compiler test:</span>
                                                    <input type="text" class="sentence-path-for-compiler-test" value="${element.data("path")}">
                                                  </div>
                                                  """
                                        )
                                    }
                                    append("""<div class="sentence-links-row">
                                                <span class="link-sentence-description-link-type">Sentence path:</span>
                                                <input type="text" class="sentence-link-field" value="${getLink(element.data("link").toString())}">
                                              </div>
                                              """
                                    )
                                }
                        ).trimIndent(),
                        width = 800,
                        height = if (mode == Mode.Dev) 150 else 100
                )
        ).apply { open() }
        `$`(".sentence-path-for-compiler-test").select().focus()
    }

    fun onParagraphGetLinkClick(element: JQuery) {
        Popup(
                PopupConfig(
                        title = "Link to this paragraph",
                        content = """
                            <div class="sentence-links-popup">
                                <div class="sentence-links-row"><span class="link-sentence-description-link-type">Link:</span><input type="text" class="sentence-link-field" value="${getLink(element.data("link").toString())}"></div>
                            </div>
                        """.trimIndent(),
                        width = 800,
                        height = 100
                )
        ).apply { open() }
        `$`(".sentence-link-field").select().focus()
    }

    fun onHeaderGetLinkClick(element: JQuery) {
        Popup(
                PopupConfig(
                        title = "Link to this section",
                        content = """
                            <div class="sentence-links-popup">
                                <div class="sentence-links-row"><span class="link-sentence-description-link-type">Link:</span><input type="text" class="sentence-link-field" value="${getSectionLink(element.attr("id"))}"></div>
                            </div>
                        """.trimIndent(),
                        width = 800,
                        height = 100
                )
        ).apply { open() }
        `$`(".sentence-link-field").select().focus()
    }
}
