package org.jetbrains.kotlin.spec.tests.loaders

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.loadFileFromRawGithub
import org.jetbrains.kotlin.spec.utils.format
import kotlin.js.Promise


class LoaderByTestsMapFile: GithubTestsLoader {
    private val testsMapFilename = "testsMap.json"

    private fun loadTestsMapFile(sectionsPath: String): Promise<Map<String, String>> {
        return loadFileFromRawGithub("$sectionsPath/$testsMapFilename")
    }

    private fun parseTestsMapFile(testsMapText: String) =
            Json(JsonConfiguration.Stable).parseJson(testsMapText)

    private fun getPromisesForTestFiles(
            testsMap: JsonElement,
            sectionsPath: List<String>,
            sectionName: String
    ): Array<Promise<Map<String, String>>> {
        val promises = mutableListOf<Promise<Map<String, String>>>()

        for ((paragraph, testsByParagraphs) in testsMap.jsonObject) {
            for ((testType, testsByTypes) in testsByParagraphs.jsonObject) {
                for ((testSentence, testsBySentences) in testsByTypes.jsonObject) {
                    testsBySentences.jsonArray.forEachIndexed() { i, _ ->
                        val testPath = "{1}/{2}/p-{3}/{4}/{5}.{6}.kt".format(
                                sectionsPath.joinToString("/"),
                                sectionName,
                                paragraph,
                                testType,
                                testSentence,
                                i + 1
                        )
                        promises.add(loadFileFromRawGithub(testPath))
                    }
                }
            }
        }

        return promises.toTypedArray()
    }

    override fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, String>>>> {
        return loadTestsMapFile(sectionsPath.joinToString("/") + "/" + sectionName)
                .then { testsMapText ->
                    val testsMap = parseTestsMapFile(testsMapText["content"]!!)
                    val testFilesPromises = getPromisesForTestFiles(testsMap, sectionsPath, sectionName)
                    Promise.all(testFilesPromises)
                }
    }
}