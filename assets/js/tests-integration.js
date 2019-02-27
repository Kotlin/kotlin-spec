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

window.addEventListener("DOMContentLoaded", function() {
    if (location.hash)
        highlightSentence(location.hash.split(':'));

    if (getQueryParam("mode") === "tests_linking") {
        loadJSON(testInfoMapPath, showCoverage);
        document.body.classList.add("tests-linking");
    }
});
