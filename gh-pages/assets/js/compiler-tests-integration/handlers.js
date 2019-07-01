$(document).ready(function () {
    $("h3, h4, h5").each(function() { SpecTestsLoader.insertLoadIcon($(this)) })
    $(document.body).on("click", ".sentence.covered", function () { SpecTestsViewer.showViewer($(this)) })
    $(document.body).on("change", ".test-coverage-view select[name='test-area']", function () { SpecTestsViewer.onTestAreaChange($(this)) });
    $(document.body).on("change", ".test-coverage-view select[name='test-type']", function () { SpecTestsViewer.onTestTypeChange($(this)) });
    $(document.body).on("change", ".test-coverage-view select[name='test-number']", function () { SpecTestsViewer.onTestNumberChange($(this)) });
    $(document.body).on("click", ".next-testcase", function () { SpecTestsViewer.navigateTestCase($(this), "next"); return false; });
    $(document.body).on("click", ".prev-testcase", function () { SpecTestsViewer.navigateTestCase($(this), "prev"); return false; });
    $(document.body).on("click", ".load-tests", function () { SpecTestsLoader.onLoadIconClick($(this)); return false; });
    $(document.body).on("click", ".set-branch", function () { SpecTestsLoader.onSetBranchIconClick($(this)); return false; });
    $(document.body).on("click", ".loaded-tests", function () { return false; });
})
