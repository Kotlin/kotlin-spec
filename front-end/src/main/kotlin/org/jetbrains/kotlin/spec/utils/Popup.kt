package org.jetbrains.kotlin.spec.utils

import js.externals.jquery.`$`
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.round

data class PopupConfig(
        val width: Int,
        val height: Int,
        val title: String,
        val content: String,
        val maxHeight: Int? = null
) {
    val optimizedWidth get() = `$`(document.body!!).run {
        width().toInt().let { bodyWidth -> if (bodyWidth - 50 < width) bodyWidth - 50 else width }
    }
}

private data class PopupSelectors(
        val main: String,
        val body: String,
        val close: String,
        val title: String,
        val shadow: String
)

private data class PopupTemplates(
        private val wrap: String,
        private val title: String,
        private val close: String,
        private val body: String
) {
    fun getSpecializedTemplate(title: String, content: String, width: Int, height: Int) =
            wrap.format(width, this.title.format(close, title), body.format(height, content))
}

class Popup(private val config: PopupConfig) {
    companion object {
        private const val TITLE_HEIGHT = 29
        private const val OFFSET_AROUND_BODY = 50

        private val selectors = PopupSelectors(
            main = ".box",
            body = ".box .body",
            close = ".box .title .close",
            title = ".box .title .text",
            shadow = ".box_shadow"
        )
        private val templates = PopupTemplates(
            wrap = "<div class=\"box_shadow\"></div><div class=\"box\" style=\"width:{1}px;opacity:0.0\">{2}{3}</div>",
            title = "<div class=\"title\">{1}<div class=\"text\">{2}</div></div>",
            close = "<a href=\"#\" class=\"close\"></a>",
            body = "<div class=\"body\" style=\"min-height:{1}px\">{2}</div>"
        )
    }

    init {
        setHandlers()
    }

    fun open() {
        `$`(selectors.main).remove()
        `$`(document.body!!).append(
                templates.getSpecializedTemplate(config.title, config.content, config.optimizedWidth, config.height - TITLE_HEIGHT)
        )
        computeSizes { `$`(".box").fadeTo(500, 1.0) }
    }

    fun computeSizes(callback: (() -> Unit)? = null) {
        val windowHeight = `$`(window).height().toInt()
        val windowWidth = `$`(window).width().toInt()
        val maxHeight = when {
            config.maxHeight == null || config.maxHeight > windowHeight - OFFSET_AROUND_BODY -> windowHeight - OFFSET_AROUND_BODY
            else -> config.maxHeight
        }
        val ownHeight = `$`(selectors.main).height().toFloat()
        val ownWidth = `$`(selectors.main).width().toFloat()

        `$`(selectors.main).css("maxHeight",  "`${maxHeight}px")
        `$`(selectors.body).css("maxHeight", "${maxHeight - TITLE_HEIGHT}px")
        `$`(selectors.main).css("top", "${round((windowHeight - ownHeight) / 2)}px")
        `$`(selectors.main).css("left", "${round((windowWidth - ownWidth) / 2)}px")

        callback?.invoke()
    }

    private fun close() {
        `$`(selectors.main).remove()
        `$`(selectors.shadow).remove()
    }

    private fun setHandlers() {
        `$`(document.body!!).run {
            on("click", selectors.close) { _, _ -> close(); false }
            on("click", selectors.shadow) { _, _ -> close(); false }
        }
        `$`(window).on("resize") { _, _ -> computeSizes() }
    }
}
