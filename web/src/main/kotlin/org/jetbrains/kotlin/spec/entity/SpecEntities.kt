package org.jetbrains.kotlin.spec.entity

import org.jetbrains.kotlin.spec.entity.test.SpecTest
import org.jetbrains.kotlin.spec.entity.test.parameters.LinkType
import org.jetbrains.kotlin.spec.entity.test.parameters.TestType
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.TestArea

private interface SpecEntity {
    val specTests: Map<TestArea, List<SpecTest>>
}

class SpecSection(override val specTests: Map<TestArea, List<SpecTest>>) : SpecEntity

class SpecParagraph(specSection: SpecSection, val paragraph: Int) : SpecEntity {
    override val specTests: Map<TestArea, List<SpecTest>>

    init {
        specTests = specSection.specTests.mapValues {
            it.value.filter { test -> test.testPlace.paragraph == paragraph }
        }
    }
}

class SpecSentence(specParagraph: SpecParagraph, sentence: Int) : SpecEntity {
    override val specTests: Map<TestArea, List<SpecTest>>

    init {
        specTests = specParagraph.specTests.mapValues {
            it.value.filter { test -> test.testPlace.sentence == sentence }
        }
    }

    fun getTests(testArea: TestArea): List<SpecTest>? {
        val tests = specTests[testArea]
        return if (tests.isNullOrEmpty()) null else tests
    }

    fun getTests(testArea: TestArea, testType: TestType): List<SpecTest>? {
        val tests = getTests(testArea)?.filter { test -> test.testPlace.testType == testType }
        return if (tests.isNullOrEmpty()) null else tests
    }

    fun getTests(testArea: TestArea, testType: TestType, linkType: LinkType): List<SpecTest>? {
        val tests = getTests(testArea, testType)?.filter { test -> test.testInfo.linkType == linkType }
        return if (tests.isNullOrEmpty()) null else tests
    }

    fun getTest(testArea: TestArea, testType: TestType, linkType: LinkType, testNumber: Int) =
            getTests(testArea, testType, linkType)?.find { it.testInfo.testNumber == testNumber }

}

