package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.JQuery
import js.externals.jquery.JQueryEventObject
import js.externals.jquery.`$`
import kotlin.browser.document


class SidebarViewer {
    companion object {
        private const val RIGHT_ARROW_ICON = "./resources/images/play-arrow.png"
        private const val DOWN_ARROW_ICON = "./resources/images/down-arrow.png"

        private const val TOC_CORE_SECTIONS = "#TOC ul li ul li a"

        private fun insertArrowIcon(element: JQuery) {
            `$`(element)
                    .parent().parent().has("ul")
                    .children("a").children("span")
                    .append("""<img src="$RIGHT_ARROW_ICON" class="nav-icon">""")
        }

        private fun hideAllSubsections() {
            `$`(TOC_CORE_SECTIONS).parent().children("ul").hide()
        }

        fun slideParagraphs(element: JQuery, e: JQueryEventObject) {
            e.preventDefault()
            element.parent().parent().children("ul:first").slideToggle()
            val img = element.children("img.nav-icon")
            val src = img.attr("src")
            if (src.contains(RIGHT_ARROW_ICON))
                img.attr("src", img.attr("src").replace(RIGHT_ARROW_ICON, DOWN_ARROW_ICON))
            else
                img.attr("src", img.attr("src").replace(DOWN_ARROW_ICON, RIGHT_ARROW_ICON))
        }

        fun turnOnBar() {
            val element = document.getElementById("TOC")
            if (element != null) `$`(element).toggleClass("active")
        }

        fun prepare() {
            `$`(TOC_CORE_SECTIONS).each { _, e ->
                val tocSection = document.createElement("span")
                tocSection.classList.add("toc-section")
                e.prepend(tocSection)
                insertArrowIcon(`$`(tocSection))
            }
            hideAllSubsections()
        }
    }
}