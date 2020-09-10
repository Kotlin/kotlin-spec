package org.jetbrains.kotlin.spec.entity

import kotlinx.serialization.json.JsonElement

sealed class TestsLoadingInfo() {
    class Tests(val json: JsonElement)
    class Sections(val json: JsonElement)
}