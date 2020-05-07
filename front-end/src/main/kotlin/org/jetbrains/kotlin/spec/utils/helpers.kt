package org.jetbrains.kotlin.spec.utils

import org.w3c.dom.Location

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
