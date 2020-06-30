package org.jetbrains.kotlin.spec.loader

import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryXHR
import js.externals.jquery.`$`
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.kotlin.spec.entity.SectionTestMap
import org.jetbrains.kotlin.spec.entity.SpecSection
import org.jetbrains.kotlin.spec.entity.test.SpecTest
import org.jetbrains.kotlin.spec.entity.test.TestPlace
import org.jetbrains.kotlin.spec.entity.test.parameters.TestInfo
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.TestArea
import org.jetbrains.kotlin.spec.utils.format
import kotlin.browser.window
import kotlin.js.Promise

interface GithubTestsLoader {
    enum class TestOrigin { SPEC_TEST, IMPLEMENTATION_TEST }

    companion object {
        private const val RAW_GITHUB_URL = "https://raw.githubusercontent.com/JetBrains/kotlin"

        private const val SPEC_TEST_DATA_PATH = "compiler/tests-spec/testData"
        private const val LINKED_SPEC_TESTS_FOLDER = "linked"
        private const val HELPERS_FOLDER = "helpers"

        const val DEFAULT_BRANCH = "spec-tests"

        fun getBranch() = window.localStorage.getItem("spec-tests-branch") ?: DEFAULT_BRANCH

        fun loadHelperFromRawGithub(fileName: String, testArea: TestArea): Promise<String> {
            return Promise { requestResolve, requestReject ->
                `$`.ajax(getFullHelperPath(testArea, fileName),
                        jQueryAjaxSettings { requestReject(Exception()) }
                ).then({ response: Any?, _: Any ->
                    requestResolve(response.toString())
                })
            }
        }

        fun loadTestFileFromRawGithub(
                path: String,
                testInfo: TestInfo,
                testPlace: TestPlace,
                testArea: TestArea
        ): Promise<SpecTest> = Promise { requestResolve, requestReject ->
            `$`.ajax(getFullTestPath(path),
                    jQueryAjaxSettings { requestReject(Exception()) }
            ).then({ response: Any?, _: Any ->
                requestResolve(SpecTest(testInfo, testPlace, response.toString(), testArea))
            })
        }


        fun loadTestMapFileFromRawGithub(
                path: String,
                testType: TestOrigin,
                testAreasToLoad: List<TestArea>
        ): Promise<Map<TestArea, SectionTestMap>> = Promise { resolve, _ ->
            val resultMap = mutableMapOf<TestArea, SectionTestMap>()
            `$`.`when`(
                    *(testAreasToLoad.associateWith {
                        `$`.ajax(getFullTestMapPath(testType, it, path), jQueryAjaxSettings { })
                                .then({ response: Any?, _: Any ->
                                    resultMap[it] = SectionTestMap(parseJsonText(response.toString()))
                                })
                    }.values.toTypedArray())
            ).then({ _: Any?, _: Any -> resolve(resultMap) }, { resolve(resultMap) })
        }

        private fun getFullTestMapPath(testOrigin: TestOrigin, testArea: TestArea, path: String) =
                when (testOrigin) {
                    TestOrigin.SPEC_TEST -> "{1}/{2}/{3}/{4}/{5}/{6}"
                            .format(RAW_GITHUB_URL, getBranch(), SPEC_TEST_DATA_PATH, testArea.path, LINKED_SPEC_TESTS_FOLDER, path)
                    TestOrigin.IMPLEMENTATION_TEST -> "{1}/{2}/{3}".format(RAW_GITHUB_URL, getBranch(), path)
                }

        private fun getFullHelperPath(testArea: TestArea, helperFile: String) =
                "{1}/{2}/{3}/{4}/{5}/{6}"
                        .format(RAW_GITHUB_URL, getBranch(), SPEC_TEST_DATA_PATH, testArea.path, HELPERS_FOLDER, helperFile)

        private fun getFullTestPath(path: String) = "{1}/{2}/{3}".format(RAW_GITHUB_URL, getBranch(), path)

        private fun parseJsonText(text: String) = Json(JsonConfiguration.Stable).parseJson(text)

        private fun jQueryAjaxSettings(requestReject: (Throwable) -> Unit) = object : JQueryAjaxSettings {
            override var cache: Boolean?
                get() = false
                set(_) {}
            override var type: String?
                get() = "GET"
                set(_) {}
            override val error: ((jqXHR: JQueryXHR, textStatus: String, errorThrown: String) -> Any)?
                get() = { _, _, _ -> requestReject(Exception()) }
        }

    }

    fun loadTestFiles(sectionName: String, sectionsPath: List<String>, testAreasToLoad: Array<TestArea>): Promise<Promise<SpecSection>>
}