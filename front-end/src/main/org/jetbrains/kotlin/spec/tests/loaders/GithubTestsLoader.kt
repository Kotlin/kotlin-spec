package org.jetbrains.kotlin.spec.tests.loaders

import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryXHR
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.utils.format
import kotlin.browser.window
import kotlin.js.Promise

interface GithubTestsLoader {
    companion object {
        private const val RAW_GITHUB_URL = "https://raw.githubusercontent.com/JetBrains/kotlin"

        const val TEST_DATA_PATH = "compiler/tests-spec/testData"
        const val LINKED_SPEC_TESTS_FOLDER = "linked"

        const val DEFAULT_BRANCH = "spec-tests"

        fun getBranch() = window.localStorage.getItem("spec-tests-branch") ?: DEFAULT_BRANCH

        fun loadFileFromRawGithub(path: String, folder: String? = null): Promise<Map<String, String>> {
            return Promise { requestResolve, requestReject ->
                `$`.ajax("{1}/{2}/{3}/diagnostics/{4}/{5}".format(
                        RAW_GITHUB_URL,
                        getBranch(),
                        TEST_DATA_PATH,
                        folder ?: LINKED_SPEC_TESTS_FOLDER,
                        path
                ), object : JQueryAjaxSettings {
                    override var cache: Boolean?
                        get() = false
                        set(_) {}
                    override var type: String?
                        get() = "GET"
                        set(_) {}
                    override val error: ((jqXHR: JQueryXHR, textStatus: String, errorThrown: String) -> Any)?
                        get() = {_, _, _ -> requestReject(Exception()) }
                }).then(
                        { response: Any?, _: Any -> requestResolve(mapOf( "content" to response.toString(), "path" to path )) },
                        { requestReject(Exception()) }
                )
            }
        }
    }

    fun loadTestFiles(sectionName: String, sectionsPath: List<String>, paragraphsInfo: List<Map<String, Any>>): Promise<Promise<Array<out Map<String, String>>>>
}