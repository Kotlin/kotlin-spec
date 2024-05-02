package org.jetbrains.kotlin.spec.entity.test.parameters

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/** contains all options which could be defined in testMap.json's tests */
private enum class TestElementKey(val value: String) {
    SPEC_VERSION("specVersion"),
    CASES_NUMBER("casesNumber"),
    DESCRIPTION("description"),
    PATH("path"),
    UNEXPECTED_BEHAVIOUR("unexpectedBehaviour"),
    LINK_TYPE("linkType"),
    HELPERS("helpers");

}

/** contains parsed info of test values declared in json test element */
class TestInfo(jsonSpecTestInfo: JsonObject, val testNumber: Int) {
    val specVersion: String
    val casesNumber: Int
    val description: String
    var path: String
    val unexpectedBehaviour: Boolean
    val linkType: LinkType
    val helpers: Set<String>


    init {
        fun parse(testElementKey: TestElementKey) = jsonSpecTestInfo[testElementKey.value]?.jsonPrimitive?.content
        specVersion = parse(TestElementKey.SPEC_VERSION) ?: ""
        casesNumber = parse(TestElementKey.CASES_NUMBER)?.toInt() ?: 1
        description = parse(TestElementKey.DESCRIPTION) ?: ""
        path = parse(TestElementKey.PATH) ?: ""
        unexpectedBehaviour = parse(TestElementKey.UNEXPECTED_BEHAVIOUR)?.toBoolean() ?: false
        linkType = parse(TestElementKey.LINK_TYPE)?.let { LinkType.valueOf(it) } ?: LinkType.main
        helpers = parse(TestElementKey.HELPERS)?.split(",")?.map(String::trim)?.toSet() ?: emptySet()
    }
}
