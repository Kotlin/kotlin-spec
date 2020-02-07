package org.jetbrains.kotlin.spec.tests.loaders

import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryXHR
import js.externals.jquery.`$`
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.kotlin.spec.tests.SpecTestsParser
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.LINKED_SPEC_TESTS_FOLDER
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.SPEC_TEST_DATA_PATH
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.getBranch
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoader.Companion.loadFileFromRawGithub
import org.jetbrains.kotlin.spec.utils.TestArea
import org.jetbrains.kotlin.spec.utils.format
import kotlin.js.Promise

interface GitHubApiSection {
    val name: String
    val git_url: String
}

class LoaderByGithubApi: GithubTestsLoader {
    private val githubGetContentsUrl = "https://api.github.com/repos/JetBrains/Kotlin/contents"

    private fun getTestFilesTreeUrl(currentSection: String, sectionsPath: List<String>): Promise<String> {
        val settings = object : JQueryAjaxSettings {
            override var url: String? = "{1}/{2}/{3}/{4}?ref={5}".format(
                    githubGetContentsUrl,
                    SPEC_TEST_DATA_PATH,
                    LINKED_SPEC_TESTS_FOLDER,
                    sectionsPath.joinToString("/"),
                    getBranch()
            )
            override val success: ((data: Any, textStatus: String, jqXHR: JQueryXHR) -> Any)?
                get() = get@ { data, _, _ ->
                    val response = JSON.parse<List<GitHubApiSection>>(data as String)

                    for (section in response) {
                        if (section.name == currentSection) {
                            return@get section.git_url
                        }
                    }
                }
            override val error: ((jqXHR: JQueryXHR, textStatus: String, errorThrown: String) -> Any)?
                get() = { _, _, _ -> Promise.reject(Exception()) }
        }

        return Promise { _, _ ->
            `$`.get(settings)
        }
    }

    private fun getTestFilesTree(url: String): Promise<String> {
        return Promise { _, reject ->
            `$`.get(
                    object : JQueryAjaxSettings {
                        override var url: String? = "$url?recursive=1"
                        override val success: ((data: Any, textStatus: String, jqXHR: JQueryXHR) -> Any)?
                            get() = { data: Any, _, _ -> Promise.resolve(data) }
                        override val error: ((jqXHR: JQueryXHR, textStatus: String, errorThrown: String) -> Any)?
                            get() = { _, _, _ -> reject(Exception()) }
                    }
            )
        }
    }

    private fun getTestFilesPromises(tree: JsonArray, currentSection: String, sectionsPath: List<String>): Array<Promise<Map<String, Any>>> {
        val promises = mutableListOf<Promise<Map<String, Any>>>()

        tree.forEach { testFile ->
            if (SpecTestsParser.testPathStartingParagraphRegexp.matches(testFile.jsonObject[TestArea.DIAGNOSTICS.path]!!.toString())) {
                promises.add(
                        loadFileFromRawGithub(
                                "{1}/{2}/{3}".format(
                                        sectionsPath.joinToString("/"),
                                        currentSection,
                                        testFile.jsonObject[TestArea.DIAGNOSTICS.path]!!.toString()
                                ),
                                null,
                                GithubTestsLoader.TestFileType.SPEC_TEST
                        )
                )
            }
        }

        return promises.toTypedArray()
    }

    override fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, Any>>>> {
        return getTestFilesTreeUrl(sectionName, sectionsPath)
                .then { url -> getTestFilesTree(url) }
                .then { response: String ->
                    Promise.all(
                            getTestFilesPromises(
                                    Json(JsonConfiguration.Stable).parseJson(response).jsonObject["tree"]?.jsonArray!!,
                                    sectionName,
                                    sectionsPath
                            )
                    )
                }
    }
}