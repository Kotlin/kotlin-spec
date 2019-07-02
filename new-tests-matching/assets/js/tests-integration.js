var testInfoMapPath = "/kotlin-spec/assets/data/tests-map.json";
var githubTestsLink = "https://github.com/JetBrains/kotlin/tree/master/compiler/tests-spec/testData";

var testAreas = ["diagnostics", "psi", "codegen"];
var paragraphSelector = [".paragraph", "DL", "UL", "OL"].join(",");

function getQueryParam(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function loadJSON(url, callback) {
    var request = new XMLHttpRequest();
    request.overrideMimeType("application/json");
    request.open("GET", url, true);
    request.onreadystatechange = function () {
        if (request.readyState === 4 && request.status === 200) {
            callback(JSON.parse(request.responseText));
        }
    };
    request.send(null);
}

function highlightSentence(hashComponents) {
    var section = hashComponents[0];
    var paragraph = hashComponents[1];
    var sentence = hashComponents[2];

    var sectionElement = document.querySelector(section);
    var nextSibling = sectionElement.nextElementSibling;
    var counter = 1;

    while (nextSibling) {
        if (nextSibling.matches(paragraphSelector) && counter++ == paragraph) {
            nextSibling.scrollIntoView();
            var sentenceElement = nextSibling.getElementsByClassName('sentence')[sentence - 1];
            sentenceElement.style.backgroundColor = 'yellow';
            break;
        }
        nextSibling = nextSibling.nextElementSibling;
    }
}

function insertNumberInfo(element, elementNumber) {
    var numberInfoSpan = document.createElement("span");
    numberInfoSpan.setAttribute("class", "number-info");
    numberInfoSpan.innerHTML = elementNumber;
    element.insertBefore(numberInfoSpan, element.firstChild);
}

function createTestLinkElement(testArea, paragraphNumber, sectionId) {
    var numberInfoSpan = document.createElement("a");
    numberInfoSpan.innerHTML = testArea;
    numberInfoSpan.setAttribute("target", "_blank");
    numberInfoSpan.setAttribute("href", githubTestsLink + "/" + testArea + "/linked/" + sectionId + "/p-" + paragraphNumber);
    return numberInfoSpan
}

function insertLinksToGithub(paragraph, paragraphNumber, paragraphObject, sectionId) {
    var testTypesPresented = {};
    testAreas.forEach(function (testArea) {
        Object.keys(paragraphObject).forEach(function (sentenceNumber) {
            if (testArea in paragraphObject[sentenceNumber]) {
                testTypesPresented[testArea] = true;
            }
        });
    });

    if (Object.keys(testTypesPresented).length !== 0) {
        var numberInfoSpan = document.createElement("span");
        numberInfoSpan.setAttribute("class", "test-links");
        testAreas.forEach(function (testArea) {
            if (testTypesPresented[testArea]) {
                numberInfoSpan.prepend(createTestLinkElement(testArea, paragraphNumber, sectionId))
            }
        });

        numberInfoSpan.prepend("Tests: ");

        paragraph.insertBefore(numberInfoSpan, paragraph.firstChild);
    }
}

function detectUnexpectedBehaviour(testsOfType) {
    var unexpectedBehaviour = false;
    Object.keys(testsOfType).forEach(function (testNumber) {
        if (testsOfType[testNumber].unexpectedBehaviour) {
            unexpectedBehaviour = true;
        }
        var testCases = testsOfType[testNumber].cases;
        if (testCases) {
            testCases.forEach(function (testCase) {
                if (testCase.unexpectedBehaviour) {
                    unexpectedBehaviour = true;
                }
            });
        }
    });

    return unexpectedBehaviour;
}

function showSentenceCoverage(sentenceElement, sentenceTestInfo) {
    var testTypes = {"pos": "positive", "neg": "negative"};

    sentenceElement.classList.add("covered");

    var testsByArea = [];
    var unexpectedBehaviour = false;

    Object.keys(sentenceTestInfo).forEach(function (testArea) {
        var testNumberByType = {};
        var testsOfArea = sentenceTestInfo[testArea];
        Object.keys(testsOfArea).forEach(function (testType) {
            testNumberByType[testType] = Object.keys(testsOfArea[testType]).length;
            unexpectedBehaviour |= detectUnexpectedBehaviour(testsOfArea[testType]);
        });

        var testNumberByTypeInfo = [];
        Object.keys(testNumberByType).forEach(function (testNumberInfo) {
            testNumberByTypeInfo.push(testNumberByType[testNumberInfo] + " " + testTypes[testNumberInfo]);
        });
        if (testNumberByTypeInfo.length > 0) {
            testsByArea.push("<b>" + testArea.toLocaleUpperCase() + "</b>: " + testNumberByTypeInfo.join(", "));
        }
    });

    var coverageInfoSpan = document.createElement("span");
    coverageInfoSpan.classList.add("coverage-info");
    if (unexpectedBehaviour) {
        var unexpectedBehaviourSpan = document.createElement("span");
        unexpectedBehaviourSpan.classList.add("unexpected-behaviour-marker");
        sentenceElement.parentNode.insertBefore(unexpectedBehaviourSpan, sentenceElement);
        sentenceElement.classList.add("unexpected-behaviour")
    }
    coverageInfoSpan.innerHTML = testsByArea.join("<br />");
    sentenceElement.prepend(coverageInfoSpan);
}

function showParagraphCoverage(paragraph, paragraphs, paragraphCounter, sectionId) {
    var sentenceElements = paragraph.querySelectorAll(".sentence");
    var paragraphObject = paragraphs ? paragraphs[paragraphCounter] : null;
    var sentenceCounter = 1;

    Array.from(sentenceElements).forEach(function(sentenceElement) {
        if (paragraphObject && sentenceCounter in paragraphObject)
            showSentenceCoverage(sentenceElement, paragraphObject[sentenceCounter], paragraph);
        insertNumberInfo(sentenceElement, sentenceCounter);
        sentenceCounter++;
    });
    insertNumberInfo(paragraph, paragraphCounter);
    if (paragraphObject != null)
        insertLinksToGithub(paragraph, paragraphCounter, paragraphObject, sectionId);
}

function showCoverage(specTestsMap) {
    var newSectionTags = ["H1", "H2", "H3"];
    var sections = document.querySelectorAll(newSectionTags.join(","));

    Array.from(sections).forEach(function(sectionElement) {
        var nextSibling = sectionElement.nextElementSibling;
        var sectionIdObject = sectionElement.attributes.getNamedItem("id");

        if (!sectionIdObject)
            return;

        var sectionId = sectionIdObject.value;
        var paragraphs = specTestsMap[sectionId];
        var paragraphCounter = 1;

        while (nextSibling) {
            if (newSectionTags.indexOf(nextSibling.tagName) !== -1)
                break;

            var isParagraph = nextSibling.matches(paragraphSelector);
            var childParagraph = nextSibling.querySelector(".paragraph");

            if (isParagraph || childParagraph) {
                showParagraphCoverage(childParagraph || nextSibling, paragraphs, paragraphCounter, sectionId);
                paragraphCounter++;
            }
            nextSibling = nextSibling.nextElementSibling;
        }
    })
}

$(document).ready(function () {
    if (location.hash)
        highlightSentence(location.hash.split(':'));

    $("h3, h4, h5").each(function() {
        var $this = $(this);
        $this.before(
            '<a href="#" data-id="' + $this.attr("id") + '" data-type="' + $this.prop("tagName").toLowerCase() + '" class="load-tests-info" title="Show tests coverage"><img src="./assets/images/test.png" />'
        );
    })

    if (getQueryParam("mode") === "tests_linking") {
        loadJSON(testInfoMapPath, showCoverage);
        document.body.classList.add("tests-linking");
    }
})

String.format = function() {
    var s = arguments[0];
    for (var i = 0; i < arguments.length - 1; i++) {
        var reg = new RegExp("\\{" + i + "\\}", "gm");
        s = s.replace(reg, arguments[i + 1]);
    }
    return s;
}

var testCasePatterns = {
    TEST_CASE_CODE_REGEX: "(?{1}[\s\S]*?)",
    testCaseInfoElementsRegex: "(?{1}{2}${SpecTestCaseInfoElementType.TESTCASE_NUMBER.name.withSpaces()}:{3}*?\n)",
    testCaseInfoRegex: "$TEST_CASE_CODE_REGEX(?<{1}>(?:$directiveRegex)|$)",
    testCaseInfoSingleLineRegex:
        SINGLE_LINE_COMMENT_REGEX.format(
            testCaseInfoElementsRegex.format("infoElementsSL", "", "\s*.")
        ) + testCaseInfoRegex.format("codeSL", "nextDirectiveSL"),
    testCaseInfoMultilineRegex =
    MULTILINE_COMMENT_REGEX.format(
        testCaseInfoElementsRegex.format("infoElementsML", " $ASTERISK_REGEX ", "[\s\S]")
    ) + testCaseInfoRegex.format("codeML", "nextDirectiveML"),
    testCaseInfoPattern: "(?:$testCaseInfoSingleLineRegex)|(?:$testCaseInfoMultilineRegex)",
    testCaseNumberPattern: "([1-9]\d*)(,\s*[1-9]\d*)*"
}

function parseTest(data) {
    var matcher = testCaseInfoPattern.matcher(fileContent);
    while (matcher.find(startFind)) {
        var nextDirective = matcher.group("nextDirectiveSL") ?: matcher.group("nextDirectiveML");
        var range = matcher.start()..matcher.end() - nextDirective.length;
        var code = matcher.group("codeSL") ?: matcher.group("codeML");
    }
}

$(document.body).on("click", ".load-tests-info", function () {
    var $this = $(this);
    var path = [$this.prevAll('h2').first().attr("id")];

    if ($this.data("type") == "h4" || $this.data("type") == "h5") {
        path.push($this.prevAll('h3').first().attr("id"));
    }
    if ($this.data("type") == "h5") {
        path.push($this.prevAll('h4').first().attr("id"));
    }
    var currentSection = $this.data("id");
    var pathAsString = path.join("/");

    // var tryQ = function (paragraph, type, sentense, number) {
    //     $.get({
    //         url: 'https://raw.githubusercontent.com/JetBrains/kotlin/spec-tests-draft/compiler/tests-spec/testData/diagnostics/linked/' + pathAsString + '/p-' + paragraph + '/' + type + '/' + sentense + '.' + number + '.kt',
    //         success: function (data) {
    //             console.log(data);
    //         }
    //     })
    // }
    //
    // tryQ(1, "pos", 1, 1);

    $.get({
        url: "https://api.github.com/repos/JetBrains/Kotlin/contents/compiler/tests-spec/testData/diagnostics/linked/" + pathAsString + "?ref=spec-tests-draft",
        success: function (data) {
            var url = _.result(_.find(data, function(obj) {
                return obj.name === currentSection;
            }), 'git_url');
            $.get({
                url: url + "?recursive=1",
                success: function (data) {
                    data.tree.forEach(function (element) {
                        if (RegExp('^p-[1-9]\d*\/(pos|neg)\/[1-9]\d*\.[1-9]\d*\.kt$').test(element.path)) {
                            $.get({
                                url: 'https://raw.githubusercontent.com/JetBrains/kotlin/spec-tests-draft/compiler/tests-spec/testData/diagnostics/linked/' + pathAsString + '/' + currentSection + '/' + element.path,
                                success: function (data) {
                                    console.log(data);
                                }
                            })
                        }
                    })
                }
            })
        }
    })
    return false
})
