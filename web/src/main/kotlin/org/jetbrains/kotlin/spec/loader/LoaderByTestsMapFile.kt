package org.jetbrains.kotlin.spec.loader

import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.kotlin.spec.entity.SpecSection
import org.jetbrains.kotlin.spec.entity.TestsLoadingInfo
import org.jetbrains.kotlin.spec.entity.test.SpecTest
import org.jetbrains.kotlin.spec.entity.test.TestPlace
import org.jetbrains.kotlin.spec.entity.test.parameters.TestInfo
import org.jetbrains.kotlin.spec.entity.test.parameters.TestType
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.TestArea
import org.jetbrains.kotlin.spec.loader.GithubTestsLoader.Companion.loadSectionsMapFileFromRawGithub
import org.jetbrains.kotlin.spec.loader.GithubTestsLoader.Companion.loadTestFileFromRawGithub
import org.jetbrains.kotlin.spec.loader.GithubTestsLoader.Companion.loadTestMapFileFromRawGithub
import org.jetbrains.kotlin.spec.loader.GithubTestsLoader.Companion.testAreasToLoad
import kotlin.js.Promise


class LoaderByTestsMapFile : GithubTestsLoader {

    private fun loadTestsMapFile(mainSectionName: String, sectionsPath: String, sectionsMapByTestArea: Map<TestArea, TestsLoadingInfo.Sections>
    ): Promise<Map<TestArea, TestsLoadingInfo.Tests>> {
        return loadTestMapFileFromRawGithub(
                mainSectionName = mainSectionName,
                path = sectionsPath,
                testType = GithubTestsLoader.TestOrigin.SPEC_TEST,
                sectionsMapByTestArea = sectionsMapByTestArea
        )
    }

    private fun loadSectionsMapFile() = loadSectionsMapFileFromRawGithub()


    private fun getPromisesForTestFilesFromTestMap(testsMap_: TestsLoadingInfo.Tests?, testArea: TestArea): Array<Promise<SpecTest>> {
        val promises = mutableListOf<Promise<SpecTest>>()
        val testsMap = testsMap_?.json ?: return promises.toTypedArray()

        for ((paragraph, testsByParagraphs) in testsMap.jsonObject) {
            for ((testType, testsByTypes) in testsByParagraphs.jsonObject) {
                for ((testSentence, testsBySentences) in testsByTypes.jsonObject) {
                    testsBySentences.jsonArray.forEachIndexed { i, testInfo ->
                        val testFilePath = testInfo.jsonObject["path"]?.jsonPrimitive?.content ?: return@forEachIndexed
                        val testElementInfo = TestInfo(testInfo.jsonObject, i + 1)
                        val testPath = TestPlace(paragraph.toInt(), TestType.getByShortName(testType), testSentence.toInt())
                        promises.add(loadTestFileFromRawGithub(testFilePath, testElementInfo, testPath, testArea))
                    }
                }
            }
        }
        return promises.toTypedArray()
    }


    override fun loadTestFiles(sectionToLoadName: String,
                               mainSectionPath: String,
                               sectionsPath: List<String>,
                               sectionsMapsByTestArea: Map<TestArea, TestsLoadingInfo.Sections>
    ): Promise<SpecSection> = loadTestsMapFile(mainSectionName = mainSectionPath,
            sectionsPath = when {
                mainSectionPath == sectionToLoadName && sectionsPath.isEmpty() -> ""
                sectionsPath.isNotEmpty() -> sectionsPath.joinToString("/") + "/" + sectionToLoadName
                else -> sectionToLoadName
            },
            sectionsMapByTestArea = sectionsMapsByTestArea)
            .then { testsMapsByTestArea ->
                val resultMap = mutableMapOf<TestArea, List<SpecTest>>()
                Promise.all(
                    testAreasToLoad.map { ta ->
                        getPromiseForTests(ta, testsMapsByTestArea, resultMap)
                    }.toTypedArray()
                ).then { SpecSection(resultMap) }
            }.then { it }

    fun loadSectionsMapFiles() = loadSectionsMapFile()

    private fun getPromiseForTests(
            testArea: TestArea,
            testMaps: Map<TestArea, TestsLoadingInfo.Tests>,
            mapOfTests: MutableMap<TestArea, List<SpecTest>>
    ): Promise<Unit> = Promise.all(
            getPromisesForTestFilesFromTestMap(testMaps[testArea], testArea))
            .then { mapOfTests[testArea] = it.toList() }
}