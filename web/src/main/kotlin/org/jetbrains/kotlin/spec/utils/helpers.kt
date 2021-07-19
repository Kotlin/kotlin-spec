package org.jetbrains.kotlin.spec.utils

import org.w3c.dom.Location
import org.w3c.dom.get
import kotlinx.browser.window

fun String.format(vararg args: Any): String {
    return this.replace(Regex("""\{(\d+)}""", RegexOption.MULTILINE)) {
        val number = it.groupValues[1].toInt()

        if (args.size >= number) args[number - 1].toString() else ""
    }
}

fun String.escapeHtml(): String {
    return this.replace(Regex("&", RegexOption.MULTILINE), "&amp;")
            .replace(Regex("<", RegexOption.MULTILINE), "&lt;")
            .replace(Regex(">"), "&gt;")
            .replace(Regex("\"", RegexOption.MULTILINE), "&quot;")
            .replace(Regex("'", RegexOption.MULTILINE), "&#039;")
}


val Location.searchMap: MutableMap<String, String>
    get() {
        val rawSearch = search.substring(1).split("&")
        val objURL = mutableMapOf<String, String>()

        rawSearch.forEach { param ->
            val paramComponents = param.split("=")
            if (paramComponents.size != 2) return@forEach
            objURL[paramComponents[0]] = paramComponents[1]
        }

        return objURL
    }

val isDevMode = window.localStorage["isDevMode"] != null || window.location.searchMap["mode"] == "dev"

val shouldBeShowedMarkup = window.localStorage["showMarkup"] != null

val sentenceToBeHighlighted = window.location.searchMap["sentence"]

val paragraphToBeHighlighted = window.location.searchMap["paragraph"]

val sectionToBeHighlighted = window.location.hash.takeIf { it.isNotEmpty() }?.substring(1)