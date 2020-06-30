package org.jetbrains.kotlin.spec.loader

import org.jetbrains.kotlin.spec.entity.SectionTestMap
import org.jetbrains.kotlin.spec.entity.SpecSection
import org.jetbrains.kotlin.spec.entity.test.SpecTest
import org.jetbrains.kotlin.spec.entity.test.TestPlace
import org.jetbrains.kotlin.spec.entity.test.parameters.TestInfo
import org.jetbrains.kotlin.spec.entity.test.parameters.TestType
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.TestArea
import org.jetbrains.kotlin.spec.loader.GithubTestsLoader.Companion.loadTestFileFromRawGithub
import org.jetbrains.kotlin.spec.loader.GithubTestsLoader.Companion.loadTestMapFileFromRawGithub
import kotlin.js.Promise


class LoaderByTestsMapFile : GithubTestsLoader {
    private val testsMapFilename = "testsMap.json"

    private fun loadTestsMapFile(sectionsPath: String, testAreasToLoad: Array<TestArea>
    ) = loadTestMapFileFromRawGithub(
            path = "$sectionsPath/$testsMapFilename",
            testType = GithubTestsLoader.TestOrigin.SPEC_TEST,
            testAreasToLoad = testAreasToLoad.asList()
    )


    private fun getPromisesForTestFilesFromTestMap(testsMapSection: SectionTestMap?, testArea: TestArea): Array<Promise<SpecTest>> {
        val promises = mutableListOf<Promise<SpecTest>>()
        val testsMap = testsMapSection?.sectionTestMap ?: return promises.toTypedArray()

        for ((paragraph, testsByParagraphs) in testsMap.jsonObject) {
            for ((testType, testsByTypes) in testsByParagraphs.jsonObject) {
                for ((testSentence, testsBySentences) in testsByTypes.jsonObject) {
                    testsBySentences.jsonArray.forEachIndexed { i, testInfo ->
                        val testFilePath = testInfo.jsonObject["path"]?.primitive?.content ?: return@forEachIndexed
                        val testElementInfo = TestInfo(testInfo.jsonObject, i + 1)
                        val testPath = TestPlace(paragraph.toInt(), TestType.getByShortName(testType), testSentence.toInt())
                        promises.add(loadTestFileFromRawGithub(testFilePath, testElementInfo, testPath, testArea))
                    }
                }
            }
        }
        return promises.toTypedArray()
    }


    override fun loadTestFiles(sectionName: String, sectionsPath: List<String>, testAreasToLoad: Array<TestArea>
    ) = loadTestsMapFile(sectionsPath.joinToString("/") + "/" + sectionName, testAreasToLoad)
            .then { sectionTestMaps ->
                val resultMap = mutableMapOf<TestArea, List<SpecTest>>()
                Promise.all(testAreasToLoad.asList()
                        .associateWith { getPromiseForTests(it, sectionTestMaps, resultMap) }
                        .values.toTypedArray()
                ).then { SpecSection(resultMap) }
            }


    private fun getPromiseForTests(
            testArea: TestArea,
            sectionTestMaps: Map<TestArea, SectionTestMap>,
            mapOfTests: MutableMap<TestArea, List<SpecTest>>
    ) = Promise.all(
            getPromisesForTestFilesFromTestMap(sectionTestMaps[testArea], testArea))
            .then { mapOfTests[testArea] = it.toList() }
}