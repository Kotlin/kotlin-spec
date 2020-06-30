package org.jetbrains.kotlin.spec.viewer

import js.externals.jquery.JQuery
import js.externals.jquery.`$`
import org.jetbrains.kotlin.spec.entity.SpecSentence
import org.jetbrains.kotlin.spec.entity.test.SpecTest
import org.jetbrains.kotlin.spec.entity.test.TestCase
import org.jetbrains.kotlin.spec.entity.test.parameters.LinkType
import org.jetbrains.kotlin.spec.entity.test.parameters.TestType
import org.jetbrains.kotlin.spec.entity.test.parameters.testArea.TestArea
import org.jetbrains.kotlin.spec.loader.SpecTestsLoader
import org.jetbrains.kotlin.spec.utils.Popup
import org.jetbrains.kotlin.spec.utils.PopupConfig
import org.jetbrains.kotlin.spec.utils.escapeHtml
import org.jetbrains.kotlin.spec.utils.format
import kotlin.js.Promise
import kotlin.js.json

external fun require(module: String): dynamic

val KotlinPlayground = require("kotlin-playground")

enum class NavigationType { PREV, NEXT }

class SpecTestsViewer {
    companion object {
        private const val TESTS_VIEWER_SELECTOR = ".test-coverage-view"

        private const val TEST_AREA_SELECTOR = "$TESTS_VIEWER_SELECTOR select[name='test-area']"
        private const val TEST_AREA_OPTION_SELECTOR = "$TEST_AREA_SELECTOR option"

        private const val TEST_TYPE_SELECTOR = "$TESTS_VIEWER_SELECTOR select[name='test-type']"
        private const val TEST_TYPE_OPTION_SELECTOR = "$TEST_TYPE_SELECTOR option"
        private const val TEST_TYPE_OPTION_TEMPLATE = "<option value='{1}'>{2}</option>"

        private const val TEST_PRIORITY_SELECTOR = "$TESTS_VIEWER_SELECTOR select[name='test-link-type']"
        private const val TEST_PRIORITY_OPTION_SELECTOR = "$TEST_PRIORITY_SELECTOR option"
        private const val TEST_PRIORITY_OPTION_TEMPLATE = "<option value='{1}'>{2}</option>"

        private const val TEST_NUMBER_SELECTOR = "$TESTS_VIEWER_SELECTOR select[name='test-number']"
        private const val TEST_NUMBER_OPTION_SELECTOR = "$TEST_NUMBER_SELECTOR option"
        private const val TEST_NUMBER_OPTION_TEMPLATE = "<option value='{1}'>{1}: {2}</option>"

        private const val TEST_CODE_WRAPPER_SELECTOR = ".test-code-wrapper"
        private const val TEST_CODE_SELECTOR = "$TESTS_VIEWER_SELECTOR .test-code"
        private const val TEST_CODE_TEMPLATE = "<div class='test-code'>{1}</div>"

        private const val TEST_CASE_INFO_SELECTOR = ".test-case-info"
        private const val TEST_VIEWER_BODY_TEMPLATE = """
                <div class='test-case-info'>
                    <div class='test-title' style='text-align: center;margin: 10px 0;'><b>{1}</b></div>
                    <div style='text-align: center;font-size: 14px;margin-top: 5px;'>
                        <a href='#' class='prev-testcase disabled'>Prev testcase</a> | Test case #<b class='testcase-number'>1</b> | <a href='#' class='next-testcase'>Next testcase</a>
                    </div>
                    <div class='test-code-wrapper' style='margin-top: 15px;'></div>
                </div>
            """
        private const val NEXT_TESTCASE_SELECTOR = ".next-testcase"
        private const val PREV_TESTCASE_SELECTOR = ".prev-testcase"
        private const val TESTCASE_NUMBER_SELECTOR = ".testcase-number"

        private const val MAIN_FUN_CODE = "\nfun main() { println(\"Test passed\") }"
        private const val SAMPLE_WRAP_CODE = "//sampleStart\n{1}\n//sampleEnd"
    }

    private lateinit var currentSpecSentenceTests: SpecSentence
    private lateinit var testPopup: Popup

    private fun insertCode(testCaseCode: TestCase, helperFilesContent: Set<String>? = null) {
        val code = StringBuilder()

        helperFilesContent?.forEach { helperFile ->
            code.append("${helperFile}\n\n")
        }

        code.append(SAMPLE_WRAP_CODE.format(testCaseCode.code))
        code.append(MAIN_FUN_CODE)

        `$`(TEST_CODE_WRAPPER_SELECTOR).html(TEST_CODE_TEMPLATE.format(code.toString().escapeHtml()))

        KotlinPlayground(TEST_CODE_SELECTOR, json(
                "callback" to { testPopup.computeSizes() },
                "onChange" to { testPopup.computeSizes() }
        ))
    }

    private fun showTestCaseCode(specTest: SpecTest, helperFilesContent: Set<String>? = null) {
        `$`(TEST_CASE_INFO_SELECTOR).remove()
        `$`(TESTS_VIEWER_SELECTOR).append(TEST_VIEWER_BODY_TEMPLATE.format(specTest.testInfo.description))

        val testCases = specTest.testCases

        insertCode(testCases.first(), helperFilesContent)

        if (testCases.size == 1) {
            `$`(NEXT_TESTCASE_SELECTOR).addClass("disabled")
        }
    }

    private fun getHelpers(helperNames: Set<String>, testArea: TestArea): Promise<Array<out String>> {
        val helperFilesPromises = mutableListOf<Promise<String>>()

        helperNames.forEach { helperName ->
            helperFilesPromises.add(SpecTestsLoader.loadHelperFile(helperName, testArea))
        }

        return Promise.all(helperFilesPromises.toTypedArray())
    }

    fun showViewer(sentenceElement: JQuery) {
        val tests = sentenceElement.data("tests") as? SpecSentence ?: return
        val sentenceText = "sentence {1}".format(sentenceElement.clone().children(".number-info").text())

        currentSpecSentenceTests = tests
        testPopup = Popup(
                PopupConfig(
                        title = "Test coverage of $sentenceText",
                        content = SpecCoverageHighlighter.TEMPLATE,
                        width = 800,
                        height = 300
                )
        ).apply { open() }

        `$`(TEST_AREA_OPTION_SELECTOR).each { _, el ->
            val testArea = `$`(el)


            if (tests.getTests(TestArea.getByShortName(testArea.attr("value"))) == null) {
                testArea.remove()
            }
        }

        `$`(TEST_AREA_SELECTOR).`val`(`$`(TEST_AREA_OPTION_SELECTOR).eq(0).`val`().toString())

        onTestAreaChange()
    }

    fun onTestAreaChange() {
        val testArea = TestArea.getByShortName(`$`(TEST_AREA_SELECTOR).`val`().toString().apply { if (isEmpty()) return })
        currentSpecSentenceTests.getTests(testArea) ?: return
        TestType.values().forEach { testType ->
            val tests = currentSpecSentenceTests.getTests(testArea, testType) ?: return@forEach
            if (tests.isNotEmpty())
                `$`(TEST_TYPE_SELECTOR).append(TEST_TYPE_OPTION_TEMPLATE.format(testType.shortName, testType.name))
        }

        `$`(TEST_TYPE_SELECTOR).show().`val`(`$`(TEST_TYPE_OPTION_SELECTOR).eq(0).`val`().toString())

        onTestTypeChange()
    }

    fun onTestTypeChange() {
        val testType = TestType.getByShortName(`$`(TEST_TYPE_SELECTOR).`val`().toString().apply { if (isEmpty()) return })
        val testArea = TestArea.getByShortName(`$`(TEST_AREA_SELECTOR).`val`().toString())

        val tests = currentSpecSentenceTests.getTests(testArea, testType)
                ?: return

        `$`(TEST_PRIORITY_OPTION_SELECTOR).each { _, el ->
            val testPriority = `$`(el)


            if (tests.getTestsByTestPriority(LinkType.valueOf(testPriority.attr("value"))).isEmpty()) {
                testPriority.remove()
            }
        }

        `$`(TEST_PRIORITY_SELECTOR).`val`(`$`(TEST_PRIORITY_OPTION_SELECTOR).eq(0).`val`().toString())

        onTestPriorityChange()
    }

    private fun List<SpecTest>.getTestsByTestPriority(attr: LinkType): List<SpecTest> {
        return this.filter { test -> test.testInfo.linkType == attr }
    }

    fun onTestPriorityChange() {
        val testPriority = LinkType.valueOf(`$`(TEST_PRIORITY_SELECTOR).`val`().toString().apply { if (isEmpty()) return })
        val testArea = TestArea.getByShortName(`$`(TEST_AREA_SELECTOR).`val`().toString())
        val testType = TestType.getByShortName(`$`(TEST_TYPE_SELECTOR).`val`().toString())

        val tests = currentSpecSentenceTests.getTests(testArea, testType, testPriority) ?: return


        `$`(TEST_NUMBER_SELECTOR).empty()
        tests.forEach { test ->
            `$`(TEST_NUMBER_SELECTOR).append(TEST_NUMBER_OPTION_TEMPLATE.format(test.testInfo.testNumber, test.testInfo.description))
        }

        `$`(TEST_NUMBER_SELECTOR).show().`val`(`$`(TEST_NUMBER_OPTION_SELECTOR).eq(0).`val`().toString())

        onTestNumberChange()
    }

    fun onTestNumberChange() {
        val testNumber = `$`(TEST_NUMBER_SELECTOR).`val`().toString().apply { if (isEmpty()) return }.toInt()
        val testPriority = LinkType.valueOf(`$`(TEST_PRIORITY_SELECTOR).`val`().toString())
        val testArea = TestArea.getByShortName(`$`(TEST_AREA_SELECTOR).`val`().toString())
        val testType = TestType.getByShortName(`$`(TEST_TYPE_SELECTOR).`val`().toString())

        val specTest = currentSpecSentenceTests.getTest(testArea, testType, testPriority, testNumber) ?: return

        if (specTest.testInfo.helpers.isNotEmpty()) {
            getHelpers(specTest.testInfo.helpers, testArea).then {
                showTestCaseCode(specTest, it.toSet())
            }
        } else {
            showTestCaseCode(specTest)
        }
    }

    fun navigateTestCase(navigationLink: JQuery, navigationType: NavigationType) {
        if (navigationLink.hasClass("disabled")) return

        val testArea = TestArea.getByShortName(`$`(TEST_AREA_SELECTOR).`val`().toString())
        val testType = TestType.getByShortName(`$`(TEST_TYPE_SELECTOR).`val`().toString())
        val testNumber = `$`(TEST_NUMBER_SELECTOR).`val`().toString().toInt()
        val testPriority = LinkType.valueOf(`$`(TEST_PRIORITY_SELECTOR).`val`().toString())

        val specTest = currentSpecSentenceTests.getTest(testArea, testType, testPriority, testNumber) ?: return

        val currentNumber = `$`(TESTCASE_NUMBER_SELECTOR).text().toInt()

        val caseNumber = if (navigationType == NavigationType.PREV) currentNumber - 2 else currentNumber
        val testCases = specTest.testCases

        `$`(TESTCASE_NUMBER_SELECTOR).html((caseNumber + 1).toString())


        if (specTest.testInfo.helpers.isNotEmpty()) {
            getHelpers(specTest.testInfo.helpers, testArea).then {
                insertCode(testCases[caseNumber], it.toSet())
            }
        } else {
            insertCode(testCases[caseNumber])
        }
        if (caseNumber + 1 >= testCases.size) {
            `$`(NEXT_TESTCASE_SELECTOR).addClass("disabled")
        } else if (navigationType == NavigationType.PREV && `$`(NEXT_TESTCASE_SELECTOR).hasClass("disabled")) {
            `$`(NEXT_TESTCASE_SELECTOR).removeClass("disabled")
        }
        if (navigationType == NavigationType.NEXT && `$`(PREV_TESTCASE_SELECTOR).hasClass("disabled")) {
            `$`(PREV_TESTCASE_SELECTOR).removeClass("disabled")
        } else if (caseNumber + 1 == 1) {
            `$`(PREV_TESTCASE_SELECTOR).addClass("disabled")
        }
    }
}

