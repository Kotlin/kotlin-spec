package org.jetbrains.kotlin.spec.tests.loaders

import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryPromise
import js.externals.jquery.JQueryXHR
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.utils.TestArea
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

                val testsPaths = TestsPaths(customFolder, path, testPathInSpec)
                val queryDiagnostics: JQueryPromise<Unit> = getQuery(TestArea.DIAGNOSTICS, resultMap, testType, requestReject, testsPaths)
                val queryCodegenBox: JQueryPromise<Unit> = getQuery(TestArea.CODEGEN_BOX, resultMap, testType, requestReject, testsPaths)

                `$`.`when`(queryCodegenBox, queryDiagnostics).then({ _: Any?, _: Any ->
                    requestResolve(resultMap)
                })
            }
        }

        class TestsPaths(
                val customFolder: String?,
                val path: String, val testPathInSpec: String?
        )

        private fun getFullPath(testFileType: TestFileType, testArea: TestArea, customFolder: String?, path: String) =
                when (testFileType) {
                    TestFileType.SPEC_TEST -> "{1}/{2}/{3}/{4}/{5}/{6}"
                            .format(RAW_GITHUB_URL, getBranch(), SPEC_TEST_DATA_PATH, testArea.path, customFolder
                                    ?: LINKED_SPEC_TESTS_FOLDER, path)
                    TestFileType.IMPLEMENTATION_TEST -> "{1}/{2}/{3}".format(RAW_GITHUB_URL, getBranch(), path)
                }

        private fun getQuery(
                testArea: TestArea,
                resultMap: MutableMap<String, Any>,
                testType: TestFileType,
                requestReject: (Throwable) -> Unit,
                testsPaths: TestsPaths
        ) = `$`.ajax(getFullPath(testType, testArea, testsPaths.customFolder, testsPaths.path),
                jQueryAjaxSettings { requestReject(Exception()) }).then(
                { response: Any?, _: Any ->
                    resultMap += Pair(testArea.content, response.toString())
                    resultMap += Pair(testArea.contentPath, (testsPaths.testPathInSpec ?: testsPaths.path))
                },
                { }
        )

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