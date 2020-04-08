package org.jetbrains.kotlin.spec.entity.test.parameters

import kotlinx.serialization.json.JsonObject

/** contains all options which could be defined in testMap.json's tests */
private enum class TestElementKey(val value: String) {
    SPEC_VERSION("specVersion"),
    CASES_NUMBER("casesNumber"),
    DESCRIPTION("description"),
    PATH("path"),
    UNEXPECTED_BEHAVIOUR("unexpectedBehaviour"),
    LINK_TYPE("linkType");

}

/** contains parsed info of test values declared in json test element */
class TestInfo(jsonSpecTestInfo: JsonObject, val testNumber: Int) {
    val specVersion: String
    val casesNumber: Int
    val description: String
    var path: String
    val unexpectedBehaviour: Boolean
    val linkType: LinkType


    init {
        fun parse(testElementKey: TestElementKey) = jsonSpecTestInfo[testElementKey.value]?.primitive?.content
                ?: throw IllegalArgumentException("test element key ${testElementKey.value} is not found")
        specVersion = parse(TestElementKey.SPEC_VERSION)
        casesNumber = parse(TestElementKey.CASES_NUMBER).toInt()
        description = parse(TestElementKey.DESCRIPTION)
        path = parse(TestElementKey.PATH)
        unexpectedBehaviour = parse(TestElementKey.UNEXPECTED_BEHAVIOUR).toBoolean()
        linkType = LinkType.valueOf(parse(TestElementKey.LINK_TYPE))
    }

}