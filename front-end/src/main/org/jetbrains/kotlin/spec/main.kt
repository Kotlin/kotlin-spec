package org.jetbrains.kotlin.spec

import kotlin.browser.document
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.links.SentenceFinder
import org.jetbrains.kotlin.spec.links.SpecPlaceHighlighter
import org.jetbrains.kotlin.spec.tests.NavigationType
import org.jetbrains.kotlin.spec.tests.SpecTestsLoader
import org.jetbrains.kotlin.spec.tests.SpecTestsViewer
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoaderType
import org.jetbrains.kotlin.spec.utils.format
import org.jetbrains.kotlin.spec.utils.searchMap
import kotlin.browser.localStorage
import kotlin.browser.window

fun runAfterDocumentReady() {
    val specTestsLoader = SpecTestsLoader(GithubTestsLoaderType.USING_TESTS_MAP_FILE)
    val specTestsViewer = SpecTestsViewer()
    val shouldBeShowedMarkup = localStorage.getItem("showMarkup") != null
    val sentenceToBeHighlighted = window.location.searchMap["sentence"]
    val paragraphToBeHighlighted = window.location.searchMap["paragraph"]
    val sectionToBeHighlighted = window.location.hash

    `$`("h3, h4, h5").each { _, el ->
        SpecTestsLoader.insertLoadIcon(`$`(el))
    }

    if (shouldBeShowedMarkup) {
        SpecTestsLoader.showMarkup()
    }

    if (sentenceToBeHighlighted != null) {
        SpecPlaceHighlighter.highlightSentence(sentenceToBeHighlighted)
    }

    if (paragraphToBeHighlighted != null) {
        SpecPlaceHighlighter.highlightParagraph(paragraphToBeHighlighted)
    }

    if (sectionToBeHighlighted.isNotEmpty()) {
        SpecPlaceHighlighter.highlightSection(sectionToBeHighlighted.substring(1))
    }

    document.body?.let { `$`(it) }?.run {
        on("click", ".sentence.covered") { e, _ ->
            specTestsViewer.showViewer(`$`(e.currentTarget))
        }
        on("change", ".test-coverage-view select[name='test-area']") { e, _ ->
            specTestsViewer.onTestAreaChange()
        }
        on("change", ".test-coverage-view select[name='test-type']") { e, _ ->
            specTestsViewer.onTestTypeChange()
        }
        on("change", ".test-coverage-view select[name='test-number']") { e, _ ->
            specTestsViewer.onTestNumberChange()
        }
        on("click", ".prev-testcase") { e, _ ->
            specTestsViewer.navigateTestCase(`$`(e.currentTarget), NavigationType.PREV)
            false
        }
        on("click", ".next-testcase") { e, _ ->
            specTestsViewer.navigateTestCase(`$`(e.currentTarget), NavigationType.NEXT)
            false
        }
        on("click", ".load-tests") { e, _ ->
            specTestsLoader.onTestsLoadingLinkClick(`$`(e.currentTarget))
            false
        }
        on("click", ".set-branch") { _, _ ->
            SpecTestsLoader.onSetBranchIconClick()
            false
        }
        on("click", ".number-info[data-path]") { e, _ ->
            SpecPlaceHighlighter.onSentenceGetLinkClick(`$`(e.currentTarget))
            e.stopPropagation()
            false
        }
        on("click", ".paragraph-link") { e, _ ->
            SpecPlaceHighlighter.onParagraphGetLinkClick(`$`(e.currentTarget))
            false
        }
        on("click", ".loaded-tests") { _, _ -> false }

        prepend(SentenceFinder.FINDER_BAR_HTML.format(
                *(if (shouldBeShowedMarkup) arrayOf("hide", "Hide") else arrayOf("show", "Show"))
        ))

        on("click", ".spec-sentence-find") { _, _ -> SentenceFinder.findSentence() }
        on("keyup", ".spec-location-search input[name=\"spec-sentence-location\"]") { e, _ ->
            if (e.keyCode == 13) {
                SentenceFinder.findSentence()
            }
        }

        mutableMapOf(18 to false, 69 to false).let { keys ->
            `$`(document).keydown{ e ->
                keys[e.keyCode.toInt()] = true
                true
            }.keyup { e ->
                if (keys[18] == true && keys[69] == true) {
                    `$`(".spec-location-search input[name=\"spec-sentence-location\"]").focus().select()
                }
                keys[e.keyCode.toInt()] = false
                true
            }
        }

        on("click", ".show-markup-link") { _, _ ->
            localStorage.setItem("showMarkup", "true")
            SpecTestsLoader.showMarkup()
            false
        }

        on("click", ".hide-markup-link") { _, _ ->
            localStorage.removeItem("showMarkup")
            window.location.reload()
            false
        }

        on("click", "h2, h3, h4, h5") { e, _ ->
            SpecPlaceHighlighter.onHeaderGetLinkClick(`$`(e.currentTarget))
        }
    }
}

fun main() {
    `$`(document).ready { runAfterDocumentReady() }
}
