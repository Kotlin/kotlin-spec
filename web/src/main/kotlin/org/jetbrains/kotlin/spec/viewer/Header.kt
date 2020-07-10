package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.utils.Mode
import org.jetbrains.kotlin.spec.utils.format
import org.jetbrains.kotlin.spec.viewer.Sidebar.ICON_BAR_HTML
import org.jetbrains.kotlin.spec.viewer.links.SentenceFinder
import kotlin.browser.document

object Header {
    private const val HEADER_HTML = """<div class="header-bar"> {1} {2} {3} {4} </div>"""


    private val MAIN_PAGE_LINK_HTML =
            """<a class=main-page href="http://kotlinlang.org" class="main-page">
                <img src="https://kotlinlang.org/assets/images/favicon.ico" width="30" height="30">
                Kotlin
             </a>""".trimMargin()


    private const val PDF_ICON = "./resources/images/pdf.png"
    private const val DOWNLOAD_FULL_PDF_HTML =
            """<a href="./pdf/kotlin-spec.pdf" target="_blank" class="download-full-pdf">
                  <img src="$PDF_ICON" class="full-pdf-icon">
                   </a>"""


    fun init(mode: Mode, shouldBeShowedMarkup: Boolean) {
        val headerHtml = HEADER_HTML.format(ICON_BAR_HTML, MAIN_PAGE_LINK_HTML, DOWNLOAD_FULL_PDF_HTML,
                when (mode) {
                    Mode.Dev -> SentenceFinder.FINDER_BAR_HTML.format(*(if (shouldBeShowedMarkup) arrayOf("hide", "Hide") else arrayOf("show", "Show")))
                    Mode.User -> ""
                }
        ).trimIndent()

        `$`(document.body ?: return).prepend(headerHtml)
    }
}



