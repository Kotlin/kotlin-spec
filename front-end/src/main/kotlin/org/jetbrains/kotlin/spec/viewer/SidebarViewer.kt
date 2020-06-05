package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.JQuery
import js.externals.jquery.JQueryEventObject
import js.externals.jquery.`$`
import kotlin.browser.document


class SidebarViewer {
    companion object {
        private const val RIGHT_ARROW_ICON = "./resources/images/play-arrow.png"
        private const val DOWN_ARROW_ICON = "./resources/images/down-arrow.png"

        private fun insertArrowIcon(element: JQuery) {
            element.toggleClass("unrolled")
            element.append("""<img src="$RIGHT_ARROW_ICON" class="nav-icon">""")

        }

        fun slideParagraphs(element: JQuery, e: JQueryEventObject) {
            e.preventDefault()
            element.parent().parent().children("ul:first").slideToggle()
            val img = element.children("img.nav-icon")
            val src = img.attr("src")
            if (src.contains(SidebarViewer.RIGHT_ARROW_ICON))
                img.attr("src", img.attr("src").replace(RIGHT_ARROW_ICON, DOWN_ARROW_ICON))
            else
                img.attr("src", img.attr("src").replace(DOWN_ARROW_ICON, RIGHT_ARROW_ICON))
        }

        fun turnOnBar() {
            val element = document.getElementById("TOC")
            if (element != null) `$`(element).toggleClass("active");
        }

        fun prepare() {

            `$`("#TOC ul li ul li a").parent().children("ul").hide()

            `$`("#TOC ul li ul li a span").each { index, element ->
                `$`(element).parent().parent().has("ul").children("a").children("span")
                        .css("visibility", "visible")
            }

            `$`(".toc-section-number").each { index, element ->
                insertArrowIcon(`$`(element))
            }

        }

    }
}