package org.jetbrains.kotlin.spec

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

@JsModule("jquery")
@JsNonModule
external fun `$`(selector: dynamic): JQuery

@JsModule("jquery")
@JsNonModule
external interface JQueryCoordinates {
    // var left: Number
    var top: Number
}

@JsModule("jquery")
@JsNonModule
external interface JQueryEventObject {
    fun stopPropagation()
    var currentTarget: Element
    var target: Element
    var keyCode: Number
    // var offsetX: Number
    var offsetY: Number
}

@JsModule("jquery")
@JsNonModule
external interface JQuery {
    fun offset(): JQueryCoordinates
    operator fun get(index: Number): HTMLElement?
    fun ready(handler: (jQueryAlias: Any? /*= null*/) -> Any): JQuery
    fun attr(attributeName: String): String
    fun prop(propertyName: String): Any
    fun contents(): JQuery
    fun addClass(className: String): JQuery
    fun removeClass(className: String): JQuery
    fun hasClass(className: String): Boolean
    fun toggleClass(className: String, swtch: Boolean? = definedExternally /* null */): JQuery
    fun prepend(content1: String, vararg content2: Any): JQuery
    fun append(content1: String, vararg content2: Any): JQuery
    fun appendTo(target: String): JQuery
    fun data(key: String, value: Any): JQuery
    fun data(key: String): Any
    fun data(): Any
    fun get(): Array<HTMLElement>
    fun children(selector: String? = definedExternally /* null */): JQuery
    fun clone(withDataAndEvents: Boolean? = definedExternally /* null */, deepWithDataAndEvents: Boolean? = definedExternally /* null */): JQuery
    fun eq(index: Number): JQuery
    fun each(func: (index: Number, elem: Element) -> Any?): JQuery
    fun empty(): JQuery
    fun filter(func: (index: Number, element: Element) -> Any): JQuery
    fun find(selector: String): JQuery
    fun find(element: Element): JQuery
    fun find(obj: JQuery): JQuery
    fun first(): JQuery
    fun `is`(selector: String): Boolean
    var length: Number
    fun parent(selector: String? = definedExternally /* null */): JQuery
    fun parents(selector: String? = definedExternally /* null */): JQuery
    fun next(selector: String? = definedExternally /* null */): JQuery
    fun nextAll(selector: String? = definedExternally /* null */): JQuery
    fun prevAll(selector: String? = definedExternally /* null */): JQuery
    fun remove(selector: String? = definedExternally /* null */): JQuery
    fun before(content1: String, vararg content2: Any): JQuery
    fun html(htmlString: String): JQuery
    fun css(propertyName: String, value: String): JQuery
    fun css(propertyName: String, value: Number): JQuery
    fun text(): String
    fun focus(): JQuery
    fun select(): JQuery
    fun click(): JQuery
    fun keydown(handler: (eventObject: JQueryEventObject) -> Any): JQuery
    fun keyup(handler: (eventObject: JQueryEventObject) -> Any): JQuery
    fun width(): Number
    fun height(): Number
    fun fadeTo(duration: Number, opacity: Number, easing: String? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): JQuery
    fun show(duration: Number? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): JQuery
    fun toggle(duration: Number? = definedExternally /* null */, complete: Function<*>? = definedExternally /* null */): JQuery
    fun on(events: String, handler: (eventObject: JQueryEventObject, args: Any) -> Any): JQuery
    fun on(events: String, selector: String, handler: (eventObject: JQueryEventObject, eventData: Any) -> Any): JQuery
    fun scroll(handler: (eventObject: JQueryEventObject) -> Any): JQuery
    fun scrollTop(): Number
    fun scrollTop(value: Number): JQuery
    fun `val`(): Any
    fun `val`(value: String): JQuery
}