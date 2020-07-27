package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.utils.format
import org.jetbrains.kotlin.spec.utils.isDevMode
import org.jetbrains.kotlin.spec.utils.shouldBeShowedMarkup
import org.jetbrains.kotlin.spec.viewer.links.SentenceFinder

object Header {
    private const val HEADER_BAR = ".header-bar";

    fun init() {
        if (isDevMode)
            `$`(SentenceFinder.FINDER_BAR_HTML.format(*(if (shouldBeShowedMarkup) arrayOf("hide", "Hide") else arrayOf("show", "Show")))).appendTo(HEADER_BAR)
    }
}



