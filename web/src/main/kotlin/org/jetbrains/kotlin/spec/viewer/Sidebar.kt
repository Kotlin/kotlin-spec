package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.JQuery
import js.externals.jquery.`$`
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window

object Sidebar {
    private const val LOAD_PDF_ICON = "./resources/images/pdf.png"
    private const val TOC = "#TOC"
    private const val TOC_BAR =".toc-bar"
    private const val TOC_BODY =".toc-body"
    private const val OFFSET_BEFORE_SCROLLED_ELEMENT = 100

    private fun expandItemsHierarchy(sectionMenuItem: JQuery) {
        if (sectionMenuItem.length == 0) return

        // Clean previously set classes on another elements
        `$`(".toc-element").removeClass("active")

        sectionMenuItem.addClass("active").addClass("toggled")
        sectionMenuItem.next("ul").find("> li").show()

        sectionMenuItem.parents("$TOC ul").find("> li").show()
        sectionMenuItem.parents("li").find(" > a").addClass("toggled")
    }

    private fun expandItemsHierarchy(sectionId: String) {
        val escapedSectionId = sectionId.replace(".", "\\.")
        val sectionMenuItem = `$`("#toc-element-$escapedSectionId")

        expandItemsHierarchy(sectionMenuItem)
    }

    private fun expandItemsHierarchyByUrl(shouldScrollToItem: Boolean = true) {
        val sectionIdFromHash = window.location.hash.removePrefix("#")
        val sectionIdFromPath = window.location.pathname.split("/").last().removeSuffix(".html")

        expandItemsHierarchy(if (sectionIdFromHash.isNotBlank()) sectionIdFromHash else sectionIdFromPath)

        if (shouldScrollToItem) {
            scrollToActiveItem()
        }
    }

    private fun scrollToActiveItem() {
        if (`$`("$TOC_BAR .active").length != 0) {
            val tocSelector = `$`(TOC_BODY)
            tocSelector.scrollTop(
                    tocSelector.scrollTop().toInt() - tocSelector.offset().top.toInt() + `$`("$TOC_BAR .active").offset().top.toInt() - OFFSET_BEFORE_SCROLLED_ELEMENT
            )
        }
    }

    private fun toggleItem(element: JQuery) {
        element.find("> li").toggle()
        element.parent("li").find("> a").toggleClass("toggled")
    }

    private fun showSidebar() {
        `$`(TOC_BAR).toggleClass("active")
        `$`(".icon-menu").toggleClass("active")
    }

    private fun setScrollSettings() {
        `$`(window).scroll {
            val x = (`$`(document).scrollTop().toDouble())
            val y = maxOf(-61.0, -x)
            `$`(TOC_BAR).css("margin-top", y)
            `$`(".toc-body").css("height", "-moz-calc(100% - ${161 + y}px)")
                    .css("height", "-webkit-calc(100% - ${161 + y}px)")
                    .css("height", "calc(100% - ${161 + y}px)")
        }
    }

    fun init() {
        // wrap generated by pandoc TOC into .toc-bar.toc-body element
        `$`(TOC_BODY).removeClass("empty")
        `$`(TOC).appendTo(TOC_BODY)

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

        document.body?.let { `$`(it) }?.run {
            on("click", ".icon-menu") { _, _ -> showSidebar() }
            on("click", ".toc-element") { _, _ -> hideSidebar() }
        }
        installSearchBar()
        setScrollSettings()
        addPdfLinks()
    }

    private fun hideSidebar() {
        `$`(".icon-menu").removeClass("active")
    }

    private var currSearchString = ""
    private var currResultIdx = 0


    private fun installSearchBar() {
        val searchBar = `$`("#toc-search-bar")[0] ?: return

        searchBar.addEventListener("keyup", callback = cb@{ e ->
            // If key isn't "Enter" then return
            if ((e as? KeyboardEvent)?.which != 13) return@cb

            val searchString = (e.target as? HTMLInputElement)?.value ?: return@cb

            if (searchString.isBlank()) return@cb

            val foundItem = `$`("$TOC .toc-element:contains($searchString)")

            if (foundItem.length == 0) return@cb

            if (currSearchString != searchString) {
                currSearchString = searchString
                currResultIdx = -1
            }

            currResultIdx += 1
            currResultIdx %= foundItem.length.toInt()

            val currItem = foundItem.eq(currResultIdx)

            expandItemsHierarchy(currItem)
            scrollToActiveItem()
        })
    }


    private fun addPdfLinks() {
        `$`("$TOC > ul > li > ul > li").each { _, el ->
            val sectionName = `$`(el).find("> a").attr("href").substringBefore(".html")
            `$`(el).prepend("<a href=\"./pdf/sections/$sectionName.pdf\" target=\"_blank\" class=\"download-section-as-pdf\" title=\"Download section as pdf\"></a>")
        }

        `$`("h2").each { _, el ->
            val sectionName = `$`(el).attr("id")
            `$`(el).after("<a href=\"./pdf/sections/$sectionName.pdf\" target=\"_blank\" class=\"download-section-as-pdf-text-link\" title=\"Download section as pdf\">"
                    + """
<img src="${LOAD_PDF_ICON}" />""".trimIndent() + "</a>")
        }
    }
}
