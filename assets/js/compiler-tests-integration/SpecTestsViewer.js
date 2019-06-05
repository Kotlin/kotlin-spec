var SpecTestsViewer = new (function() {
    this.currentSentenseTests = null;
    this.testPopup = null;
    this.showViewer = function($sentenseElement) {
        var tests = $sentenseElement.data("tests");
        var sentenceText = $sentenseElement.clone().children(".number-info, .coverage-info").remove().end().text();

        this.currentSentenseTests = tests;
        this.testPopup = new Popup({title: "Test coverage: «" + sentenceText + "»", content: SpecTestsCoverageColorsArranger.getTemplate(), width: 800, height: 300});
        $(".test-coverage-view select[name='test-area'] option").each(function () {
            var $testArea = $(this);
            if ($testArea.attr("value") && !($testArea.attr("value") in tests)) {
                $testArea.remove();
            }
        });
        $(".test-coverage-view select[name='test-area']").val($(".test-coverage-view select[name='test-area'] option").eq(0).val());
        $(".test-coverage-view select[name='test-area']").change();
    }

    this.onTestAreaChange = function ($testArea) {
        if ($testArea.val()) {
            var tests = this.currentSentenseTests[$(".test-coverage-view select[name='test-area']").val()];
            var types = {"pos": "Positive", "neg": "Negative"};

            Object.keys(tests).reverse().forEach(function (testType) {
                $(".test-coverage-view select[name='test-type']").append("<option value='" + testType + "'>" + types[testType] + "</option>");
            })
            $(".test-coverage-view select[name='test-type']").show();
            $(".test-coverage-view select[name='test-type']").val($(".test-coverage-view select[name='test-type'] option").eq(0).val());
            $(".test-coverage-view select[name='test-type']").change();
        }
    }

    this.onTestTypeChange = function ($testArea) {
        if ($testArea.val()) {
            var tests = this.currentSentenseTests[$(".test-coverage-view select[name='test-area']").val()][$(".test-coverage-view select[name='test-type']").val()];
            $(".test-coverage-view select[name='test-number']").empty();
            for (testNumber in tests) {
                $(".test-coverage-view select[name='test-number']").append("<option value='" + testNumber + "'>" + testNumber + ": " + tests[testNumber].description + "</option>");
            }
            $(".test-coverage-view select[name='test-number']").show();
            $(".test-coverage-view select[name='test-number']").val($(".test-coverage-view select[name='test-number'] option").eq(0).val());
            $(".test-coverage-view select[name='test-number']").change();
        }
    }

    this.insertCode = function (testCase, helperFilesContent) {
        var code = ""

        if (helperFilesContent) {
            helperFilesContent.forEach(function (helperFile) {
                code += helperFile.content + "\n\n";
            })
        }

        code += "//sampleStart\n" + testCase.code + "\n//sampleEnd";
        code += "\nfun main() { println(\"Test passed\") }";

        $(".test-code-wrapper").html("<div class='test-code'>" + code.escapeHtml() + "</div>");

        KotlinPlayground('.test-coverage-view .test-code', {
            callback: this.testPopup.calculate_sizes,
            onChange: this.testPopup.calculate_sizes
        });
    }

    this.showTestCaseCode = function (test, helperFilesContent) {
        $(".test-case-info").remove();
        $(".test-coverage-view").append("<div class='test-case-info'>" +
            "<div class='test-title' style='text-align: center;margin: 10px 0;'><b>" + test.description + "</b></div>" +
            "<div style='text-align: center;font-size: 14px;margin-top: 5px;'><a href='#' class='prev-testcase disabled'>Prev testcase</a> | Test case #<b class='testcase-number'>1</b> | <a href='#' class='next-testcase'>Next testcase</a></div>" +
            "<div class='test-code-wrapper' style='margin-top: 15px;'></div>" +
            "</div>");

        this.insertCode(test.cases[0], helperFilesContent);

        if (test.cases.length == 1) {
            $(".next-testcase").addClass("disabled");
        }
    }

    this.getHelpers = function (helperNames) {
        var helperFilesPromises = [];
        helperNames.forEach(function (helperName) {
            helperFilesPromises.push(SpecTestsLoader.loadHelperFile(helperName));
        })
        return Promise.all(helperFilesPromises);
    }

    this.onTestNumberChange = function ($testNumber) {
        if ($testNumber.val() != 0) {
            var tests = this.currentSentenseTests[$(".test-coverage-view select[name='test-area']").val()][$(".test-coverage-view select[name='test-type']").val()][$testNumber.val()];
            if (tests.helpers) {
                this.getHelpers(tests.helpers).then(this.showTestCaseCode.bind(this, tests))
            } else {
                this.showTestCaseCode(tests, null);
            }
        }
    }

    this.navigateTestCase = function ($navigationLink, navigationType) {
        if ($navigationLink.hasClass("disabled"))
            return;
        var tests = this.currentSentenseTests[$(".test-coverage-view select[name='test-area']").val()][$(".test-coverage-view select[name='test-type']").val()][$(".test-coverage-view select[name='test-number']").val()];
        var currentNumber = +$(".testcase-number").text();
        var targetTestCase = navigationType == "prev" ? currentNumber - 2 : currentNumber

        $(".testcase-number").html(targetTestCase + 1);

        if (tests.helpers) {
            this.getHelpers(tests.helpers).then(this.insertCode.bind(this, tests.cases[targetTestCase]))
        } else {
            this.insertCode(tests.cases[targetTestCase], null);
        }

        if (targetTestCase + 1 >= tests.cases.length) {
            $(".next-testcase").addClass("disabled");
        }
        if (navigationType == "next" && $(".prev-testcase").hasClass("disabled")) {
            $(".prev-testcase").removeClass("disabled");
        }
        if (targetTestCase + 1 == 1) {
            $(".prev-testcase").addClass("disabled");
        }
        if (navigationType == "prev" && $(".next-testcase").hasClass("disabled")) {
            $(".next-testcase").removeClass("disabled");
        }
    }
})()
