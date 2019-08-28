package org.jetbrains.kotlin.spec

import kotlin.browser.document
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.tests.NavigationType
import org.jetbrains.kotlin.spec.tests.SpecTestsLoader
import org.jetbrains.kotlin.spec.tests.SpecTestsViewer
import org.jetbrains.kotlin.spec.tests.loaders.GithubTestsLoaderType

fun main() {
    val specTestsLoader = SpecTestsLoader(GithubTestsLoaderType.USING_TESTS_MAP_FILE)
    val specTestsViewer = SpecTestsViewer()

    `$`("h3, h4, h5").each { _, el ->
        SpecTestsLoader.insertLoadIcon(`$`(el))
    }

    document.body?.let { `$`(it) }?.run {
        on("click", ".sentence.covered") { e, _ ->
            specTestsViewer.showViewer(`$`(e.currentTarget))
        }
        on("change", ".test-coverage-view select[name='test-area']") { e, _ ->
            specTestsViewer.onTestAreaChange()
        }
        on("change", ".test-coverage-view select[name='test-type']") { e, _ ->
            specTestsViewer.onTestTypeChange()
        }
        on("change", ".test-coverage-view select[name='test-number']") { e, _ ->
            specTestsViewer.onTestNumberChange()
        }
        on("click", ".prev-testcase") { e, _ ->
            specTestsViewer.navigateTestCase(`$`(e.currentTarget), NavigationType.PREV)
            false
        }
        on("click", ".next-testcase") { e, _ ->
            specTestsViewer.navigateTestCase(`$`(e.currentTarget), NavigationType.NEXT)
            false
        }
        on("click", ".load-tests") { e, _ ->
            specTestsLoader.onLoadIconClick(`$`(e.currentTarget))
            false
        }
        on("click", ".set-branch") { _, _ ->
            SpecTestsLoader.onSetBranchIconClick()
            false
        }
        on("click", ".loaded-tests") { _, _ -> false }
    }
}
