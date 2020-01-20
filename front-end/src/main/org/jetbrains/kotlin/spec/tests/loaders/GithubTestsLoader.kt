package org.jetbrains.kotlin.spec.tests.loaders

import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryPromise
import js.externals.jquery.JQueryXHR
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.utils.TestTypeInfo
import org.jetbrains.kotlin.spec.utils.format
import kotlin.browser.window
import kotlin.js.Promise

interface GithubTestsLoader {
    enum class TestFileType { SPEC_TEST, IMPLEMENTATION_TEST }

    companion object {
        private const val RAW_GITHUB_URL = "https://raw.githubusercontent.com/JetBrains/kotlin"

        const val SPEC_TEST_DATA_PATH = "compiler/tests-spec/testData"
        const val LINKED_SPEC_TESTS_FOLDER = "linked"

        const val DEFAULT_BRANCH = "spec-tests"

        fun getBranch() = window.localStorage.getItem("spec-tests-branch") ?: DEFAULT_BRANCH

        fun loadFileFromRawGithub(
                path: String,
                testPathInSpec: String? = null,
                testType: TestFileType,
                customFolder: String? = null
        ): Promise<Map<String, Any>> {

            return Promise { requestResolve, requestReject ->

                val resultMap = mutableMapOf<String, Any>()

                fun getQuery(testTypeInfo: TestTypeInfo): JQueryPromise<Unit> {
                    fun getFullPath(testFileType: TestFileType, testTypeInfo: TestTypeInfo, customFolder: String?, path: String): String {
                        return when (testFileType) {
                            TestFileType.SPEC_TEST -> "{1}/{2}/{3}/{4}/{5}/{6}"
                                    .format(RAW_GITHUB_URL, getBranch(), SPEC_TEST_DATA_PATH, testTypeInfo.path, customFolder
                                            ?: LINKED_SPEC_TESTS_FOLDER, path)
                            TestFileType.IMPLEMENTATION_TEST -> "{1}/{2}/{3}".format(RAW_GITHUB_URL, getBranch(), path)
                        }
                    }

                    return `$`.ajax(getFullPath(testType, testTypeInfo, customFolder, path),
                            jQueryAjaxSettings { requestReject(Exception()) }).then(
                            { response: Any?, _: Any ->
                                resultMap += Pair(testTypeInfo.content, response.toString())
                                resultMap += Pair(testTypeInfo.contentPath, (testPathInSpec ?: path))
                            },
                            { }
                    )
                }

                val q1: JQueryPromise<Unit> = getQuery(TestTypeInfo.DIAG)
                val q2: JQueryPromise<Unit> = getQuery(TestTypeInfo.BOX)

                `$`.`when`(q2, q1).then({ _: Any?, _: Any ->
                    requestResolve(resultMap)
                })

            }


        }


        private fun jQueryAjaxSettings(requestReject: (Throwable) -> Unit): JQueryAjaxSettings {
            return object : JQueryAjaxSettings {
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
    }


    fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, Any>>>>
}