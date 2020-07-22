package org.jetbrains.kotlin.spec.entity.test

import org.jetbrains.kotlin.spec.entity.test.parameters.TestInfo
import org.jetbrains.kotlin.spec.entity.test.parameters.TestType
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.TestArea

class SpecTest(val testInfo: TestInfo, val testPlace: TestPlace, content: String, testArea: TestArea) {
    val testCases: List<TestCase> = testArea.parseTest(content)
}

class TestCase(val code: String, val infoElements: Map<String, String?>? = null)
class TestPlace(val paragraph: Int, val testType: TestType, val sentence: Int)


