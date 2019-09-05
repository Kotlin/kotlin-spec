package org.jetbrains.kotlin.spec.tests.loaders

import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryXHR
import js.externals.jquery.`$`
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
                additionalInfo: Map<String, Any>? = null,
                customFolder: String? = null
        ): Promise<Map<String, Any>> {
            return Promise { requestResolve, requestReject ->
                val fullPath = when (testType) {
                    TestFileType.SPEC_TEST -> "{1}/{2}/{3}/diagnostics/{4}/{5}"
                            .format(RAW_GITHUB_URL, getBranch(), SPEC_TEST_DATA_PATH, customFolder ?: LINKED_SPEC_TESTS_FOLDER, path)
                    TestFileType.IMPLEMENTATION_TEST -> "{1}/{2}/{3}".format(RAW_GITHUB_URL, getBranch(), path)
                }

                `$`.ajax(fullPath, object : JQueryAjaxSettings {
                        override var cache: Boolean?
                            get() = false
                            set(_) {}
                        override var type: String?
                            get() = "GET"
                            set(_) {}
                        override val error: ((jqXHR: JQueryXHR, textStatus: String, errorThrown: String) -> Any)?
                            get() = {_, _, _ -> requestReject(Exception()) }
                    }
                ).then(
                        { response: Any?, _: Any ->
                            val resultMap = mapOf<String, Any>("content" to response.toString(), "path" to (testPathInSpec ?: path))

                            requestResolve(if (additionalInfo != null) resultMap + additionalInfo else resultMap)
                        },
                        { requestReject(Exception()) }
                )
            }
        }
    }

    fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, Any>>>>
}