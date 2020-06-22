package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.JQuery
import js.externals.jquery.`$`
import kotlin.browser.document
import kotlin.browser.window

object Sidebar {
    private const val TOC = "#TOC"
    private const val OFFSET_BEFORE_SCROLLED_ELEMENT = 100

    private fun expandItemsHierarchy(sectionId: String) {
        val escapedSectionId = sectionId.replace(".", "\\.")
        val sectionMenuItem = `$`("#toc-element-$escapedSectionId")

        // Clean previously set classes on another elements
        `$`(".toc-element").removeClass("active")

        sectionMenuItem.addClass("active").addClass("toggled")
        sectionMenuItem.next("ul").find("> li").show()

        sectionMenuItem.parents("$TOC ul").find("> li").show()
        sectionMenuItem.parents("li").find(" > a").addClass("toggled")
    }

    private fun expandItemsHierarchyByUrl(shouldScrollToItem: Boolean = false) {
        val sectionIdFromHash = window.location.hash.removePrefix("#")
        val sectionIdFromPath = window.location.pathname.split("/").last().removeSuffix(".html")

        expandItemsHierarchy(if (sectionIdFromHash.isNotBlank()) sectionIdFromHash else sectionIdFromPath)

        if (shouldScrollToItem) {
            val tocSelector = `$`(TOC)
            tocSelector.scrollTop(
                    tocSelector.scrollTop().toInt() - tocSelector.offset().top.toInt() + `$`("$TOC .active").offset().top.toInt() - OFFSET_BEFORE_SCROLLED_ELEMENT
            )
        }
    }

    private fun toggleItem(element: JQuery) {
        element.find("> li").toggle()
        element.parent("li").find("> a").toggleClass("toggled")
    }

    private fun showSidebar() {
        `$`(TOC).toggleClass("active")
        `$`(".icon-menu").toggleClass("active")
    }

    fun init() {
        expandItemsHierarchyByUrl(shouldScrollToItem = true)

        `$`(window).on("hashchange") { _, _ -> expandItemsHierarchyByUrl() }
        `$`("$TOC > ul > li, #TOC > ul > li > ul > li").show() // show "Kotlin core" subsections by default
        `$`(TOC).addClass("loaded")
        `$`("$TOC > ul > li ul").on("click") { e, _ ->
            e.stopPropagation()
            val target = `$`(e.target)
            if (e.offsetY.toInt() < 0 && !target.`is`("#TOC > ul > li > ul")) {
                toggleItem(`$`(e.target))
            }
        }

        `$`(document.body ?: return).prepend("""
            <div class="icon-menu">
                <span class="divide"></span>
                <span class="divide"></span>
                <span class="divide"></span>
            </div>
        """.trimIndent())

        `$`(".icon-menu").on("click") { _, _ -> showSidebar() }
    }
}
