package org.jetbrains.kotlin.spec

import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.loader.SpecTestsLoader
import org.jetbrains.kotlin.spec.utils.*
import org.jetbrains.kotlin.spec.viewer.Header
import org.jetbrains.kotlin.spec.viewer.NavigationType
import org.jetbrains.kotlin.spec.viewer.Sidebar
import org.jetbrains.kotlin.spec.viewer.SpecTestsViewer
import org.jetbrains.kotlin.spec.viewer.links.SentenceFinder
import org.jetbrains.kotlin.spec.viewer.links.SpecPlaceHighlighter
import org.w3c.dom.set
import kotlinx.browser.document
import kotlinx.browser.window

fun turnOnPermanentDevModeIfNeeded() {
    if (window.location.searchMap["mode"] == "dev") {
        window.localStorage["isDevMode"] = "true"
    }
}

fun init() {
    turnOnPermanentDevModeIfNeeded()

    `$`(document).ready {
        Sidebar.init()
        Header.init()
    }
    
    `$`("h2, h3, h4, h5").each { _, el ->
        val idValue = `$`(el).attr("id")
        if (idValue !in SpecTestsViewer.excludedSectionsToLoadTests) {
            SpecTestsLoader.insertLoadIcon(`$`(el))
        }
    }

    if (shouldBeShowedMarkup && isDevMode) {
        SpecTestsLoader.showMarkup()
    }

    if (sentenceToBeHighlighted != null) {
        SpecPlaceHighlighter.highlightSentence(sentenceToBeHighlighted)
    }

    if (paragraphToBeHighlighted != null) {
        SpecPlaceHighlighter.highlightParagraph(paragraphToBeHighlighted)
    }

    if (sectionToBeHighlighted != null) {
        SpecPlaceHighlighter.highlightSection(sectionToBeHighlighted)
    }

    document.body?.let { `$`(it) }?.run {
        val specTestsLoader = SpecTestsLoader()
        val specTestsViewer = SpecTestsViewer()

        on("click", ".sentence.covered") { e, _ ->
            specTestsViewer.showViewer(`$`(e.currentTarget))
        }
        on("change", ".test-coverage-view select[name='test-area']") { _, _ ->
            specTestsViewer.onTestAreaChange()
        }
        on("change", ".test-coverage-view select[name='test-type']") { _, _ ->
            specTestsViewer.onTestTypeChange()
        }
        on("change", ".test-coverage-view select[name='test-link-type']") { _, _ ->
            specTestsViewer.onTestPriorityChange()
        }
        on("change", ".test-coverage-view select[name='test-number']") { _, _ ->
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

        on("click", ".spec-sentence-find") { _, _ -> SentenceFinder.findSentence() }
        on("keyup", ".spec-location-search input[name=\"spec-sentence-location\"]") { e, _ ->
            if (e.keyCode == 13) {
                SentenceFinder.findSentence()
            }
        }

        mutableMapOf(18 to false, 69 to false).let { keys ->
            `$`(document).keydown { e ->
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
            window.localStorage["showMarkup"] = "true"
            SpecTestsLoader.showMarkup()
            false
        }

        on("click", ".disable-dev-mode") { _, _ ->
            window.localStorage.removeItem("isDevMode")
            window.location.reload()
            false
        }

        on("click", ".hide-markup-link") { _, _ ->
            window.localStorage.removeItem("showMarkup")
            window.location.reload()
            false
        }

        on("click", "h2, h3, h4, h5") { e, _ ->
            SpecPlaceHighlighter.onHeaderGetLinkClick(`$`(e.currentTarget))
        }
    }

    `$`(document).ready {
        val firstH2Heading = `$`("h2").first()
        if (firstH2Heading.length != 0) {
            val headingValue = firstH2Heading.contents()[0]?.nodeValue
            if (headingValue != null) {
                document.title = "$headingValue - ${document.title}"
            }
        }
        true
    }
}

fun main() = init()
