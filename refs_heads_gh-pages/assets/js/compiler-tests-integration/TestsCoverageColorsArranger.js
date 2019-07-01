var SpecTestsCoverageColorsArranger = new (function() {
    this.paragraphSelector = [".paragraph", "DL", "UL", "OL"].join(",");
    this.testTypes = {"pos": "positive", "neg": "negative"};
    this.testAreas = {
        "diagnostics": "Front-end diagnostics tests"
    };

    this.getTemplate = function () {
        return "<div class='test-coverage-view'>" +
            "<select name='test-area'>" +
                "<option value='diagnostics'>Front-end diagnostics tests</option>" +
            "</select>" +
            "<select name='test-type'></select>" +
            "<select name='test-number'></select>" +
        "</div>";
    }

    this.insertNumber = function($element, number) {
        $element.prepend("<span class='number-info'>{1}</span>".format(number));
    }

    this.detectUnexpectedBehaviour = function(testsOfType) {
        var unexpectedBehaviour = false;
        for (testNumber in testsOfType) {
            if (testsOfType[testNumber].unexpectedBehaviour) {
                unexpectedBehaviour = true;
            }
            testsOfType[testNumber].cases.forEach(function (testCase) {
                if (testCase.infoElements["UNEXPECTED BEHAVIOUR"]) {
                    unexpectedBehaviour = true;
                }
            });
        }

        return unexpectedBehaviour;
    }

    this.showSentenceCoverage = function($sentence, paragraph, sentenceTestInfo) {
        var self = this;
        var testsByArea = [];
        var unexpectedBehaviour = false;

        for (testArea in sentenceTestInfo) {
            var testNumberByType = {};
            var testsOfArea = sentenceTestInfo[testArea];

            for (testType in testsOfArea) {
                var sum = 0;
                for (sentenceNumber in testsOfArea[testType]) {
                    sum += testsOfArea[testType][sentenceNumber].cases.length;
                }

                testNumberByType[testType] = sum;
                unexpectedBehaviour |= self.detectUnexpectedBehaviour(testsOfArea[testType]);
            }

            var testNumberByTypeInfo = [];
            for (testNumberInfo in testNumberByType) {
                testNumberByTypeInfo.push(testNumberByType[testNumberInfo] + " " + this.testTypes[testNumberInfo]);
            }
            if (testNumberByTypeInfo.length > 0) {
                testsByArea.push("<b>" + this.testAreas[testArea] + "</b>: " + testNumberByTypeInfo.join(", "));
            }
        }

        if (unexpectedBehaviour) {
            $sentence.addClass("unexpected-behaviour")
                .parent().before("<span class='unexpected-behaviour-marker'></span>");
        }
        $sentence.prepend("<span class='coverage-info'>{1}</span>".format(testsByArea.join("<br />")))
            .data("tests", sentenceTestInfo)
            .addClass("covered");
    }

    this.showParagraphCoverage = function(paragraph, paragraphCounter, tests) {
        var self = this;
        var $sentences = $(paragraph).find(".sentence");
        var sentenceCounter = 1;
        var paragraphTests = tests["p-" + paragraphCounter];

        $sentences.each(function() {
            var sentenceTests = {};
            var $sentence = $(this);

            var existingNumberInfo = $sentence.find(".number-info");
            if (existingNumberInfo.length > 0) {
                existingNumberInfo.remove();
            }

            for (testType in paragraphTests) {
                var sentenceTestsByTestTypes = paragraphTests[testType][sentenceCounter];
                if (sentenceTestsByTestTypes) {
                    _.set(sentenceTests, "diagnostics." + testType, sentenceTestsByTestTypes);
                }
            }
            if (_.size(sentenceTests) != 0) {
                self.showSentenceCoverage($sentence, paragraph, sentenceTests);
            }
            self.insertNumber($sentence, sentenceCounter);
            sentenceCounter++;
        });
        this.insertNumber($(paragraph), paragraphCounter);
    }

    this.showCoverage = function(paragraphsInfo, tests) {
        paragraphsInfo.forEach(function (paragraph, paragraphIndex) {
            var $paragraph = $(paragraph.paragraphElement)
            $paragraph.addClass("with-tests");
            this.showParagraphCoverage($paragraph, paragraphIndex + 1, tests);
        }.bind(this))
    }
})()
