package org.jetbrains.kotlin.spec.tests.loaders

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.loadFileFromRawGithub
import org.jetbrains.kotlin.spec.utils.format
import kotlin.js.Promise


class LoaderByTestsMapFile: GithubTestsLoader {
    private val testsMapFilename = "testsMap.json"

    private fun loadTestsMapFile(sectionsPath: String) =
            loadFileFromRawGithub("$sectionsPath/$testsMapFilename", null, GithubTestsLoader.TestFileType.SPEC_TEST)

    private fun parseTestsMapFile(testsMapText: String) =
            Json(JsonConfiguration.Stable).parseJson(testsMapText)

    private fun getPromisesForTestFiles(
            testsMap: JsonElement,
            sectionsPath: List<String>,
            sectionName: String
    ): Array<Promise<Map<String, Any>>> {
        val promises =
                mutableListOf<Promise<Map<String, Any>>>()

        for ((paragraph, testsByParagraphs) in testsMap.jsonObject) {
            for ((testType, testsByTypes) in testsByParagraphs.jsonObject) {
                for ((testSentence, testsBySentences) in testsByTypes.jsonObject) {
                    testsBySentences.jsonArray.forEachIndexed() { i, testInfo ->
                        val testFileType = when (testInfo.jsonObject["path"]) {
                            null -> GithubTestsLoader.TestFileType.SPEC_TEST
                            else -> GithubTestsLoader.TestFileType.IMPLEMENTATION_TEST
                        }
                        val testPathInSpec = "{1}.{2}.p-{3}.{4}.{5}.{6}"
                                .format(sectionsPath.joinToString("."), sectionName, paragraph, testType, testSentence, i + 1)
                        val testFilePath = testInfo.jsonObject["path"]?.primitive?.content
                                ?: "{1}/{2}/p-{3}/{4}/{5}.{6}.kt"
                                        .format(sectionsPath.joinToString("/"), sectionName, paragraph, testType, testSentence, i + 1)

                        promises.add(loadFileFromRawGithub(testFilePath, testPathInSpec, testFileType, mapOf(
                                "testInfo" to testInfo.jsonObject
                        )))
                    }
                }
            }
        }

        return promises.toTypedArray()
    }

    override fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, Any>>>> {
        return loadTestsMapFile(sectionsPath.joinToString("/") + "/" + sectionName)
                .then { testsMapText ->
                    val testsMap = parseTestsMapFile(testsMapText["content"]!!.toString())
                    val testFilesPromises = getPromisesForTestFiles(testsMap, sectionsPath, sectionName)

                    Promise.all(testFilesPromises)
                }
    }
}